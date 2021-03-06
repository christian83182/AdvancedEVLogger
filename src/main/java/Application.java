import com.gargoylesoftware.htmlunit.WebClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application extends JFrame {

    //todo add notifications to exporting (make sure they appear before the final logging message

    private CustomMenuBar menuBar;
    private GraphPanel graphPanel;
    private MenuPanel menuPanel;
    private DataModel dataModel;
    private LoggingThread loggingThread;
    private WebClient webClient;
    private JLabel statusLabel;
    private JLabel logIntervalLabel;
    private DetailsPane detailsPanel;
    private JSplitPane detailsSplitPanel;
    private volatile boolean isLogging;

    Application(){
        super("Advanced EV Charging Logger");
        NotificationLogger.logger.addToLog("Starting Web Client...");
        this.webClient = new WebClient();
        this.isLogging = false;
        this.dataModel = new DataModel(this);
        this.loggingThread = new LoggingThread(this);
        init();
    }

    private void init(){
        NotificationLogger.logger.addToLog("Starting UI...");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());

        setLookAndFeel();

        menuBar = new CustomMenuBar(this);
        menuPanel = new MenuPanel(this);
        graphPanel = new GraphPanel(this);
        detailsPanel = new DetailsPane(this);

        JSplitPane firstSplitPane = new JSplitPane();
        firstSplitPane.setDividerSize(3);
        firstSplitPane.setLeftComponent(menuPanel);
        firstSplitPane.setRightComponent(graphPanel);
        firstSplitPane.setBorder(BorderFactory.createMatteBorder(1,1,1,1,new Color(42, 42, 42)));

        detailsSplitPanel = new JSplitPane();
        detailsSplitPanel.setDividerSize(3);
        detailsSplitPanel.setLeftComponent(firstSplitPane);
        detailsSplitPanel.setRightComponent(detailsPanel);
        detailsSplitPanel.setBorder(BorderFactory.createMatteBorder(1,1,1,1,new Color(42, 42, 42)));

        this.add(detailsSplitPanel,BorderLayout.CENTER);

        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel,BoxLayout.LINE_AXIS));
        JButton logButton = new JButton("");
        logButton.addActionListener(e -> new ScrollableTextWindow("Notification History", new Dimension(900,700), NotificationLogger.logger.getAllLog()));
        notificationPanel.add(logButton);
        notificationPanel.add(NotificationLogger.logger.getLabel());
        notificationPanel.add(Box.createHorizontalGlue());

        JLabel logIntervalTextLabel = new JLabel("Logging Interval: ");
        logIntervalTextLabel.setFont(Settings.DEFAULT_FONT);
        notificationPanel.add(logIntervalTextLabel);

        logIntervalLabel = new JLabel(Settings.LOG_INTERVAL.toString());
        this.setLogInterval(Settings.LOG_INTERVAL);
        notificationPanel.add(logIntervalLabel);
        notificationPanel.add(Box.createRigidArea(new Dimension(30,1)));

        JLabel statusTextLabel = new JLabel("Logging Status: ");
        statusTextLabel.setFont(Settings.DEFAULT_FONT);
        notificationPanel.add(statusTextLabel);

        statusLabel = new JLabel("");
        statusLabel.setFont(Settings.DEFAULT_FONT);
        notificationPanel.add(statusLabel);
        this.setLogging(false);
        notificationPanel.add(Box.createRigidArea(new Dimension(10,1)));

        this.add(notificationPanel, BorderLayout.SOUTH);
        this.setJMenuBar(menuBar);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you wish to exit?", "Exit Program Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void closeDetailsPanel(){
        detailsSplitPanel.setDividerSize(0);
        detailsSplitPanel.setDividerLocation(getWidth());
    }

    public void openDetailsPanel(){
        detailsSplitPanel.setDividerSize(3);
        detailsSplitPanel.setDividerLocation(0.7);
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

    public WebClient getWebClient(){
        return webClient;
    }

    public synchronized void setLogging(boolean isLogging){
        this.isLogging = isLogging;
        if(isLogging){
            statusLabel.setText("ACTIVE");
            statusLabel.setForeground(new Color(66, 155, 58));
            NotificationLogger.logger.addToLog("Logging started...");
        } else {
            statusLabel.setText("INACTIVE");
            statusLabel.setForeground(new Color(163, 0, 9));
            NotificationLogger.logger.addToLog("Logging stopped...");
        }
    }

    public synchronized boolean isLogging(){
        return isLogging;
    }

    public void setLogInterval(Long interval){
        Settings.LOG_INTERVAL = interval;
        this.logIntervalLabel.setText(interval / 60000 +"m");
        this.repaint();
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
