import com.gargoylesoftware.htmlunit.WebClient;

import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {

    private CustomMenuBar menuBar;
    private GraphPanel graphPanel;
    private MenuPanel menuPanel;
    private WebClient client;
    private DataModel dataModel;
    private volatile boolean isLogging;
    JLabel statusLabel;

    Application(){
        super("Advanced EV Charging Logger");
        NotificationLogger.logger.addToLog("Starting Web Client...");
        this.client = new WebClient();
        this.isLogging = false;
        this.dataModel = new DataModel(this);
        init();
    }

    private void init(){
        NotificationLogger.logger.addToLog("Starting UI...");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        setLookAndFeel();

        menuBar = new CustomMenuBar(this);
        graphPanel = new GraphPanel(this);
        menuPanel = new MenuPanel(this);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerSize(2);
        splitPane.setLeftComponent(menuPanel);
        splitPane.setRightComponent(graphPanel);
        splitPane.setBorder(BorderFactory.createMatteBorder(1,1,1,1,new Color(42, 42, 42)));

        this.add(splitPane,BorderLayout.CENTER);

        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel,BoxLayout.LINE_AXIS));
        JButton logButton = new JButton("");
        logButton.addActionListener(e -> new ScrollableTextWindow("Notification History", new Dimension(450,500), NotificationLogger.logger.getAllLog()));
        notificationPanel.add(logButton);
        notificationPanel.add(NotificationLogger.logger.getLabel());
        notificationPanel.add(Box.createHorizontalGlue());

        statusLabel = new JLabel("Logging Status: INACTIVE");
        notificationPanel.add(statusLabel);
        notificationPanel.add(Box.createRigidArea(new Dimension(10,1)));

        this.add(notificationPanel, BorderLayout.SOUTH);
        this.setJMenuBar(menuBar);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void setLookAndFeel(){
        UIManager.put("control", new Color(55, 55, 55)); // Primary
        UIManager.put("nimbusBase", new Color(42, 42, 42)); // The colour of selectors
        UIManager.put("nimbusBlueGrey", new Color(52, 52, 52)); // The colour of buttons
        UIManager.put("text",new Color(255,255,255)); //Sets Default text colour to white
        UIManager.put("ScrollPane.background", Color.DARK_GRAY); //Background for the ScrollPane (affects JFileChooser)
        UIManager.put("List.background", Color.DARK_GRAY); //Background for the ScrollPane (affects JFileChooser)
        UIManager.put("TextField.background", Color.DARK_GRAY); //Background for the TextField (affects JFileChooser)
        UIManager.put("Menu[Enabled].textForeground",new Color(255, 255, 255));
        UIManager.put("nimbusFocus",new Color(0, 104, 208));
        UIManager.put("nimbusLightBackground",new Color(74, 74, 74));
        UIManager.put("nimbusSelectionBackground",new Color(0, 104, 208));
        UIManager.put("List.background",new Color(80, 80, 80));
        UIManager.put("List[Selected].textForeground",new Color(250, 251, 255));
        UIManager.put("Slider.tickColor",new Color(250, 251, 255));
        UIManager.put("nimbusDisabledText",new Color(83, 83, 83));

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

    public synchronized WebClient getWebClient(){
        return client;
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

    public synchronized void setLogging(boolean isLogging){
        this.isLogging = isLogging;
        if(isLogging){
            statusLabel.setText("Logging Status: ACTIVE");
        } else {
            statusLabel.setText("Logging Status: INACTIVE");
        }
    }

    public synchronized boolean isLogging(){
        return isLogging;
    }
}
