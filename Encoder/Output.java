package Encoder;

import GUI.GUI;

public class Output {
    
    public static void handleError(Exception error){
        GUI.setError(error.getMessage());
    }
}
