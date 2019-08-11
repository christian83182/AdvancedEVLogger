import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * A class used to display a graph using the DataModel
 */
public class GraphPanel extends InteractivePanel {

    //and instance of the Application class used by the program
    private Application app;
    //a Point object used to track the position of the mouse;
    private Point mouseLocation;

    //Initialize all member variables & call super methods
    GraphPanel(Application app) {
        super(Settings.DEFAULT_PAN, Settings.DEFAULT_ZOOM, app);
        this.app = app;
        this.mouseLocation = new Point();
        this.addMouseMotionListener(new MousePositionListener());
        this.setPreferredSize(new Dimension(1500,900));
    }

    @Override
    public void paintView(Graphics2D g2) {
        //Paint a background
        paintBackground(g2);

        //Paint the graph
        paintChargerGraph(g2);

        //Paint the overlay
        paintOverlay(g2);
    }

    //Paints the overlay
    private void paintOverlay(Graphics2D g2){
        //paint graph background
        g2.setColor(Settings.BACKGROUND_COLOUR);
        g2.fillRect(0,-10000,1000000,10000-getHeight()+130 );

        //paint legend
        g2.setTransform(new AffineTransform());
        paintScale(g2);
    }

    //Changes xStep such that all data is fit within the window.
    public void fitGraphToWindow(Boolean includeTotalValue){
        //Create and sort a list of all times
        List<Long> times = new ArrayList<>(app.getDataModel().getGeneralLogKey());
        Collections.sort(times);
        if(!times.isEmpty()){
            //Adjusted width accounts for some extra room around the graph
            Double adjustedWidth = this.getWidth()*0.85;
            //Calculate the total number of hours the data represents
            Double totalHours = (times.get(times.size()-1) - times.get(0))/3600000.0;
            //Change the horizontal scale such that the total hours fit within the width
            app.getMenuPanel().setHorizontalScale((int)(adjustedWidth/totalHours));

            //Iterate over all values, find the maximum value, and change the vertical width accordingly.
            Double maxValue = 0.0;
            if(includeTotalValue){
                maxValue = (double)app.getDataModel().getIds().size();
            } else {
                for(Long time : times){
                    if(app.getDataModel().getGeneralLogEntry(time) > maxValue){
                        maxValue = app.getDataModel().getGeneralLogEntry(time);
                    }
                }
            }
            app.getMenuPanel().setVerticalScale(maxValue.intValue()+1);

            this.setPan(new Point(Settings.DEFAULT_PAN.x,Settings.DEFAULT_PAN.y));
        }
    }

    //paints the charger graph
    private void paintChargerGraph(Graphics2D g2){
        //Create member variables
        Integer xStep = app.getMenuPanel().getHorizontalScale();
        Integer yStep = app.getMenuPanel().getVerticalScale();
        Integer yIncrement = (getHeight()-130)/yStep;
        List<Long> times;
        FontMetrics fontMetrics = g2.getFontMetrics();

        //populate times according to the selected option
        if(app.getMenuPanel().getSelectedOption().equals("Show All") ||
            app.getMenuPanel().getSelectedOption().equals("Show Moving Average")){
            times = new ArrayList<>(app.getDataModel().getGeneralLogKey());
        } else {
            String id = app.getMenuPanel().getSelectedOption().split(" - ")[0];
            ChargerObject charger = app.getDataModel().getCharger(id);
            times = new ArrayList<>(charger.getLogTimes());
        }
        Collections.sort(times);

        //paint graph background
        g2.setColor(Settings.FIRST_GRAPH_COLOUR);
        g2.fillRect(0,-yStep*yIncrement,1000000,yStep*yIncrement);

        //paint the alternating colours for days
        if(!times.isEmpty()){
            g2.setColor(Settings.SECOND_GRAPH_COLOUR);
            double firstDayWidth = (double)(xStep)*((86400000-(times.get(0) % 86400000.0))/3600000.0 - 1);
            g2.fillRect(0,-yStep*yIncrement,(int)firstDayWidth,yStep*yIncrement);

            //int numOfDays = (int)Math.floor((times.get(times.size()-1) - times.get(0))/86400000.0);
            for(int i =1; i < 10; i += 2){
                double xPos = firstDayWidth + i* xStep*24;
                g2.fillRect((int)xPos,-yStep*yIncrement, 24*xStep, yStep*yIncrement);
            }
        }

        //paint y axis
        g2.setColor(Color.WHITE);
        g2.drawLine(0,0,0,-yStep*yIncrement);

        //paint the y axis ticks and labels
        for(int i = 1; i <= yStep; i++){
            //draw gridlines if the option is enabled
            if(app.getMenuPanel().isShowGrid()){
                g2.setColor(Settings.GRID_COLOUR);
                g2.drawLine(0,-yIncrement*i,1000000,-yIncrement*i);
            }
            g2.setColor(Color.WHITE);

            //draw a tick line
            g2.drawLine(0,-yIncrement*i,15,-yIncrement*i);

            //draw the label
            Integer labelLength = fontMetrics.stringWidth(""+i);
            Integer labelHeight = fontMetrics.getHeight();
            g2.drawString(""+i,-labelLength-10,-yIncrement*i + labelHeight/2);
        }

        //draw x axis line
        g2.setColor(Color.WHITE);
        g2.drawLine(0,0,1000000,0);

        //draw x axis labels and markers
        if (!times.isEmpty()) {
            long numOfLabels = (times.get(times.size()-1) - times.get(0))/60000;
            for(int i = 1; i < numOfLabels; i++) {
                if(xStep < 9 && xStep >= 1 && i % 1440 == 0){
                    drawXAxisHelp(g2,times,i);
                } else if(xStep < 23 && xStep >= 9 && i % 720 == 0){
                    drawXAxisHelp(g2,times,i);
                }else if(xStep < 55 && xStep >= 23 && i % 300 == 0){
                    drawXAxisHelp(g2,times,i);
                }else if(xStep < 107 && xStep >= 55 && i % 120 == 0){
                    drawXAxisHelp(g2,times,i);
                } else if(xStep < 420 && xStep >= 107 && i % 60 == 0){
                    drawXAxisHelp(g2,times,i);
                } else if(xStep < 1333 && xStep >= 420 && i % 15 == 0){
                    drawXAxisHelp(g2,times,i);
                } else if(xStep < 6666 && xStep >= 1333 && i % 5 == 0){
                    drawXAxisHelp(g2,times,i);
                }  else if(xStep >= 6666){
                    drawXAxisHelp(g2,times,i);
                }
            }
        }

        //Draw the graphs themselves
        if(app.getMenuPanel().getSelectedOption().equals("Show All")){
            paintAggregateData(g2,times);
        } else if(app.getMenuPanel().getSelectedOption().equals("Show Moving Average")){
            paintMovingAverage(g2,times);
        }else {
            String id = app.getMenuPanel().getSelectedOption().split(" - ")[0];
            paintIndividualData(g2,id,times);
        }

        //paints the mouse overlay
        if(!times.isEmpty()){
            paintMouseOverlay(g2,times.get(0),xStep);
        }
    }

    //paints the mouse overlay based on the position of the mouse
    private void paintMouseOverlay(Graphics2D g2, Long firstTime, Integer xStep){
        try {
            AffineTransform oldTx = g2.getTransform();
            AffineTransform newTx = oldTx.createInverse();

            Point localMousePos = new Point();
            newTx.transform(mouseLocation,localMousePos);

            if(localMousePos.x >0){
                g2.setColor(new Color(140, 229, 162));
                g2.drawLine(localMousePos.x,0,localMousePos.x,-10000);
                //g2.drawLine(0,localMousePos.y,100000,localMousePos.y);

                FontMetrics fontMetrics = g2.getFontMetrics();
                DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Long mousePosTime = firstTime + (int)(((double)localMousePos.x/(double)xStep)*3600000.0);
                String mousePosLabel = formatter.format(mousePosTime);
                g2.drawString(mousePosLabel,localMousePos.x-fontMetrics.stringWidth(mousePosLabel)/2,45);
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }

    }

    //a commonly used operation in paintChargerGraph used to paint the x axis marks`
    private void drawXAxisHelp(Graphics2D g2, List<Long> times, Integer i){
        DateFormat simple = new SimpleDateFormat("dd/MM/yy HH:mm");
        FontMetrics fontMetrics = g2.getFontMetrics();

        Integer xStep = app.getMenuPanel().getHorizontalScale();
        Integer yStep = app.getMenuPanel().getVerticalScale();
        Integer yIncrement = (getHeight()-130)/yStep;

        String label = simple.format(times.get(0) + i * 60000);
        Integer stringLength = fontMetrics.stringWidth(label);
        Integer stringHeight = fontMetrics.getHeight();

        Integer textXpos = (int)(i * xStep/60.0 - (stringLength / 2.0));
        Integer tickXpos = (int)(i * xStep/60.0);

        if(app.getMenuPanel().isShowGrid()){
            g2.setColor(Settings.GRID_COLOUR);
            g2.drawLine(tickXpos,0, tickXpos,-yStep*yIncrement);
        }
        g2.setColor(Color.WHITE);
        g2.drawString(label, textXpos, stringHeight + 10);
        g2.drawLine(tickXpos,0, tickXpos,-10);
    }

    //paints the graph summarizing all data
    private void paintAggregateData(Graphics2D g2, List<Long> times){
        Integer xStep = app.getMenuPanel().getHorizontalScale();
        Integer yStep = app.getMenuPanel().getVerticalScale();
        Integer yIncrement = (getHeight()-130)/yStep;

        if(!times.isEmpty()) {
            long startTime = times.get(0);
            Integer lossyCompressionFactor = (int)Math.ceil(1.0/(xStep/25.0));
            for (int i = 0; i < times.size() - lossyCompressionFactor; i++) {
                if(i%lossyCompressionFactor == 0){
                    int x1 = (int) (long) (times.get(i) - startTime) / (3600000 / xStep);
                    int y1 = (int)-app.getDataModel().getGeneralLogEntry(times.get(i)) * yIncrement;
                    int x2 = (int) (long) (times.get(i + lossyCompressionFactor) - startTime) / (3600000 / xStep);
                    int y2 = (int)-app.getDataModel().getGeneralLogEntry(times.get(i + lossyCompressionFactor)) * yIncrement;

                    //change colour based on if the data was generated or not.
                    if(app.getDataModel().isGenerated(times.get(i))){
                        g2.setColor(Settings.GENERATED_HIGHLIGHT_COLOUR);
                        g2.fillRect(x1,-yIncrement*yStep,x2-x1,yIncrement*yStep);
                        g2.setColor(Settings.GENERATED_LINE_COLOUR);
                    } else {
                        g2.setColor(Settings.ALL_CHARGERS_COLOUR);
                    }

                    if (xStep < 400) {
                        g2.setStroke(new BasicStroke((int) ((xStep / 400.0) * 4) + 1));
                        g2.drawLine(x1, y1, x2, y2);
                        if(app.getMenuPanel().isShowLogMarkers()) g2.fillRect((x2 - xStep / 40), (y2 - xStep / 40), xStep / 20, xStep / 20);
                    } else {
                        g2.setStroke(new BasicStroke(5));
                        g2.drawLine(x1, y1, x2, y2);
                        if(app.getMenuPanel().isShowLogMarkers()) g2.fillRect((x2 - 10), (y2 - 10), 20, 20);
                    }

                    if(!app.getMenuPanel().isShowLineOnly()){
                        if(app.getDataModel().isGenerated(times.get(i))){
                            g2.setColor(Settings.GENERATED_FILL);
                        } else {
                            g2.setColor(Settings.ALL_CHARGERS_FILL);
                        }
                        g2.fillPolygon(new int[] {x1,x1,x2,x2}, new int[] {0,y1,y2,0}, 4);
                    }
                }
            }

            //paint total chargers line
            Integer totalValid = 0;
            for (String chargerID : app.getDataModel().getIds()) {
                ChargerObject charger = app.getDataModel().getCharger(chargerID);
                if (app.getDataModel().isValidCharger(charger)) {
                    totalValid++;
                }
            }
            g2.setColor(Settings.TOTAL_CHARGERS_COLOUR);
            g2.drawLine(0, -yIncrement * totalValid, 1000000, -yIncrement * totalValid);
        }
    }

    //paints the graph for the moving average
    private void paintMovingAverage(Graphics2D g2, List<Long> times){
        Integer xStep = app.getMenuPanel().getHorizontalScale();
        Integer yStep = app.getMenuPanel().getVerticalScale();
        Integer yIncrement = (getHeight()-130)/yStep;


        if(!times.isEmpty()) {
            long startTime = times.get(0);
            Integer lossyCompressionFactor = (int)Math.ceil(1.0/(xStep/25.0));
            for (int i = 0; i < times.size() - lossyCompressionFactor; i++) {
                if(i%lossyCompressionFactor == 0){
                    int x1 = (int) (long) (times.get(i) - startTime) / (3600000 / xStep);
                    int y1 = (int)(-app.getDataModel().getGeneralLogEntry(times.get(i)) * yIncrement);
                    int x2 = (int) (long) (times.get(i + lossyCompressionFactor) - startTime) / (3600000 / xStep);
                    int y2 = (int)(-app.getDataModel().getGeneralLogEntry(times.get(i + lossyCompressionFactor)) * yIncrement);

                    if(app.getDataModel().isGenerated(times.get(i))){
                        g2.setColor(Settings.GENERATED_HIGHLIGHT_COLOUR);
                        g2.fillRect(x1,-yIncrement*yStep,x2-x1,yIncrement*yStep);
                        g2.setColor(Settings.GENERATED_LINE_COLOUR);
                    } else {
                        g2.setColor(Settings.MOVING_AVERAGE_COLOUR);
                    }

                    if (xStep < 400) {
                        g2.setStroke(new BasicStroke((int) ((xStep / 400.0) * 4) + 1));
                        g2.drawLine(x1, y1, x2, y2);
                        if(app.getMenuPanel().isShowLogMarkers()) g2.fillRect((x2 - xStep / 40), (y2 - xStep / 40), xStep / 20, xStep / 20);
                    } else {
                        g2.setStroke(new BasicStroke(5));
                        g2.drawLine(x1, y1, x2, y2);
                        if(app.getMenuPanel().isShowLogMarkers()) g2.fillRect((x2 - 10), (y2 - 10), 20, 20);
                    }

                    if(!app.getMenuPanel().isShowLineOnly()){
                        if(app.getDataModel().isGenerated(times.get(i))){
                            g2.setColor(Settings.GENERATED_FILL);
                        } else {
                            g2.setColor(Settings.MOVING_AVERAGE_FILL);
                        }
                        g2.fillPolygon(new int[] {x1,x1,x2,x2}, new int[] {0,y1,y2,0}, 4);
                    }
                }
            }


            //paint total chargers line
            Integer totalValid = 0;
            for (String chargerID : app.getDataModel().getIds()) {
                ChargerObject charger = app.getDataModel().getCharger(chargerID);
                if (app.getDataModel().isValidCharger(charger)) {
                    totalValid++;
                }
            }
            g2.setColor(Settings.TOTAL_CHARGERS_COLOUR);
            g2.drawLine(0, -yIncrement * totalValid, 1000000, -yIncrement * totalValid);
        }
    }

    //paints the graph for a single charger
    private void paintIndividualData(Graphics2D g2, String id, List<Long> times){
        Integer xStep = app.getMenuPanel().getHorizontalScale();
        Integer yStep = app.getMenuPanel().getVerticalScale();
        Integer yIncrement = (getHeight()-130)/yStep;
        ChargerObject charger = app.getDataModel().getCharger(id);


        if(!times.isEmpty()){
            long startTime  = times.get(0);
            for(int i = 0; i < times.size()-1 ; i++){
                int x1 = (int)(long)(times.get(i) - startTime)/(3600000/xStep);
                int x2 = (int)(long)(times.get(i+1) - startTime)/(3600000/xStep);
                int y1 = (-yIncrement * (charger.getEntryInLog(times.get(i)) ? 1 : 0)-5);
                int y2 = (-yIncrement * (charger.getEntryInLog(times.get(i+1)) ? 1 : 0)-5);

                //Set the colour depending on if the data was generated
                if(charger.isGenerated(times.get(i))){
                    g2.setColor(Settings.GENERATED_HIGHLIGHT_COLOUR);
                    g2.fillRect(x1,-yIncrement*yStep,x2-x1,yIncrement*yStep);
                    g2.setColor(Settings.GENERATED_LINE_COLOUR);
                } else{
                    g2.setColor(Settings.SINGLE_CHARGER_COLOUR);
                }

                if(xStep < 400){
                    g2.setStroke(new BasicStroke((int)((xStep/400.0) * 4)+1));
                    g2.drawLine(x1,y1,x2,y2);
                    if(app.getMenuPanel().isShowLogMarkers()) g2.fillRect((x2-xStep/40),(y2-xStep/40),xStep/20,xStep/20);
                } else {
                    g2.setStroke(new BasicStroke(5));
                    g2.drawLine(x1,y1,x2,y2);
                    if(app.getMenuPanel().isShowLogMarkers()) g2.fillRect((x2-10),(y2-10),20,20);
                }

                if(!app.getMenuPanel().isShowLineOnly()){
                    if(app.getDataModel().isGenerated(times.get(i))){
                        g2.setColor(Settings.GENERATED_FILL);
                    } else {
                        g2.setColor(Settings.SINGLE_CHARGER_FILL);
                    }
                    g2.fillPolygon(new int[] {x1,x1,x2,x2}, new int[] {0,y1,y2,0}, 4);
                }
            }
        }
    }

    //paints the background.
    private void paintBackground(Graphics2D g2){
        g2.setColor(Settings.BACKGROUND_COLOUR);
        g2.fillRect(-100000,-100000,200000,200000);
    }

    private class MousePositionListener extends MouseAdapter{
        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
            mouseLocation = new Point(e.getX(),e.getY());
            GraphPanel.this.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            mouseLocation = new Point(e.getX(),e.getY());
            GraphPanel.this.repaint();
        }
    }
}
