import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

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
                    NotificationLogger.logger.addToLog("Logging complete. Thread sleeping for 15m");
                    Thread.sleep(300000);
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
