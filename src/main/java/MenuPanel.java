import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    private DefaultListModel<String> selectorModel;
    private JList<String> selectorList;
    private Application app;

    MenuPanel(Application app){
        this.app = app;
        selectorModel = new DefaultListModel<String>();
        selectorModel.addElement("None");
        selectorList = new JList<String>(selectorModel);
        init();
    }

    private void init(){
        this.setPreferredSize(new Dimension(350,100));

        this.setLayout(new GridBagLayout());
        GridBagConstraints c;

        JLabel title = new JLabel("EV CHARGERS");
        title.setFont(Settings.TITLE_FONT);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5,0,5,0);
        this.add(title,c);

        selectorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectorList.setLayoutOrientation(JList.VERTICAL);
        JScrollPane selectorPanel = new JScrollPane(selectorList);

        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1; c.weightx = 1; c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0,20,5,20);
        this.add(selectorPanel,c);

        JTextArea infoArea = new JTextArea(1,1);
        infoArea.setPreferredSize(new Dimension(10,300));
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);

        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,20,20,20);
        this.add(infoArea,c);

        selectorList.addListSelectionListener(e -> {
            if(e.getValueIsAdjusting()){
                if(selectorList.getSelectedIndex() == 0){
                    infoArea.setText("");
                } else{
                    String selectedID = selectorList.getSelectedValue().split(" - ")[0];
                    ChargerObject selectedCharger = app.getDataModel().getChargeObject(selectedID);
                    String newText = "NAME: " + selectedCharger.getName();
                    newText+= "\nPRICE: " + selectedCharger.getPrice() +"p";
                    newText+= "\nOUTPUT: " + selectedCharger.getPowerOutput()+"kW";
                    newText+= "\nRAPID: " + selectedCharger.isRapid();
                    newText+= "\nADDRESS: " + selectedCharger.getAddress();
                    newText+= "\nINFO: " + selectedCharger.getAdditionalInfo().substring(1);
                    infoArea.setText(newText);
                }
            }
        });
    }

    public synchronized void addMenuItem(String item){
        selectorModel.addElement(item);
        this.repaint();
    }

}
