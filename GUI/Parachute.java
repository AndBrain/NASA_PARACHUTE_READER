package GUI;
import Decoder.Decode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;

import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Parachute extends JPanel implements MouseListener{
    
    private ArrayList<Integer> radii;
    private double[] middleSectionAngles;
    private double[] rightMostAngles;
    private double panelAngle;
    private double startingAngle;
    private String inBetween;

    private Color backgroudColor;

    private final double FULL_ANGLE = 360.0;
    private final int PANELS = 80;
    private final int CIRCLE_DIAMETER = 800;
    private final int DIAMETER_DIFFERENCE = 100;
    private final int RADIUS_DIFFERENCE = DIAMETER_DIFFERENCE / 2;
    private final int BUFFER_WIDTH = DIAMETER_DIFFERENCE/8;
    private final int IMAGE_SIDE_LENGTH = CIRCLE_DIAMETER + BUFFER_WIDTH*2;
    private final int CENTER = IMAGE_SIDE_LENGTH / 2;
    private final int PARACHUTE_LAYERS = 7;
    private final int RINGS = 4;
    private final double TWELVEOCLOCK = 90.0;

    private final int INNERMOST_RING = 6;
    private final int SECOND_INNERMOST_RING = 4;
    private final int IGNORED_RING = 1;
    
    public Parachute(Color backgroundColor){
        this.backgroudColor = backgroundColor;
        this.addMouseListener(this);
        this.setPreferredSize(new Dimension(IMAGE_SIDE_LENGTH, IMAGE_SIDE_LENGTH));
        
        panelAngle = FULL_ANGLE / PANELS;
        startingAngle = TWELVEOCLOCK - panelAngle*2;
        
        radii = new ArrayList<Integer>();
        setRadii();
        middleSectionAngles = new double[PANELS];
        rightMostAngles = new double[PANELS];
        setAngles();

        inBetween = "";
        setUpInBetween();
    }

    public void setUpInBetween(){
        for(int ring = 0; ring < RINGS; ring++){
            for(int panel = 0; panel < PANELS; panel++){
                inBetween += "0";
            }
        }
    }

    public void setRadii(){
        for (int i = 0; i < PARACHUTE_LAYERS; i++){
            if(i != INNERMOST_RING && i != SECOND_INNERMOST_RING){
                radii.add((CIRCLE_DIAMETER - i * DIAMETER_DIFFERENCE)/2);
            }
        }
    }

    public void setAngles(){
        for(int i = 0; i < middleSectionAngles.length; i++){
            rightMostAngles[i] = startingAngle - i*panelAngle;
            middleSectionAngles[i] = (rightMostAngles[i]) + panelAngle/2; 
            rightMostAngles[i] = (rightMostAngles[i] + 360) % 360;
            middleSectionAngles[i] = (middleSectionAngles[i] + 360) % 360; 
        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        drawDisk((Graphics2D) g);
    }

    public void drawDisk(Graphics2D g2){  
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);      
        
        indicateBuffers(g2);

        for(int index = 0; index < radii.size(); index++){
            int radius = radii.get(index);

            int ring = formatToRing(index);

            if(index!=IGNORED_RING){

                String majority = findMajorityColor(inBetween.substring(ring*PANELS, (ring+1)*PANELS));
                Color ringColor = majority.equals("1") ? Color.RED.darker() : Color.GRAY;

                drawFilledRing(g2, radius, ringColor);

                for (int i = 0; i < PANELS; i++) {
                    int mult = 0;
                    String colorValue = "";
                    while(i+mult<PANELS-1){
                        colorValue = inBetween.substring(((ring*PANELS)+i+mult),(ring*PANELS)+i+mult+1);
                        if(colorValue.equals(inBetween.substring(((ring*PANELS)+i+mult+1),((ring*PANELS)+i+mult+2)))){
                            mult+=1;
                        }else{
                            break;
                        }
                    }
                    i+=mult;
                    if(!colorValue.equals(majority)){
                        drawPanel(ring, i, radius, mult+1, g2);
                    }
                }
            }else{
                drawEmptyRing(g2, radius);
            }
            drawRingSep(g2, radius);
        }
        drawLines(g2);
        createHole(g2);
        
    }

    public void drawSlice(Graphics2D g2, Color color, int coord, int diameter, double start, double angle){
        g2.setColor(color);
        Arc2D.Double panel = new Arc2D.Double(coord, coord, diameter, diameter, start, angle, Arc2D.PIE);
        g2.fill(panel);
    }

    public void drawPanel(int ring, int index, int radius, int mult, Graphics2D g2){
        double startAngle = rightMostAngles[index];
        int diameter = radius*2;
        Color color = null;

        if(inBetween.substring((ring*PANELS)+index, (ring*PANELS)+index+1).equals("1")){
            color = Color.RED.darker();
        }else{
            color = Color.GRAY;
        }
        drawSlice(g2, color, CENTER-radius, diameter, startAngle, panelAngle*mult);
    }

    public void drawLines(Graphics2D g2){
        int startRadius = RADIUS_DIFFERENCE;
        int endRadius = radii.get(2);
        int innerRadius = radii.get(1);
        int outerRadius = radii.get(0);
        g2.setColor(Color.BLACK);

        for(double angle : rightMostAngles){
            double xMultiple = (Math.cos(Math.toRadians(angle)));
            double yMultiple = (Math.sin(Math.toRadians(angle)));

            int xStart = (int)(startRadius*xMultiple), xEnd = (int)(endRadius*xMultiple);
            int yStart = (int)(startRadius*yMultiple), yEnd = (int)(endRadius*yMultiple);

            xStart = (IMAGE_SIDE_LENGTH/2) + xStart;
            xEnd = (IMAGE_SIDE_LENGTH/2) + xEnd;

            yStart = (IMAGE_SIDE_LENGTH/2) - yStart;
            yEnd = (IMAGE_SIDE_LENGTH/2) - yEnd; 
            
            g2.drawLine(xStart, yStart, xEnd, yEnd);

            xStart = (int)(innerRadius*xMultiple);
            xEnd = (int)(outerRadius*xMultiple);
            
            yStart = (int)(innerRadius*yMultiple);
            yEnd = (int)(outerRadius*yMultiple);

            xStart = (IMAGE_SIDE_LENGTH/2) + xStart;
            xEnd = (IMAGE_SIDE_LENGTH/2) + xEnd;

            yStart = (IMAGE_SIDE_LENGTH/2) - yStart;
            yEnd = (IMAGE_SIDE_LENGTH/2) - yEnd; 
            
            g2.drawLine(xStart, yStart, xEnd, yEnd);
        }
    }

    public void indicateBuffers(Graphics2D g2){
        int radius = radii.get(0) + BUFFER_WIDTH;
        int bufferLength = 3;
        int diameter = 2*radius;
        Color color = (Color.YELLOW);
        for(int bufferStart = bufferLength-1; bufferStart < rightMostAngles.length; bufferStart+=10){
            double startAngle = rightMostAngles[bufferStart];
            drawSlice(g2, color, CENTER-radius, diameter, startAngle, panelAngle*bufferLength);
        }
    }

    public void drawEmptyRing(Graphics2D g2, int radius){
        g2.setColor(backgroudColor);
        g2.fillArc(CENTER - radius, CENTER - radius, 2 * radius, 2 * radius, 0, 360);
    }

    public void drawFilledRing(Graphics2D g2, int radius, Color color){
        g2.setColor(color);
        g2.fillArc(CENTER - radius, CENTER - radius, 2 * radius, 2 * radius, 0, 360);
    }

    public String findMajorityColor(String ring){
        int majority = 0;
        for(int i = 0; i < ring.length(); i++){
            majority += inBetween.substring(i, i+1).equals("1") ? 1:-1;
        }
        return majority >= 0 ? "1" : "0";
    }

    public void drawRingSep(Graphics2D g2, int radius){
        g2.setColor(Color.BLACK);
        g2.drawArc(CENTER - radius, CENTER - radius, 2 * radius, 2 * radius, 0, 360);
    }

    public void createHole(Graphics2D g2){
        g2.setColor(backgroudColor);
        g2.fillArc(CENTER - RADIUS_DIFFERENCE, CENTER - RADIUS_DIFFERENCE, DIAMETER_DIFFERENCE, DIAMETER_DIFFERENCE, 0, 360);
        g2.setColor(Color.BLACK);
        g2.drawArc(CENTER - RADIUS_DIFFERENCE, CENTER - RADIUS_DIFFERENCE, DIAMETER_DIFFERENCE, DIAMETER_DIFFERENCE, 0,360);
    }

    public void shiftCounterClockwise(){
        String binary = "";
        for(int ring = 0; ring < RINGS; ring++){
            String line = this.inBetween.substring((ring*PANELS), (ring+1)*PANELS);
            line = line.substring(1) + line.substring(0, 1);
            binary += line;
        }
        inBetween = binary;
        GUI.setInput(Decode.decodeBinaryMessage(inBetween));
        repaint();
    }

    public void shiftClockwise(){
        String binary = "";
        for(int ring = 0; ring < RINGS; ring++){
            String line = inBetween.substring((ring*PANELS), (ring+1)*PANELS);
            int end = line.length()-1;
            line = line.substring(end) + line.substring(0, end);
            binary += line;
        }
        inBetween = binary;
        GUI.setInput(Decode.decodeBinaryMessage(inBetween));
        repaint();
    }

    public int findRing(double radius){
        int index = 4;
        for(int i = radii.size()-1; i >= 0; i--){
            if(radius < radii.get(i) && radius > RADIUS_DIFFERENCE){
                return index;
            }
            index-=1;
        }
        return -1;
    }

    public int findIndex(double angle){
        int bestMatch = 0;
        double difference = Integer.MAX_VALUE;
        for (int index = 0; index < middleSectionAngles.length; index++){
            if(Math.abs(angle-middleSectionAngles[index]) <= difference){
                bestMatch = index;
                difference = Math.abs(angle-middleSectionAngles[index]);
            }
        } 
        return bestMatch;
    }

    public void setPanelOpp(int ring, int index){
        ring = formatToRing(ring);
        int i = (ring*PANELS)+index;
        String bit = inBetween.substring(i, i+1);
        if(bit.equals("1")){
            inBetween = inBetween.substring(0, i) + "0" + inBetween.substring(i+1);
        }else{
            inBetween = inBetween.substring(0, i) + "1" + inBetween.substring(i+1);
        }
    }

    public void setInBetween(String inBetween){
        this.inBetween = inBetween;
        repaint();
    }

    public void mousePressed(MouseEvent e) {       
        int x = e.getX() - CENTER;
        int y = CENTER - e.getY();

        double radius = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double angle = ((Math.toDegrees(Math.atan2(y, x)))+360)%360;

        int ring = findRing(radius);
        int index = findIndex(angle);

        if(ring!=-1 && ring!=IGNORED_RING){
            setPanelOpp(ring, index);
        }

        GUI.setInput(Decode.decodeBinaryMessage(inBetween));

        repaint();
    }
    
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void downloadImage(){
        BufferedImage image = new BufferedImage(IMAGE_SIDE_LENGTH, IMAGE_SIDE_LENGTH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        paintComponent(g2);
        g2.dispose();
        try {
            File outputfile = new File("parachute_image.png");
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int formatToRing(int index){
        return Math.abs(3-((5*index)/6));
    }
}
