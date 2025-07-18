package Encoder;
import java.util.ArrayList;

public class Parachute{
    private Row[] parachute;

    public Parachute(){
        parachute = new Row[4];
        for(int i = 0; i < parachute.length; i++){
            parachute[i] = new Row();
        }
    }

    public void shiftRows(int[] shift){
        for(int i = 0; i < shift.length; i++){
            parachute[i].shiftRow(shift[i]);
        }
    }

    public void setRows (ArrayList<Word> map){
        for(int i = 0; i < parachute.length; i++){
            try{
                parachute[i].setRow(map.get(i));
                parachute[i].fillGaps();
            }catch(Exception e){
                parachute[i].setBlack();
            }
        }
    }

    public String toString(){
        String message = "";
        for(Row r : parachute){
            message += r.toString();
        }
        return message;
    }
}