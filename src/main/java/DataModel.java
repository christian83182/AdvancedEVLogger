import com.gargoylesoftware.htmlunit.WebClient;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataModel {

    private List<String> ids;
    private Map<String,ChargerObject> chargers;
    private Application app;

    DataModel(Application app){
        this.ids = new ArrayList<String>();
        this.chargers = new HashMap<>();
        this.app = app;
    }

    public void clearIDs() {
        ids.clear();
    }

    public void addId(String id){
        ids.add(id);
    }

    public void downloadIdData() {
        chargers.clear();
        NotificationLogger.logger.addToLog("");
        Thread webThread = new Thread(() -> {
            for(String id : ids){
                NotificationLogger.logger.addToLog("Downloading data for '" + id + "'");

                String realID = id.split(":")[0];
                Integer designator = Integer.parseInt(id.split(":")[1]);
                ChargerObject newCharger = new ChargerObject(realID,designator);
                try {
                    newCharger.fetchDetails(app.getWebClient());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                chargers.put(id,newCharger);
                app.getMenuPanel().addMenuItem(newCharger.getId() +":" + newCharger.getDesignator() + " - " + newCharger.getName());

                NotificationLogger.logger.addToLog("Download successful:");
                NotificationLogger.logger.addToLog("\tName: " + newCharger.getName());
                NotificationLogger.logger.addToLog("\tPrice: " + newCharger.getPrice() +"p");
                NotificationLogger.logger.addToLog("\tPower: " + newCharger.getPowerOutput() +"kW");
                NotificationLogger.logger.addToLog("");
            }
            NotificationLogger.logger.addToLog("Download Complete");
        });
        webThread.start();

    }

    public List<String> getIds(){
        return ids;
    }

    public ChargerObject getChargeObject(String id){
        return chargers.get(id);
    }
}
