import com.gargoylesoftware.htmlunit.html.HtmlPage;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

class DataModel {

    private Set<String> ids;
    private Map<String,ChargerObject> chargers;
    private Application app;
    private Map<Long,Integer> totalChargersLog;

    DataModel(Application app){
        this.ids = new TreeSet<>();
        this.chargers = new HashMap<>();
        this.app = app;
        this.totalChargersLog = new HashMap<>();
    }

    /**
     * Downloads the details for all the chargers in the 'ids' list in DataModel
     */
    public synchronized void downloadIdData() {
        //Clear the previous map
        chargers.clear();
        NotificationLogger.logger.addToLog("");

        //Create a new thread to run this on so that it can happen concurrently
        Thread webThread = new Thread(() -> {
            //create these objects outside the for loop so they can be used to optimize downloads.
            String previousID = "";
            HtmlPage doc = null;
            long startTime = System.currentTimeMillis();

            //Iterate over all ids
            for(String id : ids){
                NotificationLogger.logger.addToLog("Downloading data for '" + id + "'");

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

                //log the results in teh notification panel.
                NotificationLogger.logger.addToLog("Download successful:");
                NotificationLogger.logger.addToLog("\tName: " + newCharger.getName());
                NotificationLogger.logger.addToLog("\tPrice: " + newCharger.getPrice() +"p");
                NotificationLogger.logger.addToLog("\tPower: " + newCharger.getPowerOutput() +"kW");
                NotificationLogger.logger.addToLog("");
            }
            NotificationLogger.logger.addToLog("Download Completed in " + (System.currentTimeMillis() - startTime)/1000 +"s");
        });
        //start the new thread.
        webThread.start();
    }

    public synchronized Set<String> getIds(){
        return ids;
    }

    public synchronized ChargerObject getChargeObject(String id){
        return chargers.get(id);
    }

    public synchronized void clearIds(){
        ids.clear();
    }

    public synchronized void clearChargers(){
        chargers.clear();
    }

    public synchronized void addId(String newId){
        ids.add(newId);
    }

    public synchronized void addCharger(String chargerId, ChargerObject charger){
        chargers.put(chargerId,charger);
    }

    public synchronized Set<Long> getTotalChargersKeySet(){
        return totalChargersLog.keySet();
    }

    public synchronized Integer getTotalChargersAtTime(Long time){
        return totalChargersLog.get(time);
    }

    public synchronized void clearTotalChargersLog(){
        totalChargersLog.clear();
    }

    public synchronized void addTotalChargersLog(Long time, Integer quantity){
        totalChargersLog.put(time,quantity);
    }
}
