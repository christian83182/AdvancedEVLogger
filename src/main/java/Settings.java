import java.awt.*;

public class Settings {

    static Point DEFAULT_PAN = new Point(70, 983);
    static Double DEFAULT_ZOOM = 1.0;

    static Color BACKGROUND_COLOUR = new Color(36, 36, 36);
    static Color FIRST_GRAPH_COLOUR = new Color(41, 41, 41);
    static Color SECOND_GRAPH_COLOUR = new Color(47, 47, 47);
    static Color GRID_COLOUR = new Color(59, 59, 59);

    static Color GENERATED_HIGHLIGHT_COLOUR = new Color(164, 0, 5,35);
    static Color GENERATED_LINE_COLOUR = new Color(164, 44, 47);
    static Color MOVING_AVERAGE_COLOUR = new Color(0, 164, 161);
    static Color TOTAL_CHARGERS_COLOUR = new Color(162, 55, 118);
    static Color SINGLE_CHARGER_COLOUR = new Color(164, 121, 58);
    static Color ALL_CHARGERS_COLOUR = new Color(32, 100, 164);

    static Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 15);

    static String[] FAST_CHARGERS = {"CHAdeMO","DC Combo Type 2"};

    static Long LOG_INTERVAL = 900000L;

}
