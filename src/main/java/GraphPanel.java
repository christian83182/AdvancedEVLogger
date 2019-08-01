import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        if(app.getMenuPanel().getSelectedOption().equals("Show All")){
            paintTotalChargersGraph(g2);
        } else {

        }
    }

    private void paintTotalChargersGraph(Graphics2D g2){
        g2.setColor(new Color(32, 100, 164));
        g2.setStroke(new BasicStroke(3));
        List<Long> times = new ArrayList<>(app.getDataModel().getTotalChargersKeySet());
        Collections.sort(times);

        if(!times.isEmpty()){
            long startTime  = times.get(0);
            for(int i = 0; i < times.size()-1 ; i++){
                int x1 = (int)(long)(times.get(i) - startTime)/1000;
                int y1 = -app.getDataModel().getTotalChargersAtTime(times.get(i))*100;
                int x2 = (int)(long)(times.get(i+1) - startTime)/1000;
                int y2 = -app.getDataModel().getTotalChargersAtTime(times.get(i+1))*100;
                g2.drawLine(x1,y1,x2,y2);

                g2.fillRect(x2-10,y2-10,20,20);
            }
        }
    }

    private void paintAxes(Graphics2D g2){
        g2.setColor(Color.WHITE);
        g2.drawLine(0,50,0,-100000);
        g2.drawLine(100000,0,-50,0);

        Integer xStep = 100;
        Integer yStep = 100;

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
