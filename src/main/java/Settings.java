import java.awt.*;

public class Settings {

    static Point DEFAULT_PAN = new Point(70, 983);
    static Double DEFAULT_ZOOM = 1.0;

    static Color BACKGROUND_COLOUR = new Color(36, 36, 36);
    static Color FIRST_GRAPH_COLOUR = new Color(41, 41, 41);
    static Color SECOND_GRAPH_COLOUR = new Color(47, 47, 47);
    static Color GRID_COLOUR = new Color(59, 59, 59);

    static Color GENERATED_LINE_COLOUR = new Color(164, 44, 47);
    static Color MOVING_AVERAGE_COLOUR = new Color(0, 164, 161);
    static Color TOTAL_CHARGERS_COLOUR = new Color(162, 55, 118);
    static Color SINGLE_CHARGER_COLOUR = new Color(164, 121, 58);

    static Color ALL_CHARGERS_COLOUR = new Color(32, 100, 164);
    static Color ALL_CHARGERS_FILL = new Color(ALL_CHARGERS_COLOUR.getRed(),ALL_CHARGERS_COLOUR.getGreen(),ALL_CHARGERS_COLOUR.getBlue(),40);
    static Color SINGLE_CHARGER_FILL = new Color(SINGLE_CHARGER_COLOUR.getRed(),SINGLE_CHARGER_COLOUR.getGreen(),SINGLE_CHARGER_COLOUR.getBlue(),40);
    static Color MOVING_AVERAGE_FILL = new Color(MOVING_AVERAGE_COLOUR.getRed(),MOVING_AVERAGE_COLOUR.getGreen(),MOVING_AVERAGE_COLOUR.getBlue(),40);
    static Color GENERATED_FILL = new Color(GENERATED_LINE_COLOUR.getRed(),GENERATED_LINE_COLOUR.getGreen(),GENERATED_LINE_COLOUR.getBlue(),40);
    static Color GENERATED_HIGHLIGHT_COLOUR = new Color(GENERATED_LINE_COLOUR.getRed(),GENERATED_LINE_COLOUR.getGreen(),GENERATED_LINE_COLOUR.getBlue(),30);

    static Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 10);

    static String[] FAST_CHARGERS = {"CHAdeMO","DC Combo Type 2"};

    static Long LOG_INTERVAL = 900000L;

}
