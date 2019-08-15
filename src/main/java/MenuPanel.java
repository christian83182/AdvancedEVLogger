import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

class MenuPanel extends JPanel {

    private DefaultListModel<String> selectorModel;
    private JList<String> selectorList;
    private Application app;

    private JSpinner spinnerHorizontal;
    private JSpinner spinnerVertical;
    private JSpinner spinnerAverage;
    private JCheckBox includeRapid;
    private JCheckBox includeFast;

    MenuPanel(Application app){
        this.app = app;
        selectorModel = new DefaultListModel<>();
        selectorModel.addElement("Show Aggregate Data");
        selectorModel.addElement("Show Moving Average");
        selectorList = new JList<>(selectorModel);
        init();
    }

    private void init(){
        //this.setPreferredSize(new Dimension(300,100));

        this.setLayout(new GridBagLayout());
        GridBagConstraints c;

        JPanel controlPanel = new JPanel();
        TitledBorder controlBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY,1),"Control Panel   ",0,2,
                Settings.DEFAULT_FONT,Color.WHITE);
        controlBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 15));
        controlPanel.setBorder(controlBorder);
        controlPanel.setLayout(new GridBagLayout());

        JPanel selectionPanel = new JPanel();
        TitledBorder selectionBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY,1),"Selection Panel   ",0,2,
                Settings.DEFAULT_FONT,Color.WHITE);
        selectionBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 15));
        selectionPanel.setBorder(selectionBorder);
        selectionPanel.setLayout(new GridBagLayout());

        c = new GridBagConstraints(); c.gridx = 0; c.gridy = 0; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10,5,0,5);
        this.add(controlPanel,c);

        c = new GridBagConstraints(); c.gridx = 0; c.gridy = 1; c.weighty =1; c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5,5,5,5);
        this.add(selectionPanel,c);

        JLabel xAxisScaleLabel = new JLabel("Horizontal Scale:  ");
        xAxisScaleLabel.setFont(Settings.DEFAULT_FONT);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.insets = new Insets(5,10,0,0);
        c.anchor = GridBagConstraints.LINE_START;
        controlPanel.add(xAxisScaleLabel,c);

        SpinnerModel spinnerModelHorizontal = new SpinnerNumberModel(100,1,5000,1);
        spinnerHorizontal = new JSpinner(spinnerModelHorizontal);
        c = new GridBagConstraints();
        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5,10,0,10);
        c.anchor = GridBagConstraints.LINE_START;
        controlPanel.add(spinnerHorizontal,c);

        JLabel yAxisScaleLabel = new JLabel("Vertical Scale:  ");
        yAxisScaleLabel.setFont(Settings.DEFAULT_FONT);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1;
        c.insets = new Insets(0,10,10,0);
        c.anchor = GridBagConstraints.LINE_START;
        controlPanel.add(yAxisScaleLabel,c);

        SpinnerModel spinnerModelVertical = new SpinnerNumberModel(10,1,1000,1);
        spinnerVertical = new JSpinner(spinnerModelVertical);
        c = new GridBagConstraints();
        c.gridx = 1; c.gridy = 1; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,10,5,10);
        c.anchor = GridBagConstraints.LINE_START;
        controlPanel.add(spinnerVertical,c);

        JLabel movingAverageLabel = new JLabel("Moving Average Width");
        movingAverageLabel.setFont(Settings.DEFAULT_FONT);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 2;
        c.insets = new Insets(0,10,10,0);
        c.anchor = GridBagConstraints.LINE_START;
        //controlPanel.add(movingAverageLabel,c);

        SpinnerModel spinnerModelAverage = new SpinnerNumberModel(17,1,1000,2);
        spinnerAverage = new JSpinner(spinnerModelAverage);
        c = new GridBagConstraints();
        c.gridx = 1; c.gridy = 2; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,10,10,10);
        c.anchor = GridBagConstraints.LINE_START;
        //controlPanel.add(spinnerAverage,c);

        includeRapid = new JCheckBox("Include Level 3 Chargers");
        includeRapid.setFont(Settings.DEFAULT_FONT);
        includeRapid.setSelected(true);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 3; c.weightx = 1; c.gridwidth = 2;
        c.insets = new Insets(0,10,0,0);
        c.anchor = GridBagConstraints.LINE_START;
        controlPanel.add(includeRapid,c);

        includeFast = new JCheckBox("Include Level 2 Chargers");
        includeFast.setFont(Settings.DEFAULT_FONT);
        includeFast.setSelected(true);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 4; c.weightx = 1; c.gridwidth = 2;
        c.insets = new Insets(0,10,5,0);
        c.anchor = GridBagConstraints.LINE_START;
        controlPanel.add(includeFast,c);

        selectorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectorList.setLayoutOrientation(JList.VERTICAL);
        selectorList.setSelectedIndex(0);
        selectorList.setFont(Settings.DEFAULT_FONT);
        JScrollPane selectorPanel = new JScrollPane(selectorList);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1; c.weighty = 1; c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5,5,0,5);
        selectionPanel.add(selectorPanel,c);

        JPanel buttonPanel = new JPanel();
        JButton openInfoButton = new JButton("Open Browser");
        openInfoButton.setFont(Settings.DEFAULT_FONT);
        JButton openMapsButton = new JButton("Show on Map");
        openMapsButton.setFont(Settings.DEFAULT_FONT);
        openMapsButton.setEnabled(false);
        buttonPanel.setLayout(new GridLayout(1,2));
        buttonPanel.add(openInfoButton);
        buttonPanel.add(openMapsButton);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 3; c.weightx=1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,10,10,10);
        selectionPanel.add(buttonPanel,c);

        selectorList.addListSelectionListener(e -> {
            if(selectorList.getSelectedIndex() == 0 || selectorList.getSelectedIndex() == 1) {
                openMapsButton.setEnabled(false);
                openInfoButton.setEnabled(true);
                app.getDataModel().rebuiltGeneralModel();
            } else{
                String selectedID = selectorList.getSelectedValue().split(" - ")[0];
                ChargerObject selectedCharger = app.getDataModel().getCharger(selectedID);
                app.getDetailsPanel().setInfoText(selectedCharger.getInfoString());
                app.getDetailsPanel().setAnalysisText(selectedCharger.getDetailsString());
                openMapsButton.setEnabled(true);
                openInfoButton.setEnabled(true);
            }
            app.repaint();
        });

        spinnerModelHorizontal.addChangeListener(e -> app.repaint());

        spinnerModelVertical.addChangeListener(e -> app.repaint());

        spinnerModelAverage.addChangeListener(e -> {
            app.getDataModel().rebuiltGeneralModel();
            app.repaint();
        });

        includeRapid.addActionListener(e -> {
            app.getDataModel().rebuiltGeneralModel();
            app.repaint();
        });

        includeFast.addActionListener(e -> {
            app.getDataModel().rebuiltGeneralModel();
            app.repaint();
        });

        openInfoButton.addActionListener(e -> {
            String id = getSelectedOption().split(":")[0];
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try{
                    if(id.equals("Show Aggregate Data") || id.equals("Show Moving Average")){
                        Desktop.getDesktop().browse(new URI("https://polar-network.com/live-map/"));
                    } else {
                        Desktop.getDesktop().browse(new URI("https://polar-network.com/charge-point-information/" + id +"/"));
                    }
                } catch (URISyntaxException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        openMapsButton.addActionListener(e -> {
            String id = getSelectedOption().split(" - ")[0];
            if(!id.equals("Show Aggregate Data") && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
                try{
                    Desktop.getDesktop().browse(new URI("https://www.google.com/maps/search/?api=1&query="+
                            URLEncoder.encode(app.getDataModel().getCharger(id).getAddress())));
                } catch (URISyntaxException | IOException e1) {
                    e1.printStackTrace();
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

    public Integer getHorizontalScale(){
        return (Integer)spinnerHorizontal.getValue();
    }

    public void setHorizontalScale(Integer newValue){
        if(newValue >1 && newValue < 1000000){
            spinnerHorizontal.setValue(newValue);
            app.repaint();
        }
    }

    public Integer getVerticalScale(){
        return (Integer)spinnerVertical.getValue();
    }

    public void setVerticalScale(Integer newValue){
        if(newValue >= 1 && newValue < 1000){
            spinnerVertical.setValue(newValue);
            app.repaint();
        }
    }

    public boolean isShowRapid(){
        return includeRapid.isSelected();
    }

    public boolean isShowFast(){
        return includeFast.isSelected();
    }

    public Integer getMovingAverageWidth(){
        return (Integer) spinnerAverage.getValue();
    }

    public void resetSelectedItem(){
        this.selectorList.setSelectedIndex(1);
        app.getDataModel().rebuiltGeneralModel();
        app.getDetailsPanel().setInfoText(app.getDataModel().getInfoString());
        app.getDetailsPanel().setAnalysisText(app.getDataModel().getAnalysisString());
    }

}
