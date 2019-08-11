import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class DetailsPane extends JPanel {

    private Application app;
    private JTextArea infoArea;
    private JTextArea analysisArea;

    DetailsPane(Application app){
        this.app = app;
        init();
    }

    private void init(){
        this.setLayout(new GridBagLayout());
        GridBagConstraints c;

        JPanel detailsPanel = new JPanel();
        TitledBorder detailsBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY,1),"General Information",0,2,
                Settings.DEFAULT_FONT,Color.WHITE);
        detailsBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 18));
        detailsPanel.setBorder(detailsBorder);
        detailsPanel.setLayout(new GridBagLayout());

        JPanel analysisPanel = new JPanel();
        TitledBorder controlBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY,1),"Data Analysis",0,2,
                Settings.DEFAULT_FONT,Color.WHITE);
        controlBorder.setTitleFont(new Font("SansSerif", Font.BOLD, 18));
        analysisPanel.setBorder(controlBorder);
        analysisPanel.setLayout(new GridBagLayout());

        c = new GridBagConstraints(); c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty =0.5;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10,20,10,20);
        this.add(detailsPanel,c);

        c = new GridBagConstraints(); c.gridx = 0; c.gridy = 2; c.weightx = 1; c.weighty=1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10,20,10,20);
        this.add(analysisPanel,c);

        infoArea = new JTextArea();
        infoArea.setLineWrap(true);
        infoArea.setFont(Settings.DEFAULT_FONT);
        infoArea.setPreferredSize(new Dimension(350,10));
        JScrollPane infoScrollPanel = new JScrollPane(infoArea);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 0.4;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5,5,5,5);
        detailsPanel.add(infoScrollPanel, c);

        analysisArea = new JTextArea();
        analysisArea.setLineWrap(true);
        analysisArea.setFont(Settings.DEFAULT_FONT);
        analysisArea.setPreferredSize(new Dimension(350,10));
        JScrollPane analysisScrollArea = new JScrollPane(analysisArea);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1; c.weightx = 1; c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5,5,5,5);
        analysisPanel.add(analysisScrollArea, c);

        JButton closePanelButton = new JButton("Close Panel");
        closePanelButton.setFont(Settings.DEFAULT_FONT);
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 3; c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0,10,10,10);
        this.add(closePanelButton,c);
        closePanelButton.addActionListener( e-> app.closeDetailsPanel());
    }

    public void setInfoText(String newText){
        infoArea.setText(newText);
    }

    public void setAnalysisText(String newText){
        analysisArea.setText(newText);
    }
}
