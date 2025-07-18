package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import Decoder.Decode;

class Parachute extends JPanel implements MouseListener{
    
    private int[] radii;
    private double[] startingAngles;
    private double panelAngle;
    private double startingAngle;
    private boolean[][] redSectors;
    private String inBetween;

    private int centerX;
    private int centerY;

    private Color backgroudColor;

    private final double FULL_ANGLE = 360.0;
    private final int SECTORS = 80;
    private final int CIRCLE_WIDTH = 800;
    private final int LAYER_WIDTH = 100;
    private final int BUFFER_WIDTH = LAYER_WIDTH/8;
    private final int IMAGE_WIDTH = CIRCLE_WIDTH + BUFFER_WIDTH*2;
    private final int PARACHUTE_LAYERS = 7;
    private final double TWELVEOCLOCK = 90.0;
    
    public Parachute(Color backgroundColor){
        this.backgroudColor = backgroundColor;
        this.addMouseListener(this);
        this.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_WIDTH));
        
        centerX = IMAGE_WIDTH / 2;
        centerY = IMAGE_WIDTH / 2;
        panelAngle = FULL_ANGLE / SECTORS;
        startingAngle = TWELVEOCLOCK - panelAngle*2;
        
        radii = new int[PARACHUTE_LAYERS];
        setRadii();
        startingAngles = new double[SECTORS];
        setAngles();

        redSectors = new boolean[PARACHUTE_LAYERS][SECTORS];
        inBetween = "";
        updateInBetween();
    }

    public void setRadii(){
        for (int i = 0; i < radii.length; i++){
            radii[i] = (CIRCLE_WIDTH - i * LAYER_WIDTH)/2;
        }
    }

    public void setAngles(){
        for(int i = 0; i < startingAngles.length; i++){
            startingAngles[i] = (startingAngle - i*panelAngle) + panelAngle/2; 
            startingAngles[i] = (startingAngles[i] + 360) % 360; 
        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        drawDisk((Graphics2D) g);
    }

    public void drawDisk(Graphics2D g2){        
        indicateBuffers(g2);
        for(int index = 0; index < radii.length; index++){
            int radius = radii[index];
            for (int i = 0; i < SECTORS; i++) {
                drawPanel(index, i, radius, g2);
            }
        }
        createHole(g2);
    }

    public void indicateBuffers(Graphics2D g2){
        int radius = radii[0] + BUFFER_WIDTH;
        g2.setColor(Color.YELLOW);
        for(int bufferStart = 2; bufferStart < startingAngles.length; bufferStart+=10){
            for(int index = bufferStart; index >= bufferStart-2; index--){
                double startAngle = (startingAngles[index]) - panelAngle/2;
                g2.fillArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, (int) (startAngle), (int) Math.ceil(panelAngle));
            }
        }
    }

    public void drawPanel(int ring, int index, int radius, Graphics2D g2){
        double startAngle = (startingAngles[index]) - panelAngle/2;
        if (ring == 1){
            g2.setColor(backgroudColor);
        }else{
            g2.setColor(redSectors[ring][index] ? Color.RED.darker() : Color.GRAY);
        }
        g2.fillArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, (int) (startAngle), (int) Math.ceil(panelAngle));
        if(ring!=4 && ring!=6){
            g2.setColor(Color.BLACK);
            g2.drawArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, (int) (startAngle), (int) Math.ceil(panelAngle));
        }
    }

    public void createHole(Graphics2D g2){
        g2.setColor(backgroudColor);
        g2.fillArc(centerX - LAYER_WIDTH/2, centerY - LAYER_WIDTH/2, LAYER_WIDTH, LAYER_WIDTH, 0, (int) Math.ceil(360.0));
        g2.setColor(Color.BLACK);
        g2.drawArc(centerX - LAYER_WIDTH/2, centerY - LAYER_WIDTH/2, LAYER_WIDTH, LAYER_WIDTH, 0, (int) Math.ceil(360.0));
    }

    public int findRing(double radius){
        int index = 6;
        for(int i = radii.length-1; i >= 0; i--){
            if(radius < radii[i] && radius > LAYER_WIDTH/2){
                return index;
            }
            index-=1;
        }
        return -1;
    }

    public int findIndex(double angle){
        int bestMatch = 0;
        double difference = Integer.MAX_VALUE;
        for (int index = 0; index < startingAngles.length; index++){
            if(Math.abs(angle-startingAngles[index]) <= difference){
                bestMatch = index;
                difference = Math.abs(angle-startingAngles[index]);
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
        int dx = e.getX() - centerX;
        int dy = centerY - e.getY();

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

//D.A.R.E  M.I.G.H.T.Y  T.H.I.N.G.S  34.K.58.N.118.J.31.W  
