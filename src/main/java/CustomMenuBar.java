import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;

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

        JMenu editMenu = new JMenu("Edit");
        editMenu.setFont(Settings.DEFAULT_FONT);
        this.add(editMenu);

        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setFont(Settings.DEFAULT_FONT);
        this.add(toolsMenu);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setFont(Settings.DEFAULT_FONT);
        this.add(viewMenu);

        JMenuItem importIdsMenu = new JMenuItem("Import EV Charger's IDs");
        importIdsMenu.setFont(Settings.DEFAULT_FONT);
        fileMenu.add(importIdsMenu);
        importIdsMenu.addActionListener(e -> importIds());

        JMenuItem importConfiguration = new JMenuItem("Import Program Configuration",'o');
        importConfiguration.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        importConfiguration.setFont(Settings.DEFAULT_FONT);
        fileMenu.add(importConfiguration);
        importConfiguration.addActionListener(e -> importConfig());

        fileMenu.addSeparator();

        JMenuItem exportConfiguration = new JMenuItem("Export Program Configuration",'s');
        exportConfiguration.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        exportConfiguration.setFont(Settings.DEFAULT_FONT);
        fileMenu.add(exportConfiguration);
        exportConfiguration.addActionListener(e -> exportConfig());

        JMenuItem exportCSV = new JMenuItem("Export Data as CSV");
        exportCSV.setFont(Settings.DEFAULT_FONT);
        fileMenu.add(exportCSV);

        JMenuItem exportGraph = new JMenuItem("Export View");
        exportGraph.setFont(Settings.DEFAULT_FONT);
        fileMenu.add(exportGraph);

        JMenuItem changeInterval = new JMenuItem("Edit Log Interval");
        changeInterval.setFont(Settings.DEFAULT_FONT);
        editMenu.add(changeInterval);

        JMenuItem downloadData = new JMenuItem("Download EV Charger's Data");
        downloadData.setFont(Settings.DEFAULT_FONT);
        toolsMenu.add(downloadData);
        downloadData.addActionListener(e-> app.getDataModel().downloadIdData());

        JMenuItem repairData = new JMenuItem("Repair Log Data",'r');
        repairData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        repairData.setFont(Settings.DEFAULT_FONT);
        toolsMenu.add(repairData);
        repairData.addActionListener(e -> app.getDataModel().repairDataModel());

        toolsMenu.addSeparator();

        JMenuItem startLoggingMenu = new JMenuItem("EV Data Logging - Start");
        startLoggingMenu.setFont(Settings.DEFAULT_FONT);
        toolsMenu.add(startLoggingMenu);

        JMenuItem stopLoggingMenu = new JMenuItem("EV Data Logging - Stop");
        stopLoggingMenu.setEnabled(false);
        stopLoggingMenu.setFont(Settings.DEFAULT_FONT);
        toolsMenu.add(stopLoggingMenu);

        JMenuItem fitGraph = new JMenuItem("Fit Graph Scale to Window");
        fitGraph.setFont(Settings.DEFAULT_FONT);
        viewMenu.add(fitGraph);
        fitGraph.addActionListener(e -> app.getGraphPanel().fitGraphToWindow(false));

        JMenuItem fitFullGraph = new JMenuItem("Fit Data Scale to Window");
        fitFullGraph.setFont(Settings.DEFAULT_FONT);
        viewMenu.add(fitFullGraph);
        fitFullGraph.addActionListener(e -> app.getGraphPanel().fitGraphToWindow(true));

        viewMenu.addSeparator();

        JMenuItem openDetailsPanel = new JMenuItem("Open Details Panel");
        openDetailsPanel.setFont(Settings.DEFAULT_FONT);
        viewMenu.add(openDetailsPanel);
        openDetailsPanel.addActionListener(e -> {app.openDetailsPanel();});

        JMenuItem closeDetailsPanel = new JMenuItem("Close Details Panel");
        closeDetailsPanel.setFont(Settings.DEFAULT_FONT);
        viewMenu.add(closeDetailsPanel);
        closeDetailsPanel.addActionListener(e -> {app.closeDetailsPanel();});

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

        changeInterval.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(null, "Enter a new Log Interval (minutes)", "Edit Log Interval", JOptionPane.PLAIN_MESSAGE);
            if(input != null){
                try{
                    if (Long.parseLong(input) > 0){
                        app.setLogInterval(Long.parseLong(input)*60000);
                    } else {
                        NotificationLogger.logger.addToLog("Could not change Log Interval: Invalid Number");
                    }
                } catch (Exception e1){
                    NotificationLogger.logger.addToLog("Could not change Log Interval: Invalid Input");
                }
            }
            app.repaint();
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
        System.out.println();
    }

    public void exportConfig(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Export Program Configuration");
        fileChooser.setFont(Settings.DEFAULT_FONT);
        int returnVal = fileChooser.showSaveDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            try {
                exportConfigToFIle(fileChooser.getSelectedFile().getAbsolutePath());
            } catch (ParserConfigurationException | TransformerException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportConfigToFIle(String path) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();

        //Create the root
        Element programConfigElement = doc.createElement("ProgramConfiguration");
        doc.appendChild(programConfigElement);

        Element logIntervalElement = doc.createElement("LogInterval");
        programConfigElement.appendChild(logIntervalElement);
        logIntervalElement.appendChild(doc.createTextNode(Settings.LOG_INTERVAL.toString()));

        //Iterate over all chargers and create a node for each of them
        for(String chargerID : app.getDataModel().getIds()){
            ChargerObject chargerObject = app.getDataModel().getCharger(chargerID);
            if(chargerObject == null){
                System.out.println(chargerID);
            }

            //append the base ChargerObject node.
            Element chargerElement = doc.createElement("ChargerObject");
            programConfigElement.appendChild(chargerElement);

            //append the id to the ChargerObject
            Element chargerIDElement = doc.createElement("ChargerID");
            chargerElement.appendChild(chargerIDElement);
            chargerIDElement.appendChild(doc.createTextNode(chargerObject.getId()));

            //append the designator to the ChargerObject
            Element chargerDesignatorElement = doc.createElement("ChargerDesignator");
            chargerElement.appendChild(chargerDesignatorElement);
            chargerDesignatorElement.appendChild(doc.createTextNode(chargerObject.getDesignator().toString()));

            //append the name to the ChargerObject
            Element chargerNameElement = doc.createElement("ChargerName");
            chargerElement.appendChild(chargerNameElement);
            chargerNameElement.appendChild(doc.createTextNode(chargerObject.getName()));

            //append the address to the ChargerObject
            Element chargerAddressElement = doc.createElement("ChargerAddress");
            chargerElement.appendChild(chargerAddressElement);
            chargerAddressElement.appendChild(doc.createTextNode(chargerObject.getAddress()));


            //append the power to the ChargerObject
            Element chargerPowerElement = doc.createElement("ChargerPower");
            chargerElement.appendChild(chargerPowerElement);
            chargerPowerElement.appendChild(doc.createTextNode(chargerObject.getPowerOutput().toString()));

            //append the price to the ChargerObject
            Element chargerPriceElement = doc.createElement("ChargerPrice");
            chargerElement.appendChild(chargerPriceElement);
            chargerPriceElement.appendChild(doc.createTextNode(chargerObject.getPrice().toString()));

            //append the isRapid to the ChargerObject
            Element chargerRapidElement = doc.createElement("IsChargerRapid");
            chargerElement.appendChild(chargerRapidElement);
            chargerRapidElement.appendChild(doc.createTextNode(chargerObject.isRapid().toString()));

            //append the log to the ChargerObject
            Element chargerLogElement = doc.createElement("ChargerLog");
            chargerElement.appendChild(chargerLogElement);

            //Iterate over all entries in the log and append them to the log element.
            for(Long logEntry : chargerObject.getLogTimes()){
                Element logEntryElement = doc.createElement("LogEntry");
                chargerLogElement.appendChild(logEntryElement);

                //append the time of the entry to the entry node.
                Element timeAttributeElement = doc.createElement("EntryTime");
                logEntryElement.appendChild(timeAttributeElement);
                timeAttributeElement.appendChild(doc.createTextNode(logEntry.toString()));

                //append the time of the entry to the entry node.
                Element statusAttributeElement = doc.createElement("EntryStatus");
                logEntryElement.appendChild(statusAttributeElement);
                statusAttributeElement.appendChild(doc.createTextNode(chargerObject.getEntryInLog(logEntry).toString()));
            }
        }

        //Create a transformer which creates an output stream from the document. Use this to output the file to the given path.
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(path+".xml");
        transformer.transform(domSource, streamResult);
    }

    public void importConfig(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Import Program Configuration");
        fileChooser.setFont(Settings.DEFAULT_FONT);
        int returnVal = fileChooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            try {
                importConfigHelper(fileChooser.getSelectedFile().getAbsolutePath());
            } catch (SAXException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void importConfigHelper(String path) throws IOException, SAXException, ParserConfigurationException {
        app.getDataModel().clearChargers();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(path));
        document.getDocumentElement().normalize();

        //Extract the root node
        Element programConfigElement = document.getDocumentElement();

        Node logIntervalNode = programConfigElement.getElementsByTagName("LogInterval").item(0);
        app.setLogInterval(Long.parseLong(logIntervalNode.getTextContent()));

        //iterate over all nodes
        NodeList nodes = programConfigElement.getElementsByTagName("ChargerObject");
        for(int i = 0; i < nodes.getLength(); i++){
            Element chargerObjectElement = (Element) nodes.item(i);
            ChargerObject chargerObject = new ChargerObject("",0);

            //extract the ID
            Node chargerIDNode = chargerObjectElement.getElementsByTagName("ChargerID").item(0);
            chargerObject.setId(chargerIDNode.getTextContent());

            //extract the designator
            Node chargerDesignatorNode = chargerObjectElement.getElementsByTagName("ChargerDesignator").item(0);
            chargerObject.setDesignator(Integer.parseInt(chargerDesignatorNode.getTextContent()));

            //extract the name
            Node chargerNameNode = chargerObjectElement.getElementsByTagName("ChargerName").item(0);
            chargerObject.setName(chargerNameNode.getTextContent());

            //extract the address
            Node chargerAddressNode = chargerObjectElement.getElementsByTagName("ChargerAddress").item(0);
            chargerObject.setAddress(chargerAddressNode.getTextContent());

            //extract the power
            Node chargerPowerNode = chargerObjectElement.getElementsByTagName("ChargerPower").item(0);
            chargerObject.setPowerOutput(Double.parseDouble(chargerPowerNode.getTextContent()));

            //extract the price
            Node chargerPriceNode = chargerObjectElement.getElementsByTagName("ChargerPrice").item(0);
            chargerObject.setPrice(Double.parseDouble(chargerPriceNode.getTextContent()));

            //extract isRapid
            Node chargerRapidNode = chargerObjectElement.getElementsByTagName("IsChargerRapid").item(0);
            chargerObject.setRapid(Boolean.parseBoolean(chargerRapidNode.getTextContent()));

            //iterate over log and extract
            Element chargerLogElement = (Element) chargerObjectElement.getElementsByTagName("ChargerLog").item(0);
            NodeList chargerLogNodes = chargerLogElement.getElementsByTagName("LogEntry");
            for(int j = 0; j< chargerLogNodes.getLength(); j++){
                Element logEntryElement = (Element) chargerLogNodes.item(j);

                Node logEntryTimeNode = logEntryElement.getElementsByTagName("EntryTime").item(0);
                Long timeEntry = Long.parseLong(logEntryTimeNode.getTextContent());
                Node logEntryStatusNode = logEntryElement.getElementsByTagName("EntryStatus").item(0);
                Boolean statusEntry = Boolean.parseBoolean(logEntryStatusNode.getTextContent());

                chargerObject.addLogEntry(timeEntry,statusEntry,false);
            }

            String chargerObjectId = chargerObject.getId() +":" + chargerObject.getDesignator();
            app.getDataModel().addCharger(chargerObjectId,chargerObject);
            app.getMenuPanel().addMenuItem(chargerObjectId + " - " + chargerObject.getName());
        }
        app.getMenuPanel().resetSelectedItem();
        app.getDataModel().rebuiltGeneralModel();
        app.getGraphPanel().fitGraphToWindow(true);
        app.repaint();
    }
}
