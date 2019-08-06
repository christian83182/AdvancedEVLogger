import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

class DataModel {

    private Map<String,ChargerObject> chargers;
    private Application app;
    private Map<Long,Double> generalChargingLog;

    DataModel(Application app){
        this.chargers = new HashMap<>();
        this.app = app;
        this.generalChargingLog = new HashMap<>();
    }

    /**
     * Downloads the details for all the chargers in the 'ids' list in DataModel
     */
    public synchronized void downloadIdData() {
        //Clear any previous values
        chargers.replaceAll((k,v) -> null);
        System.out.println();

        //Create a new thread to run this on so that it can happen concurrently
        Thread webThread = new Thread(() -> {
            //create these objects outside the for loop so they can be used to optimize downloads.
            String previousID = "";
            HtmlPage doc = null;
            long startTime = System.currentTimeMillis();

            //Iterate over all ids
            Integer counter = 1;
            for(String id : getIds()){
                NotificationLogger.logger.addToLog("Downloading data for '" + id +
                        "'    (" + counter +"/" + getIds().size()+ ")");

                //Ids come in the form of "xxxx:x", where xxxx is the id and x is the designator
                String realID = id.split(":")[0];
                Integer designator = Integer.parseInt(id.split(":")[1]);
                ChargerObject newCharger = new ChargerObject(realID,designator);

                //If the previous charger has a different id then the page must be re-downloaded
                try {
                    if(!previousID.equals(newCharger.getId())){
                        doc = newCharger.getHtmlPage(new WebClient());
                    }
                    newCharger.fetchDetailsFromPage(doc);
                    counter++;

                    //Put it in the internal map and update the previous charger
                    chargers.put(id,newCharger);
                    previousID = newCharger.getId();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //add it to the menu panel as they're added. Since this is in a new thread, it must be run using 'invokeLater'
                SwingUtilities.invokeLater(() -> {
                    String itemTitle = newCharger.getId() +":" + newCharger.getDesignator() + " - " + newCharger.getName();
                    app.getMenuPanel().addMenuItem(itemTitle);
                });
            }
            NotificationLogger.logger.addToLog("Download Completed in " + (System.currentTimeMillis() - startTime)/1000 +"s");
        });
        //start the new thread.
        webThread.start();
    }

    public synchronized void rebuiltGeneralModel(){
        this.generalChargingLog.clear();
        for(ChargerObject charger : chargers.values()){
            if(isValidCharger(charger)){
                for(Long time : charger.getLogTimes()){
                    if(charger.getEntryInLog(time)){
                        if(generalChargingLog.containsKey(time)){
                            generalChargingLog.put(time, generalChargingLog.get(time)+1);
                        } else {
                            generalChargingLog.put(time,1.0);
                        }
                    } else {
                        if(!generalChargingLog.containsKey(time)){
                            generalChargingLog.put(time, 0.0);
                        }
                    }
                }
            }
        }

        if(app.getMenuPanel().getSelectedOption().equals("Show Moving Average")
                && !getGeneralLogKey().isEmpty()){
            List<Long> times = new ArrayList<>(getGeneralLogKey());
            Map<Long,Double> newMap = new HashMap<>();
            Collections.sort(times);
            Integer movingAverageWidth = app.getMenuPanel().getMovingAverageWidth()/2;
            for(int i =movingAverageWidth; i < times.size()-movingAverageWidth; i++){
                double sumCount = 0.0;
                //double sumTimes = 0.0;
                for(int j = -movingAverageWidth; j <= movingAverageWidth; j++){
                    sumCount += getGeneralLogEntry(times.get(i+j));
                    //sumTimes += times.get(i+j);
                }
                double averageCount = sumCount/(movingAverageWidth*2 +1);
                //long averageTime = (long)(sumTimes/(movingAverageWidth*2 +1));
                newMap.put(times.get(i),averageCount);
            }
            this.generalChargingLog = newMap;
        }
    }

    public void repairDataModel(){
        if(!chargers.isEmpty()){
            for(ChargerObject charger : chargers.values()){
                List<Long> times = new ArrayList<>(charger.getLogTimes());
                Collections.sort(times);

                //Iterate over all chargers where there are 3 chargers or more
                Long previousInterval = times.get(1) - times.get(0);
                for(int i = 2; i < times.size(); i++){
                    Long currentInterval = times.get(i) - times.get(i-1);
                    Long intervalChange = currentInterval - previousInterval;

                    //if the current interval is not within 10s of the previous interval, then correct.
                    if(intervalChange > 5000 || intervalChange < -5000){
                        Long numOfNewPoints = Math.round((double)currentInterval/(double)previousInterval)-2;
                        Long newInterval = currentInterval/numOfNewPoints;

                        //iterate over the number of new points, and add them as log entries
                        Long leftTime = times.get(i-1);
                        Long rightTime = times.get(i);
                        for(int j = 1; j <= numOfNewPoints; j++){
                            //make half of them the first value, and the other half the second value
                            if(j < numOfNewPoints/2){
                                charger.addLogEntry(leftTime + j*newInterval, charger.getEntryInLog(leftTime));
                            } else
                                charger.addLogEntry(leftTime + j*newInterval, charger.getEntryInLog(rightTime));
                        }
                    } else {
                        previousInterval = times.get(i) - times.get(i-1);
                    }
                }
            }
            this.rebuiltGeneralModel();
            app.repaint();
        }

    }

    public boolean isValidCharger(ChargerObject charger){
        if(app.getMenuPanel().isShowRapid() && charger.isRapid()){
            return true;
        } else if(app.getMenuPanel().isShowFast() && !charger.isRapid()){
            return true;
        }
        return false;
    }

    public synchronized Set<String> getIds(){
        return new TreeSet<>(chargers.keySet());
    }

    public synchronized ChargerObject getCharger(String id){
        return chargers.get(id);
    }

    public synchronized void clearChargers(){
        chargers.clear();
    }

    public synchronized void addId(String newId){
        chargers.put(newId,null);
    }

    public synchronized void addCharger(String chargerId, ChargerObject charger){
        chargers.put(chargerId,charger);
    }

    public synchronized Set<Long> getGeneralLogKey(){
        return generalChargingLog.keySet();
    }

    public synchronized Double getGeneralLogEntry(Long time){
        return generalChargingLog.get(time);
    }

    public synchronized void clearGeneralLogEntries(){
        generalChargingLog.clear();
    }

    public synchronized void addToGeneralLog(Long time, Double quantity){
        generalChargingLog.put(time,quantity);
    }
}
