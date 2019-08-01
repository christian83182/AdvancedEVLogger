import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.Collections;

public class LoggingThread extends Thread {

    private Application app;
    private boolean isAlive;

    LoggingThread(Application app){
        this.app = app;
        this.isAlive = true;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        while(isAlive){
            if(app.isLogging()){
                try {
                    NotificationLogger.logger.addToLog("Starting logging process...");
                    HtmlPage doc = null;
                    String previousId = "";
                    for(String id : app.getDataModel().getIds()){
                        ChargerObject currentCharger = app.getDataModel().getChargeObject(id);
                        if(!currentCharger.getId().equals(previousId)){
                            doc = currentCharger.getHtmlPage(app.getWebClient());
                        }
                        currentCharger.logCurrent(doc);
                        previousId = currentCharger.getId();
                        NotificationLogger.logger.addToLog("Log Successful for '"+id+"'");
                    }

                    Integer totalChargers = 0;
                    for(String id : app.getDataModel().getIds()){
                        ChargerObject charger = app.getDataModel().getChargeObject(id);
                        Long latestLogTime = Collections.max(charger.getLogTimes());
                        if(charger.getEntryInLog(latestLogTime)){
                            totalChargers++;
                        }
                    }
                    app.getDataModel().addTotalChargersLog(System.currentTimeMillis(),totalChargers);

                    NotificationLogger.logger.addToLog("Logging complete. Total Chargers in Use: " + totalChargers);
                    Thread.sleep(600000 );
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void kill(){
        this.isAlive = false;
    }
}
