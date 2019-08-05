import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    public void fitToWindow(){
        List<Long> times = new ArrayList<>(app.getDataModel().getGeneralLogKey());
        Collections.sort(times);
        if(!times.isEmpty()){
            Double adjustedWidth = this.getWidth()*0.85;
            Double totalHours = (times.get(times.size()-1) - times.get(0))/3600000.0;
            app.getMenuPanel().setHorizontalScale((int)(adjustedWidth/totalHours));

            Integer maxValue = 0;
            for(Long time : times){
                if(app.getDataModel().getGeneralLogEntry(time) > maxValue){
                    maxValue = app.getDataModel().getGeneralLogEntry(time);
                }
            }
            app.getMenuPanel().setVerticalScale(maxValue+1);
        }
    }

    private void paintSingleChargerGraph(Graphics2D g2, String id){
        Integer xStep = app.getMenuPanel().getHorizontalScale();
        Integer yStep = app.getMenuPanel().getVerticalScale();
        Integer yIncrement = (getHeight()-130)/yStep;
        ChargerObject charger = app.getDataModel().getCharger(id);
        List<Long> times = new ArrayList<>(charger.getLogTimes());
        Collections.sort(times);
        g2.setFont(Settings.DEFAULT_FONT);
        FontMetrics fontMetrics = g2.getFontMetrics();

        //paint graph background
        g2.setColor(Settings.GRAPH_COLOUR);
        g2.fillRect(0,-yStep*yIncrement,1000000,yStep*yIncrement);

        //paint yaxis and markers & labels
        g2.setColor(Color.WHITE);
        g2.drawLine(0,0,0,-yStep*yIncrement);
        for(int i = 1; i <= yStep; i++){
            if(app.getMenuPanel().isShowGrid()){
                g2.setColor(Settings.GRID_COLOUR);
                g2.drawLine(0,-yIncrement*i,1000000,-yIncrement*i);
            }
            g2.setColor(Color.WHITE);
            g2.drawLine(0,-yIncrement*i,15,-yIncrement*i);
            Integer labelLength = fontMetrics.stringWidth(""+i);
            Integer labelHeight = fontMetrics.getHeight();
            g2.drawString(""+i,-labelLength-10,-yIncrement*i + labelHeight/2);
        }

        //draw x axis line
        g2.setColor(Color.WHITE);
        g2.drawLine(0,0,1000000,0);

        //draw x axis labels and markers
        DateFormat simple = new SimpleDateFormat("dd/MM/yy HH:mm");
        for(int i = 1; i < 10000; i++) {
            if (!times.isEmpty()) {
                String label = simple.format(times.get(0) + i * 60000);
                Integer stringLength = fontMetrics.stringWidth(label);
                Integer stringHeight = fontMetrics.getHeight();
                Integer textXpos = (int)(i * xStep/60.0 - (stringLength / 2.0));
                Integer tickXpos = (int)(i * xStep/60.0);

                if(xStep < 9 && xStep >= 1 && i % 1440 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                } else if(xStep < 23 && xStep >= 9 && i % 720 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                }else if(xStep < 55 && xStep >= 23 && i % 300 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                }else if(xStep < 107 && xStep >= 55 && i % 120 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                } else if(xStep < 420 && xStep >= 107 && i % 60 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                } else if(xStep < 1333 && xStep >= 420 && i % 15 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                } else if(xStep < 6666 && xStep >= 1333 && i % 5 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                }  else if(xStep >= 6666){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);
                }
            }
        }

        g2.setColor(new Color(164, 121, 58));

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
        Integer xStep = app.getMenuPanel().getHorizontalScale();
        Integer yStep = app.getMenuPanel().getVerticalScale();
        Integer yIncrement = (getHeight()-130)/yStep;
        List<Long> times = new ArrayList<>(app.getDataModel().getGeneralLogKey());
        Collections.sort(times);

        //paint graph background
        g2.setColor(Settings.GRAPH_COLOUR);
        g2.fillRect(0,-yStep*yIncrement,1000000,yStep*yIncrement);

        //paint yaxis and markers
        g2.setColor(Color.WHITE);
        g2.drawLine(0,0,0,-yStep*yIncrement);
        for(int i = 1; i <= yStep; i++){
            if(app.getMenuPanel().isShowGrid()){
                g2.setColor(Settings.GRID_COLOUR);
                g2.drawLine(0,-yIncrement*i,1000000,-yIncrement*i);
            }
            g2.setColor(Color.WHITE);
            g2.drawLine(0,-yIncrement*i,15,-yIncrement*i);
        }

        //draw x axis line
        g2.setColor(Color.WHITE);
        g2.drawLine(0,0,1000000,0);

        //draw x axis labels and markers
        g2.setFont(Settings.DEFAULT_FONT);
        FontMetrics fontMetrics = g2.getFontMetrics();
        DateFormat simple = new SimpleDateFormat("dd/MM/yy HH:mm");
        for(int i = 1; i < 10000; i++) {
            if (!times.isEmpty()) {
                String label = simple.format(times.get(0) + i * 60000);
                Integer stringLength = fontMetrics.stringWidth(label);
                Integer stringHeight = fontMetrics.getHeight();
                Integer textXpos = (int)(i * xStep/60.0 - (stringLength / 2.0));
                Integer tickXpos = (int)(i * xStep/60.0);

                if(xStep < 9 && xStep >= 1 && i % 1440 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                } else if(xStep < 23 && xStep >= 9 && i % 720 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                }else if(xStep < 55 && xStep >= 23 && i % 300 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                }else if(xStep < 107 && xStep >= 55 && i % 120 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                } else if(xStep < 420 && xStep >= 107 && i % 60 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                } else if(xStep < 1333 && xStep >= 420 && i % 15 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                } else if(xStep < 6666 && xStep >= 1333 && i % 5 == 0){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);

                }  else if(xStep >= 6666){
                    if(app.getMenuPanel().isShowGrid()){
                        g2.setColor(Settings.GRID_COLOUR);
                        g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString(label, textXpos, stringHeight + 10);
                    g2.drawLine(tickXpos,0, tickXpos,-10);
                }
            }
        }

        //paint yaxis labels
        for(int i = 1; i <= yStep; i++){
            Integer labelLength = fontMetrics.stringWidth(""+i);
            Integer labelHeight = fontMetrics.getHeight();
            g2.drawString(""+i,-labelLength-10,-yIncrement*i + labelHeight/2);
        }

        g2.setColor(new Color(32, 100, 164));
        if(!times.isEmpty()){
            long startTime  = times.get(0);
            for(int i = 0; i < times.size()-1 ; i++){
                int x1 = (int)(long)(times.get(i) - startTime)/(3600000/xStep);
                int y1 = -app.getDataModel().getGeneralLogEntry(times.get(i))*yIncrement;
                int x2 = (int)(long)(times.get(i+1) - startTime)/(3600000/xStep);
                int y2 = -app.getDataModel().getGeneralLogEntry(times.get(i+1))*yIncrement;

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
