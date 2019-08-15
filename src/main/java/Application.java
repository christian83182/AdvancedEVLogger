import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Application extends JFrame {

    private CustomMenuBar menuBar;
    private GraphPanel graphPanel;
    private MenuPanel menuPanel;
    private DataModel dataModel;
    private DetailsPane detailsPanel;
    private volatile boolean isLogging;

    Application(){
        super("Advanced EV Charging Logger");
        this.isLogging = false;
        this.dataModel = new DataModel(this);
        init();
    }

    private void init(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(true);
        this.setLayout(new BorderLayout());

        setLookAndFeel();

        menuBar = new CustomMenuBar(this);
        menuPanel = new MenuPanel(this);
        graphPanel = new GraphPanel(this);
        detailsPanel = new DetailsPane(this);

        this.add(graphPanel,BorderLayout.CENTER);
        this.add(menuPanel, BorderLayout.WEST);
        this.add(detailsPanel, BorderLayout.EAST);
        //this.setJMenuBar(menuBar);

        try {
            getCustomMenuBar().importConfigHelper("C:\\Users\\chris\\IdeaProjects\\AdvancedEVLogger\\LogFiles\\Log_Recent.xml");
            getDataModel().repairDataModel();
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        maximizeWindow();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.setVisible(true);
        getGraphPanel().fitGraphToWindow(true);

    }

    public synchronized DataModel getDataModel(){
        return dataModel;
    }

    public synchronized CustomMenuBar getCustomMenuBar() {
        return menuBar;
    }

    public synchronized GraphPanel getGraphPanel() {
        return graphPanel;
    }

    public synchronized MenuPanel getMenuPanel() {
        return menuPanel;
    }

    public synchronized DetailsPane getDetailsPanel(){
        return detailsPanel;
    }

    public synchronized boolean isLogging(){
        return isLogging;
    }

    public synchronized void maximizeWindow(){
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
    }

    private void setLookAndFeel(){
        UIManager.put("control", new Color(45, 45, 45)); // Primary
        UIManager.put("nimbusBase", new Color(33, 33, 33)); // The colour of selectors
        UIManager.put("nimbusBlueGrey", new Color(41, 41, 41)); // The colour of buttons
        UIManager.put("text",new Color(255,255,255)); //Sets Default text colour to white
        UIManager.put("TextArea.background", new Color(64, 64, 64));
        UIManager.put("ScrollPane.background", Color.DARK_GRAY); //Background for the ScrollPane (affects JFileChooser)
        UIManager.put("List.background", Color.DARK_GRAY); //Background for the ScrollPane (affects JFileChooser)
        UIManager.put("TextField.background", Color.DARK_GRAY); //Background for the TextField (affects JFileChooser)
        UIManager.put("Menu[Enabled].textForeground",new Color(255, 255, 255));
        UIManager.put("nimbusFocus",new Color(0, 104, 208));
        UIManager.put("nimbusLightBackground",new Color(66, 66, 66));
        UIManager.put("nimbusSelectionBackground",new Color(0, 104, 208));
        UIManager.put("List.background",new Color(72, 72, 72));
        UIManager.put("List[Selected].textForeground",new Color(250, 251, 255));
        UIManager.put("Slider.tickColor",new Color(250, 251, 255));
        UIManager.put("nimbusDisabledText",new Color(71, 71, 71));

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Nimbus not available, using default 'Metal'");
        }
    }


}
