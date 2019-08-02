import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.regex.Pattern;

public class CustomMenuBar extends JMenuBar {

    Application app;

    CustomMenuBar(Application app){
        this.app = app;
        this.setBorder(BorderFactory.createMatteBorder(1,1,3,1,new Color(50, 50, 50)));
        init();
    }

    private void init(){
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(Settings.DEFAULT_FONT);
        this.add(fileMenu);

        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setFont(Settings.DEFAULT_FONT);
        this.add(toolsMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(Settings.DEFAULT_FONT);
        this.add(helpMenu);

        JMenuItem importIdsMenu = new JMenuItem("Import EV Charger's IDs");
        importIdsMenu.setFont(Settings.DEFAULT_FONT);
        fileMenu.add(importIdsMenu);
        importIdsMenu.addActionListener(e -> importIds());

        JMenuItem importConfiguration = new JMenuItem("Import Program Configuration");
        importConfiguration.setFont(Settings.DEFAULT_FONT);
        fileMenu.add(importConfiguration);
        importConfiguration.addActionListener(e -> importConfig());

        fileMenu.addSeparator();

        JMenuItem exportConfiguration = new JMenuItem("Export Full Program Configuration");
        exportConfiguration.setFont(Settings.DEFAULT_FONT);
        fileMenu.add(exportConfiguration);
        exportConfiguration.addActionListener(e -> exportConfig(true));

        JMenuItem exporWithoutLog = new JMenuItem("Export Configuration Without Log");
        exporWithoutLog.setFont(Settings.DEFAULT_FONT);
        fileMenu.add(exporWithoutLog);
        exporWithoutLog.addActionListener(e -> exportConfig(false));

        JMenuItem exportCSV = new JMenuItem("Export Data as CSV");
        exportCSV.setFont(Settings.DEFAULT_FONT);
        fileMenu.add(exportCSV);

        JMenuItem exportGraph = new JMenuItem("Export View");
        exportGraph.setFont(Settings.DEFAULT_FONT);
        fileMenu.add(exportGraph);

        JMenuItem downloadData = new JMenuItem("Download EV Charger's Data");
        downloadData.setFont(Settings.DEFAULT_FONT);
        toolsMenu.add(downloadData);
        downloadData.addActionListener(e-> app.getDataModel().downloadIdData());

        JMenuItem fitGraph = new JMenuItem("Fit Graph Scale to Window");
        fitGraph.setFont(Settings.DEFAULT_FONT);
        toolsMenu.add(fitGraph);
        fitGraph.addActionListener(e -> app.getGraphPanel().fitToWindow());

        toolsMenu.addSeparator();

        JMenuItem startLoggingMenu = new JMenuItem("EV Data Logging - Start");
        startLoggingMenu.setFont(Settings.DEFAULT_FONT);
        toolsMenu.add(startLoggingMenu);

        JMenuItem stopLoggingMenu = new JMenuItem("EV Data Logging - Stop");
        stopLoggingMenu.setEnabled(false);
        stopLoggingMenu.setFont(Settings.DEFAULT_FONT);
        toolsMenu.add(stopLoggingMenu);

        startLoggingMenu.addActionListener(e -> {
            app.setLogging(true);
            stopLoggingMenu.setEnabled(true);
            startLoggingMenu.setEnabled(false);
        });

        stopLoggingMenu.addActionListener(e -> {
            app.setLogging(false);
            startLoggingMenu.setEnabled(true);
            stopLoggingMenu.setEnabled(false);
        });
    }

    private void importIds(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "TXT (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Import EV Charger's IDs");
        fileChooser.setFont(Settings.DEFAULT_FONT);
        int returnVal = fileChooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                String line;
                while((line = reader.readLine()) != null){
                    if(!line.equals("")){
                        String[] data = line.split(" ");
                        app.getDataModel().addId(data[0]+":"+data[1]);
                        NotificationLogger.logger.addToLog("Adding ID '" + data[0]+":"+data[1] +"' to Configuration");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportConfig(boolean includeLog){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Export Configuration");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "TXT (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setFont(Settings.DEFAULT_FONT);
        int returnVal = fileChooser.showSaveDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            try{
                NotificationLogger.logger.addToLog("");
                NotificationLogger.logger.addToLog("Exporting Program Configuration");

                File fileToBeSaved = fileChooser.getSelectedFile();
                if(!fileChooser.getSelectedFile().getAbsolutePath().endsWith(".txt")){
                    fileToBeSaved = new File(fileChooser.getSelectedFile() + ".txt");
                }
                FileWriter writer = new FileWriter(fileToBeSaved);

                NotificationLogger.logger.addToLog("Exporting ID list");
                StringBuilder ids = new StringBuilder("IDLIST");
                for(String id : app.getDataModel().getIds()){
                    ids.append("|").append(id);
                }
                writer.write(ids.toString() +"\n");

                if(includeLog){
                    StringBuilder output = new StringBuilder("TOTALCHARGERS");
                    for(Long time : app.getDataModel().getGeneralLogKey()){
                        output.append("|").append(time);
                        output.append(":").append(app.getDataModel().getGeneralLogEntry(time));
                    }
                    writer.write(output.toString()+"\n");
                }

                NotificationLogger.logger.addToLog("Exporting EV Charger Info");
                for(String id : app.getDataModel().getIds()){
                    NotificationLogger.logger.addToLog("Exporting data for '" + id + "'");
                    ChargerObject charger = app.getDataModel().getChargeObject(id);
                    StringBuilder output = new StringBuilder("CHARGERINFO");
                    if(charger != null){
                        output.append("|").append(charger.getId());
                        output.append("|").append(charger.getDesignator());
                        output.append("|").append(charger.getAddress());
                        output.append("|").append(charger.getAdditionalInfo());
                        output.append("|").append(charger.getName());
                        output.append("|").append(charger.getPrice());
                        output.append("|").append(charger.getPowerOutput());
                        output.append("|").append(charger.isRapid());

                        if(includeLog){
                            for (long timeOfLog : charger.getLogTimes()){
                                output.append("|").append(timeOfLog).append(":").append(charger.getEntryInLog(timeOfLog));
                            }
                            writer.write(output.toString()+"\n");
                        }
                    }
                }
                NotificationLogger.logger.addToLog("Export Complete");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void importConfig(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "TXT (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Import Program Configuration");
        fileChooser.setFont(Settings.DEFAULT_FONT);
        int returnVal = fileChooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            NotificationLogger.logger.addToLog("Importing new program configuration");
            try {
                app.getDataModel().clearChargers();
                app.getDataModel().clearIds();
                BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                String line;
                while((line = reader.readLine()) != null){
                    String[] data = line.split(Pattern.quote("|"));
                    if(data[0].equals("IDLIST")){
                        for(int i = 1; i < data.length; i++){
                            app.getDataModel().addId(data[i]);
                        }
                        NotificationLogger.logger.addToLog("Imported ID List from File");
                    } else if(data[0].equals("CHARGERINFO")){
                        ChargerObject newCharger = new ChargerObject(data[1],Integer.parseInt(data[2]));
                        newCharger.setAddress(data[3]);
                        newCharger.setAdditionalInfo(data[4]);
                        newCharger.setName(data[5]);
                        newCharger.setRapid(Boolean.parseBoolean(data[8]));
                        if(!data[6].equals("")) newCharger.setPrice(Double.parseDouble(data[6]));
                        if(!data[7].equals("")) newCharger.setPowerOutput(Double.parseDouble(data[7]));
                        String[] log = Arrays.copyOfRange(data,9,data.length);
                        for(String logEntry : log){
                            Long time = Long.parseLong(logEntry.split(":")[0]);
                            Boolean value = Boolean.parseBoolean(logEntry.split(":")[1]);
                            newCharger.addLogEntry(time,value);
                        }

                        app.getDataModel().addCharger(newCharger.getId()+":"+newCharger.getDesignator(),newCharger);
                        app.getMenuPanel().addMenuItem(newCharger.getId()+":"+newCharger.getDesignator() +" - " + newCharger.getName());
                        NotificationLogger.logger.addToLog("Imported Charger '" +data[1]+":"+data[2] +"' from file" );
                    } else {
                        NotificationLogger.logger.addToLog("Invalid entry found, ignoring data...");
                    }
                }
                NotificationLogger.logger.addToLog("Import Successful");
                app.getDataModel().rebuiltGeneralModel();
                app.getGraphPanel().fitToWindow();
                app.repaint();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
