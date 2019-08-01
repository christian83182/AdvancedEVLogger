import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class InteractivePanel extends JPanel {

    //Variable used to keep track of the current pan location.
    private Point globalPan;
    //Variable used to keep track of the current level of zoom.
    private Double globalZoom;

    private Application app;

    InteractivePanel(Point globalPan, Double globalZoom, Application app){
        this.globalPan = globalPan;
        this.globalZoom = globalZoom;
        this.app = app;

        PanAndZoomListener panListener = new PanAndZoomListener();
        this.addMouseWheelListener(panListener);
        this.addMouseListener(panListener);
        this.addMouseMotionListener(panListener);
    }

    public abstract void paintView(Graphics2D g2);

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        BufferedImage img = generateSnapshot();

        g2d.drawImage(img,0,0,getWidth(),getHeight(),null);
    }

    private void configureGlobalTransform(Graphics2D g2){
        //Create a global affine transformation which pans and zooms the view accordingly.
        AffineTransform globalTransform = new AffineTransform();

        //Translate the view to account for the user's pan.
        globalTransform.translate(getPan().x*getZoom(), getPan().y*getZoom());

        //Scale the view to account for the user's zoom level. Translate such that it zoom to the center of the screen.
        globalTransform.translate(getWidth()/2, getHeight()/2);
        globalTransform.scale(getZoom(), getZoom());
        globalTransform.translate(-getWidth()/2, -getHeight()/2);

        //Set the transform to the one used by the graphics object.
        g2.setTransform(globalTransform);

    }

    public Point getPan(){
        return globalPan;
    }

    public Double getZoom(){
        return globalZoom;
    }

    public void setPan(Point newPan){
        this.globalPan = newPan;
    }

    public void setZoom(Double newZoom){
        this.globalZoom = newZoom;
    }

    public BufferedImage generateSnapshot(){
        //Generate a Buffered Image of the size of the window to draw on.
        BufferedImage img = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        //Configure the global transform to match the pan and zoom settings.
        configureGlobalTransform(g2);
        paintView(g2);

        return img;
    }

    public void paintScale(Graphics2D g2){
        String label = "1 Hour";
        Double scaleLength = app.getMenuPanel().getScale() * getZoom();

        //Different size options should be added from largest to smallest.
        if(scaleLength < 4) {
            scaleLength = app.getMenuPanel().getScale() * getZoom() * 24;
            label = "1 Day";
        } else if(scaleLength < 20){
            scaleLength = app.getMenuPanel().getScale() * getZoom()*5;
            label = "5 Hours";
        } else if (scaleLength <50){
            scaleLength = app.getMenuPanel().getScale() * getZoom()*2;
            label= "2 Hours";
        }

        //Different size options should be added from smallest to largest.
        if (scaleLength > 400) {
            scaleLength = app.getMenuPanel().getScale() * getZoom() / 4;
            label = "15 Minutes";
        }else if (scaleLength > 200){
            scaleLength = app.getMenuPanel().getScale() * getZoom()/2;
            label = "30 Minutes";
        }

        Point scaleStart = new Point(20,getHeight()-10);
        Point scaleEnd = new Point(scaleStart.x + scaleLength.intValue(), scaleStart.y);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(scaleStart.x, scaleStart.y, scaleEnd.x, scaleEnd.y);
        g2.drawLine(scaleStart.x, scaleStart.y, scaleStart.x, scaleStart.y-7);
        g2.drawLine(scaleEnd.x, scaleEnd.y, scaleEnd.x, scaleEnd.y-7);
        g2.drawString(label, scaleEnd.x+20, scaleEnd.y);
    }

    //Inner class devoted to giving the view zoom and pan functionality.
    private class PanAndZoomListener extends MouseAdapter {

        Point startPoint;
        Point originalGlobalPan;

        PanAndZoomListener(){
            startPoint = new Point(0,0);
            originalGlobalPan = new Point(0,0);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            originalGlobalPan = (Point)getPan().clone();
            startPoint = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int newPanX = (int)(originalGlobalPan.x + (e.getX() - startPoint.x)/getZoom());
            if(newPanX < 0){
                getPan().x = (int)(originalGlobalPan.x + (e.getX() - startPoint.x)/getZoom());
            } else {
                getPan().x = 0;
            }
            //getPan().y = (int)(originalGlobalPan.y + (e.getY() - startPoint.y)/getZoom());
            InteractivePanel.this.repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int scaleFactor = e.getWheelRotation();
            Double maxZoom = Settings.MAX_ZOOM;
            Double minZoom = Settings.MIN_ZOOM;
            if(scaleFactor < 0 & getZoom() * 1/0.95 < maxZoom ){
                //setZoom(getZoom() * 1/0.95);
            } else  if (scaleFactor > 0 & getZoom() * 0.95 > minZoom){
                //setZoom(getZoom()*0.95);
            } else {
                return;
            }
            InteractivePanel.this.repaint();
        }
    }

}
