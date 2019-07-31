import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
        int returnVal = fileChooser.showSaveDialog(null);
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
}
