import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

class MenuPanel extends JPanel {

    private DefaultListModel<String> selectorModel;
    private JList<String> selectorList;
    private Application app;

    MenuPanel(Application app){
        this.app = app;
        selectorModel = new DefaultListModel<>();
        selectorModel.addElement("Show All");
        selectorList = new JList<>(selectorModel);
        init();
    }

    private void init(){
        this.setPreferredSize(new Dimension(370,100));

        this.setLayout(new GridBagLayout());
        GridBagConstraints c;

        JPanel controlPanel = new JPanel();
        TitledBorder controlBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY,1),"Control Panel",0,2,
                Settings.DEFAULT_FONT,Color.WHITE);
        controlBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 18));
        controlPanel.setBorder(controlBorder);
        controlPanel.setLayout(new GridBagLayout());

        JPanel selectionPanel = new JPanel();
        TitledBorder selectionBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY,1),"Selection Panel",0,2,
                Settings.DEFAULT_FONT,Color.WHITE);
        selectionBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 18));
        selectionPanel.setBorder(selectionBorder);
        selectionPanel.setLayout(new GridBagLayout());

        c = new GridBagConstraints(); c.gridx = 0; c.gridy = 0; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10,10,25,10);
        this.add(controlPanel,c);

        c = new GridBagConstraints(); c.gridx = 0; c.gridy = 1; c.weighty =1; c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0,10,25,10);
        this.add(selectionPanel,c);

        JLabel xAxisScaleLabel = new JLabel("Horizontal Scale:");
        xAxisScaleLabel.setFont(Settings.DEFAULT_FONT);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.insets = new Insets(10,15,0,0);
        c.anchor = GridBagConstraints.LINE_START;
        controlPanel.add(xAxisScaleLabel,c);

        SpinnerModel spinnerModel = new SpinnerNumberModel(100,1,9999,1);
        JSpinner scaleSpinner = new JSpinner(spinnerModel);
        c = new GridBagConstraints();
        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10,15,15,10);
        c.anchor = GridBagConstraints.LINE_START;
        controlPanel.add(scaleSpinner,c);

        JCheckBox includeRapid = new JCheckBox("Include Level 3 Chargers");
        includeRapid.setFont(Settings.DEFAULT_FONT);
        includeRapid.setSelected(true);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1; c.weightx = 1; c.gridwidth = 2;
        c.insets = new Insets(0,10,0,0);
        c.anchor = GridBagConstraints.LINE_START;
        controlPanel.add(includeRapid,c);

        JCheckBox includeFast = new JCheckBox("Include Level 2 Chargers");
        includeFast.setFont(Settings.DEFAULT_FONT);
        includeFast.setSelected(true);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 2; c.weightx = 1; c.gridwidth = 2;
        c.insets = new Insets(0,10,10,0);
        c.anchor = GridBagConstraints.LINE_START;
        controlPanel.add(includeFast,c);

        selectorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectorList.setLayoutOrientation(JList.VERTICAL);
        selectorList.setSelectedIndex(0);
        JScrollPane selectorPanel = new JScrollPane(selectorList);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1; c.weighty = 1; c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10,10,0,10);
        selectionPanel.add(selectorPanel,c);

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        JScrollPane infoScroller = new JScrollPane(infoArea);
        infoScroller.setPreferredSize(new Dimension(0,200));
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 2; c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0,10,10,10);
        selectionPanel.add(infoScroller,c);

        selectorList.addListSelectionListener(e -> {
            app.repaint();
            if(e.getValueIsAdjusting()){
                if(selectorList.getSelectedIndex() == 0){
                    infoArea.setText("");
                } else{
                    String selectedID = selectorList.getSelectedValue().split(" - ")[0];
                    ChargerObject selectedCharger = app.getDataModel().getChargeObject(selectedID);
                    String newText = "NAME: " + selectedCharger.getName();
                    newText+= "\nPRICE: " + selectedCharger.getPrice() +"p";
                    newText+= "\nOUTPUT: " + selectedCharger.getPowerOutput()+"kW";
                    if(selectedCharger.isRapid()){
                        newText+= "\nRAPID: Yes";
                    } else {
                        newText+= "\nRAPID: No";
                    }
                    newText+= "\nADDRESS: " + selectedCharger.getAddress();
                    newText+= "\nINFO: " + selectedCharger.getAdditionalInfo();
                    infoArea.setText(newText);
                }
            }
        });
    }

    public synchronized void addMenuItem(String item){
        selectorModel.addElement(item);
        this.repaint();
    }

    public String getSelectedOption(){
        return selectorList.getSelectedValue();
    }

}
