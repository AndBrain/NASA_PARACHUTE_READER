package GUI;
import javax.swing.*;
import javax.swing.event.*;

import Encoder.Encode;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


//Opptomize how arcs are drawn on the page
public class GUI {
    private static JFrame frame;
    private static JPanel page;
    private static Parachute pImage;
    private static JPanel parachuteIO;
    private static JPanel textOutput;
    private static JButton shiftc;
    private static JButton shiftcc;
    private static JLabel message;
    private static JTextField inputField;

    private static String pastInBetween = "";

    private static Dimension PAGE_SIZE = new Dimension(900, 950);
    private static Color backColor = new Color(238, 238, 238);
    private static Dimension TEXT_SIZE = new Dimension(500, 50);
    private static boolean clearing = false;
    
    public static void makeGUI(){
        frame = new JFrame("Parachute");
        frame.setResizable(false);
        frame.setPreferredSize(PAGE_SIZE);
        frame.setMinimumSize(PAGE_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBackground(backColor);
        
        
        page = new JPanel(new BorderLayout());
        page.setPreferredSize(PAGE_SIZE);  
        page.setOpaque(true);
        page.setBackground(backColor);

        parachuteIO = new JPanel(new FlowLayout(FlowLayout.CENTER));
        parachuteIO.setPreferredSize(PAGE_SIZE);  
        parachuteIO.setOpaque(true);
        parachuteIO.setBackground(backColor);
        pImage = new Parachute(backColor);
        parachuteIO.add(pImage);

        textOutput = new JPanel(new FlowLayout(FlowLayout.CENTER));

        message = new JLabel("Your Message: ");
        shiftc = new JButton("Shift (C)");
        shiftcc = new JButton("Shift (CC)");

        inputField = new JTextField("0.0.0.0.0.0.0.0.  0.0.0.0.0.0.0.0.  0.0.0.0.0.0.0.0.  0.0.0.0.0.0.0.0.  ");
        inputField.setPreferredSize(TEXT_SIZE);
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateOutput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateOutput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateOutput();
            }

            private void updateOutput() {
                GUI.setMessage();
                String message = inputField.getText();
                if(!clearing){
                    String inBetween = Encode.ConvertToBinaryMessage(message);
                    if(!inBetween.equals(pastInBetween)){
                        pastInBetween = inBetween;
                        pImage.setInBetween(inBetween);
                        
                    }
                }
            }
        });

        shiftcc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event){
                pImage.shiftCounterClockwise();
            }
        });

        shiftc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event){
                pImage.shiftClockwise();
            }
        });

        textOutput.add(message);
        textOutput.add(inputField);
        textOutput.add(shiftc);
        textOutput.add(shiftcc);
        
        page.add(parachuteIO);
        page.add(textOutput, BorderLayout.SOUTH);
        frame.add(page);
        
        frame.setVisible(true);
    }

    public static void setError(String er){
        message.setText("Error ("+er+"): ");
    }

    public static void setMessage(){
        message.setText("Your Message: ");
    }

    public static void setInput(String message){
        setMessage();
        clearing = true;
        inputField.setText(message);
        clearing = false;
    }

    public static void main(String[] args) {
        GUI.makeGUI();
    }
}
