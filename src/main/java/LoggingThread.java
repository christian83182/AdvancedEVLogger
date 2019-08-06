import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
                    Long startTime = System.currentTimeMillis();
                    new LoggingHelperThread();
                    Thread.sleep(System.currentTimeMillis() - startTime + Settings.LOG_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    NotificationLogger.logger.addToLog("[ERROR] An error occurred while logging...");
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

    private class LoggingHelperThread extends Thread{

        LoggingHelperThread(){
            this.start();
        }

        @Override
        public void run() {
            super.run();
            logData();
        }

        private void logData(){
            try {
                Long startTime = System.currentTimeMillis();
                NotificationLogger.logger.addToLog("Starting logging process...");
                HtmlPage doc = null;
                String previousId = "";

                Integer counter =0;
                for(String id : app.getDataModel().getIds()){
                    ChargerObject currentCharger = app.getDataModel().getCharger(id);
                    if(!currentCharger.getId().equals(previousId)){
                        doc = currentCharger.getHtmlPage(new WebClient());
                    }
                    currentCharger.logCurrent(doc,startTime);
                    previousId = currentCharger.getId();
                    counter++;
                    NotificationLogger.logger.addToLog("Log Successful for '"+id+
                            "'    (" + counter +"/" +app.getDataModel().getIds().size() +")");
                }

                app.getDataModel().rebuiltGeneralModel();
                app.repaint();
                DateFormat simple = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                NotificationLogger.logger.addToLog(
                        "Logging completed in " +(System.currentTimeMillis() - startTime)/1000
                                +"s    Total Chargers in Use: " + app.getDataModel().getGeneralLogEntry(startTime)
                                +"    Next Log at " + simple.format(startTime+Settings.LOG_INTERVAL));

                app.getCustomMenuBar().exportConfigToFIle("LogFiles\\Log_Recent");
            } catch (IOException  | TransformerException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
    }
}
