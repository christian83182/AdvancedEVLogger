import com.gargoylesoftware.htmlunit.html.HtmlPage;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

class DataModel {

    private Map<String,ChargerObject> chargers;
    private Application app;
    private Map<Long,Integer> generalChargingLog;

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
                        doc = newCharger.getHtmlPage(app.getWebClient());
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
                            generalChargingLog.put(time,1);
                        }
                    }
                }
            }
        }
    }

    private boolean isValidCharger(ChargerObject charger){
        return true;
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

    public synchronized Integer getGeneralLogEntry(Long time){
        return generalChargingLog.get(time);
    }

    public synchronized void clearGeneralLogEntries(){
        generalChargingLog.clear();
    }

    public synchronized void addToGeneralLog(Long time, Integer quantity){
        generalChargingLog.put(time,quantity);
    }
}
