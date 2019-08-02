import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphPanel extends InteractivePanel {

    private Application app;

    GraphPanel(Application app) {
        super(Settings.DEFAULT_PAN, Settings.DEFAULT_ZOOM, app);
        this.app = app;
        this.setPreferredSize(new Dimension(1500,900));
    }

    @Override
    public void paintView(Graphics2D g2) {
        paintBackground(g2);
        if(app.getMenuPanel().getSelectedOption().equals("Show All")){
            paintTotalChargersGraph(g2);
        } else {
            String id = app.getMenuPanel().getSelectedOption().split(" - ")[0];
            paintSingleChargerGraph(g2,id);
        }
        paintOverlay(g2);
    }

    private void paintOverlay(Graphics2D g2){
        //paint graph background
        g2.setColor(Settings.BACKGROUND_COLOUR);
        g2.fillRect(0,-10000,1000000,10000-getHeight()+130 );

        //paint legend
        g2.setTransform(new AffineTransform());
        paintScale(g2);
    }

    private void paintSingleChargerGraph(Graphics2D g2, String id){
        //paint graph background
        g2.setColor(Settings.GRAPH_COLOUR);
        g2.fillRect(0,-getHeight()+130,1000000,getHeight()-130);

        //paint axis, and yaxis markers
        g2.setColor(Color.WHITE);
        g2.drawLine(0,0,0,-getHeight()+130);
        g2.drawLine(0,0,1000000,0);

        //paint xaxis markers
        Integer xStep = app.getMenuPanel().getHorizontalScale();
        for(int i = 0; i < 500; i++){
            g2.drawLine(i*xStep,-15,i*xStep,0);
        }

        //paint yaxis markers
        Integer yStep = app.getMenuPanel().getVerticalScale();
        Integer yIncrement = (getHeight()-130)/yStep;
        for(int i = 1; i <= yStep; i++){
            g2.drawLine(0,-yIncrement*i,15,-yIncrement*i);
        }

        g2.setColor(new Color(164, 121, 58));
        ChargerObject charger = app.getDataModel().getChargeObject(id);
        List<Long> times = new ArrayList<>(charger.getLogTimes());
        Collections.sort(times);

        if(!times.isEmpty()){
            long startTime  = times.get(0);
            for(int i = 0; i < times.size()-1 ; i++){
                int x1 = (int)(long)(times.get(i) - startTime)/(3600000/xStep);
                int x2 = (int)(long)(times.get(i+1) - startTime)/(3600000/xStep);
                int y1 = -yIncrement * (charger.getEntryInLog(times.get(i)) ? 1 : 0);
                int y2 = -yIncrement * (charger.getEntryInLog(times.get(i+1)) ? 1 : 0);
                if(xStep < 200){
                    g2.setStroke(new BasicStroke((int)((xStep/200.0) * 4)+1));
                    g2.drawLine(x1,y1,x2,y2);
                    g2.fillRect((x2-xStep/20),(y2-xStep/20),xStep/10,xStep/10);
                } else {
                    g2.setStroke(new BasicStroke(5));
                    g2.drawLine(x1,y1,x2,y2);
                    g2.fillRect((x2-10),(y2-10),20,20);
                }
            }
        }
    }

    private void paintTotalChargersGraph(Graphics2D g2){
        //paint graph background
        g2.setColor(Settings.GRAPH_COLOUR);
        g2.fillRect(0,-getHeight()+130,1000000,getHeight()-130);

        //paint axis lines
        g2.setColor(Color.WHITE);
        g2.drawLine(0,0,0,-getHeight()+130);
        g2.drawLine(100000,0,0,0);

        //paint graph background
        g2.setColor(Settings.GRAPH_COLOUR);
        g2.fillRect(0,-getHeight()+130,1000000,getHeight()-130);

        //paint axis, and yaxis markers
        g2.setColor(Color.WHITE);
        g2.drawLine(0,0,0,-getHeight()+130);
        g2.drawLine(0,0,1000000,0);

        //paint xaxis markers
        Integer xStep = app.getMenuPanel().getHorizontalScale();
        for(int i = 0; i < 500; i++){
            g2.drawLine(i*xStep,-15,i*xStep,0);
        }

        //paint yaxis markers
        Integer yStep = app.getMenuPanel().getVerticalScale();
        Integer yIncrement = (getHeight()-130)/yStep;
        for(int i = 1; i <= yStep; i++){
            g2.drawLine(0,-yIncrement*i,15,-yIncrement*i);
        }

        g2.setColor(new Color(32, 100, 164));
        List<Long> times = new ArrayList<>(app.getDataModel().getTotalChargersKeySet());
        Collections.sort(times);

        if(!times.isEmpty()){
            long startTime  = times.get(0);
            for(int i = 0; i < times.size()-1 ; i++){
                int x1 = (int)(long)(times.get(i) - startTime)/(3600000/xStep);
                int y1 = -app.getDataModel().getTotalChargersAtTime(times.get(i))*yIncrement;
                int x2 = (int)(long)(times.get(i+1) - startTime)/(3600000/xStep);
                int y2 = -app.getDataModel().getTotalChargersAtTime(times.get(i+1))*yIncrement;

                if(xStep < 200){
                    g2.setStroke(new BasicStroke((int)((xStep/200.0) * 4)+1));
                    g2.drawLine(x1,y1,x2,y2);
                    g2.fillRect((x2-xStep/20),(y2-xStep/20),xStep/10,xStep/10);
                } else {
                    g2.setStroke(new BasicStroke(5));
                    g2.drawLine(x1,y1,x2,y2);
                    g2.fillRect((x2-10),(y2-10),20,20);
                }
            }
        }
    }

    private void paintBackground(Graphics2D g2){
        g2.setColor(Settings.BACKGROUND_COLOUR);
        g2.fillRect(-100000,-100000,200000,200000);
    }
}
