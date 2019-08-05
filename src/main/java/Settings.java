import java.awt.*;

public class Settings {

    static Double MAX_ZOOM = 5.0;
    static Double MIN_ZOOM = 0.05;
    static Point DEFAULT_PAN = new Point(70, 983);
    static Double DEFAULT_ZOOM = 1.0;

    static Color BACKGROUND_COLOUR = new Color(44, 44, 44);
    static Color GRAPH_COLOUR = new Color(48, 48, 48);
    static Color GRID_COLOUR = new Color(57, 57, 57);

    static Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 15);

    static String[] FAST_CHARGERS = {"CHAdeMO","DC Combo Type 2"};

    static Long LOG_INTERVAL = new Long(900000);

}
