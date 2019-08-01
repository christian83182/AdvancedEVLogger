import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CustomMenuBar extends JMenuBar {

    Application app;

    CustomMenuBar(Application app){
        this.app = app;
        this.setBorder(BorderFactory.createMatteBorder(1,1,3,1,new Color(50, 50, 50)));
        init();
    }

    private void init(){
        JMenu dataMenu = new JMenu("File");
        dataMenu.setFont(Settings.MENU_BAR_DEFAULT_FONT);
        this.add(dataMenu);

        JMenu exportMenu = new JMenu("Tools");
        exportMenu.setFont(Settings.MENU_BAR_DEFAULT_FONT);
        this.add(exportMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(Settings.MENU_BAR_DEFAULT_FONT);
        this.add(helpMenu);

        JMenuItem importIdsMenu = new JMenuItem("Import EV Charger's IDs");
        importIdsMenu.setFont(Settings.MENU_BAR_DEFAULT_FONT);
        dataMenu.add(importIdsMenu);
        importIdsMenu.addActionListener(e -> importIds());

        JMenuItem importConfiguration = new JMenuItem("Import Program Configuration");
        importConfiguration.setFont(Settings.MENU_BAR_DEFAULT_FONT);
        dataMenu.add(importConfiguration);

        dataMenu.addSeparator();

        JMenuItem exportConfiguration = new JMenuItem("Export Program Configuration");
        exportConfiguration.setFont(Settings.MENU_BAR_DEFAULT_FONT);
        dataMenu.add(exportConfiguration);
        exportConfiguration.addActionListener(e -> exportConfig());

        JMenuItem exportCSV = new JMenuItem("Export Data as CSV");
        exportCSV.setFont(Settings.MENU_BAR_DEFAULT_FONT);
        dataMenu.add(exportCSV);

        JMenuItem exportGraph = new JMenuItem("Export View");
        exportGraph.setFont(Settings.MENU_BAR_DEFAULT_FONT);
        dataMenu.add(exportGraph);

        JMenuItem downloadData = new JMenuItem("Download EV Charger's Data");
        downloadData.setFont(Settings.MENU_BAR_DEFAULT_FONT);
        exportMenu.add(downloadData);
        downloadData.addActionListener(e-> app.getDataModel().downloadIdData());
    }

    private void importIds(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "TXT (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Import EV Charger's IDs");
        fileChooser.setFont(Settings.MENU_BAR_DEFAULT_FONT);
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportConfig(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Export Configuration");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "TXT (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setFont(Settings.MENU_BAR_DEFAULT_FONT);
        int returnVal = fileChooser.showSaveDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            try{
                NotificationLogger.logger.addToLog("");
                NotificationLogger.logger.addToLog("Exporting Program Configuration");
                FileWriter writer = new FileWriter(fileChooser.getSelectedFile()+".txt");

                NotificationLogger.logger.addToLog("Exporting ID list");
                String ids = "IDLIST";
                for(String id : app.getDataModel().getIds()){
                    ids += "|" + id;
                }
                writer.write(ids +"\n");

                NotificationLogger.logger.addToLog("Exporting EV Charger Info");
                for(String id : app.getDataModel().getIds()){
                    NotificationLogger.logger.addToLog("Exporting data for '" + id + "'");
                    ChargerObject charger = app.getDataModel().getChargeObject(id);
                    StringBuilder output = new StringBuilder("CHARGERINFO");
                    if(charger != null){
                        output.append("|" + charger.getId());
                        output.append("|" + charger.getDesignator());
                        output.append("|" + charger.getAddress());
                        output.append("|" + charger.getAdditionalInfo());
                        output.append("|" + charger.getName());
                        output.append("|" + charger.getPrice());
                        output.append("|" + charger.getPowerOutput());
                        output.append("|" + charger.isRapid());
                        for (long timeOfLog : charger.getLogTimes()){
                            output.append("|" + timeOfLog + ":" + charger.getEntryInLog(timeOfLog));
                        }
                        writer.write(output+"\n");
                    }
                }
                NotificationLogger.logger.addToLog("Export Complete");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
