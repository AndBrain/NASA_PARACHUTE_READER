package GUI;
import Decoder.Decode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;


//Rewrite this to make it better
class Parachute extends JPanel implements MouseListener{
    
    private int[] radii;
    private double[] middleSectionAngles;
    private double[] rightMostAngles;
    private double panelAngle;
    private double startingAngle;
    private boolean[][] redSectors;
    private String inBetween;

    private Color backgroudColor;

    private final double FULL_ANGLE = 360.0;
    private final int SECTORS = 80;
    private final int CIRCLE_DIAMETER = 800;
    private final int DIAMETER_DIFFERENCE = 100;
    private final int RADIUS_DIFFERENCE = DIAMETER_DIFFERENCE / 2;
    private final int BUFFER_WIDTH = DIAMETER_DIFFERENCE/8;
    private final int IMAGE_SIDE_LENGTH = CIRCLE_DIAMETER + BUFFER_WIDTH*2;
    private final int CENTER = IMAGE_SIDE_LENGTH / 2;
    private final int PARACHUTE_LAYERS = 7;
    private final double TWELVEOCLOCK = 90.0;
    
    public Parachute(Color backgroundColor){
        this.backgroudColor = backgroundColor;
        this.addMouseListener(this);
        this.setPreferredSize(new Dimension(IMAGE_SIDE_LENGTH, IMAGE_SIDE_LENGTH));
        
        panelAngle = FULL_ANGLE / SECTORS;
        startingAngle = TWELVEOCLOCK - panelAngle*2;
        
        radii = new int[PARACHUTE_LAYERS];
        setRadii();
        middleSectionAngles = new double[SECTORS];
        rightMostAngles = new double[SECTORS];
        setAngles();

        redSectors = new boolean[PARACHUTE_LAYERS][SECTORS];
        inBetween = "";
        updateInBetween();
    }

    

    public void setRadii(){
        for (int i = 0; i < radii.length; i++){
            radii[i] = (CIRCLE_DIAMETER - i * DIAMETER_DIFFERENCE)/2;
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
        for(int index = 0; index < radii.length; index++){
            int radius = radii[index];
            if(index!=1){
                for (int i = 0; i < SECTORS; i++) {
                    drawPanel(index, i, radius, g2);
                }
            }else{
                drawEmptyRing(g2, radius);
            }
            drawRingSep(g2, index, radius);
        }
        drawLines(g2);
        createHole(g2);
        
    }

    public void drawSlice(Graphics2D g2, Color color, int coord, int diameter, double start, double angle){
        g2.setColor(color);
        Arc2D.Double panel = new Arc2D.Double(coord, coord, diameter, diameter, start, angle, Arc2D.PIE);
        g2.fill(panel);
    }

    public void drawPanel(int ring, int index, int radius, Graphics2D g2){
        double startAngle = rightMostAngles[index];
        int diameter = radius*2;
        Color color = (redSectors[ring][index] ? Color.RED.darker() : Color.GRAY);

        drawSlice(g2, color, CENTER-radius, diameter, startAngle, panelAngle);
    }

    public void drawLines(Graphics2D g2){
        int startRadius = RADIUS_DIFFERENCE;
        int endRadius = radii[2];
        int innerRadius = radii[1];
        int outerRadius = radii[0];
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
        int radius = radii[0] + BUFFER_WIDTH;
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

    public void drawRingSep(Graphics2D g2, int ring, int radius){
        if(ring!=4 && ring!=6){
            g2.setColor(Color.BLACK);
            g2.drawArc(CENTER - radius, CENTER - radius, 2 * radius, 2 * radius, 0, 360);
        }
    }

    public void createHole(Graphics2D g2){
        g2.setColor(backgroudColor);
        g2.fillArc(CENTER - RADIUS_DIFFERENCE, CENTER - RADIUS_DIFFERENCE, DIAMETER_DIFFERENCE, DIAMETER_DIFFERENCE, 0, 360);
        g2.setColor(Color.BLACK);
        g2.drawArc(CENTER - RADIUS_DIFFERENCE, CENTER - RADIUS_DIFFERENCE, DIAMETER_DIFFERENCE, DIAMETER_DIFFERENCE, 0,360);
    }

    public void shiftClockwise(){
        for(int ring = 0; ring < redSectors.length; ring++){
            boolean temp = redSectors[ring][redSectors[ring].length-1];
            for(int i = redSectors[ring].length-1; i > 0 ; i--){
                redSectors[ring][i] = redSectors[ring][i-1];
            }
            redSectors[ring][0] = temp;
        }
        updateInBetween();
        GUI.setInput(Decode.decodeBinaryMessage(inBetween));
        repaint();
    }

    public void shiftCounterClockwise(){
        for(int ring = 0; ring < redSectors.length; ring++){
            boolean temp = redSectors[ring][0];
            for(int i = 0; i < redSectors[ring].length-1; i++){
                redSectors[ring][i] = redSectors[ring][i+1];
            }
            redSectors[ring][redSectors[ring].length-1] = temp;
        }
        updateInBetween();
        GUI.setInput(Decode.decodeBinaryMessage(inBetween));
        repaint();
    }

    public int findRing(double radius){
        int index = 6;
        for(int i = radii.length-1; i >= 0; i--){
            if(radius < radii[i] && radius > RADIUS_DIFFERENCE){
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

    public void setPanelsOpp(int ring, int index){
        if(ring == 3 || ring == 4){
            setPanelOpp(3, index);
            setPanelOpp(4, index);
        }else if(ring == 6 || ring == 5){
            setPanelOpp(5, index);
            setPanelOpp(6, index);
        }else{
            setPanelOpp(ring, index);
        }
    }

    public void setPanel(int ring, int index, boolean set){
        redSectors[ring][index] = set;
    }

    public void setPanels(int ring, int index, boolean set){
        if(ring == 3 || ring == 4){
            setPanel(3, index, set);
            setPanel(4, index, set);
        }else if(ring == 6 || ring == 5){
            setPanel(5, index, set);
            setPanel(6, index, set);
        }else{
            setPanel(ring, index, set);
        }
    }

    public void setPanelOpp(int ring, int index){
        redSectors[ring][index] = !redSectors[ring][index];
    }

    public void inBetweenToSector(String inBetween){
        this.inBetween = inBetween;
        updateSectors();
    }

    public void updateSectors(){
        for(int i = 0; i < inBetween.length(); i++){
            setPanels((PARACHUTE_LAYERS-1)-(i/SECTORS)*2, i%SECTORS, inBetween.substring(i, i+1).equals("1"));
        }
        repaint();
    }

    public void updateInBetween(){
        inBetween = "";
        for(int i = radii.length-1; i >= 0; i-=2){
            for(int index = 0; index < redSectors[i].length; index++){
                inBetween += (redSectors[i][index] ? "1":"0");
            }
        }
    }

    public void mousePressed(MouseEvent e) {       
        int dx = e.getX() - CENTER;
        int dy = CENTER - e.getY();

        double radius = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        double angle = ((Math.toDegrees(Math.atan2(dy, dx)))+360)%360;

        int ring = findRing(radius);
        int index = findIndex(angle);

        if(ring!=-1 && ring!=1){
            setPanelsOpp(ring, index);
        }

        updateInBetween();

        GUI.setInput(Decode.decodeBinaryMessage(inBetween));

        repaint();
    }
    
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
