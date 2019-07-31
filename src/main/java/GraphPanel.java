import java.awt.*;

public class GraphPanel extends InteractivePanel {

    private Application app;

    GraphPanel(Application app) {
        super(Settings.DEFAULT_PAN, Settings.DEFAULT_ZOOM);
        this.app = app;
        this.setPreferredSize(new Dimension(1500,900));
    }

    @Override
    public void paintView(Graphics2D g2) {
        paintBackground(g2);
        paintAxes(g2);
        //System.out.println(this.getPan().x + ", " + this.getPan().y + " : " +this.getZoom());
    }
    private void paintAxes(Graphics2D g2){
        g2.setColor(Color.WHITE);
        g2.drawLine(0,50,0,-100000);
        g2.drawLine(100000,0,-50,0);

        Integer xStep = new Integer(100);
        Integer yStep = new Integer(100);

        for(int i = 0; i < 500; i++){
            g2.drawLine(i*xStep,-10,i*xStep,0);
            g2.drawLine(0,i*-yStep,10,i*-yStep);
        }
    }

    private void paintBackground(Graphics2D g2){
        g2.setColor(Settings.BACKGROUND_COLOUR);
        g2.fillRect(-100000,-100000,200000,200000);
        g2.setColor(Settings.GRAPH_COLOUR);
        g2.fillRect(0,-100000,100000,100000);
    }
}
