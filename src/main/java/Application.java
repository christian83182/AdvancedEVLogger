import com.gargoylesoftware.htmlunit.WebClient;

import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {

    private CustomMenuBar menuBar;
    private GraphPanel graphPanel;
    private MenuPanel menuPanel;
    private WebClient client;
    private DataModel dataModel;

    Application(){
        super("Advanced EV Charging Logger");
        NotificationLogger.logger.addToLog("Starting Web Client...");
        this.client = new WebClient();
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
        //this.add(menuPanel, BorderLayout.WEST);

        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new FlowLayout(FlowLayout.LEADING,7,2));
        JButton logButton = new JButton("");
        logButton.addActionListener(e -> new ScrollableTextWindow("Notification History", new Dimension(450,500), NotificationLogger.logger.getAllLog()));
        notificationPanel.add(logButton);
        notificationPanel.add(NotificationLogger.logger.getLabel());
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

    public WebClient getWebClient(){
        return client;
    }

    public DataModel getDataModel(){
        return dataModel;
    }

    public CustomMenuBar getCustomMenuBar() {
        return menuBar;
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }

    public MenuPanel getMenuPanel() {
        return menuPanel;
    }
}
