package Encoder;
import java.util.ArrayList;

public class Row{
    private Section[] values;

    private final String KEY = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public void shiftRow(int shift){
        for(int i = 0; i < shift; i++){
            shiftRight();
        }
    }

    public void shiftRight(){
        Section endOfValue = values[(values.length-1)];
        for(int i = values.length-1; i > 0; i--){
            values[i] = values[i-1];
        }
        values[0] = endOfValue;
    }

    public void fillGaps(){
        for(int index = 1; index < values.length-2; index+=2){
            if ((values[index].getValue()==values[index+2].getValue()) && values[index].getValue()==127){
                values[index+1].setValue(127);
            }
        }
        if((values[1].getValue()==values[15].getValue()) && values[1].getValue()==127){
            values[0].setValue(127);
        }
    }

    public void setRow(Word w){
        ArrayList<Element> elements = w.getElements();
        int i = 0;
        for (int index = 1; index < values.length; index+=2){
            try{
                Element e = elements.get(i);
                if (e.getIsNum()){
                    values[index].setValue(Integer.parseInt(e.getValue()));
                }else{
                    values[index].setValue(KEY.indexOf(e.getValue()));
                }
            }catch(Exception e){
                values[index].setValue(127);
           }
           i++;
        }
    }

    public void setBlack(){
        for (int index = 0; index < values.length; index++){
            values[index].setValue(127);
        }
    }

    public Row(){
        values = new Section[16];
        int fluff = 1;
        for(int i = 0; i < values.length; i++){
            values[i] = new Section(0, fluff==1);
            fluff = Math.abs(fluff-1);
        }
    }

    public String toString(){
        String message = "";
        for (Section s : values){
            message += s.toString();
        }
        return message;
    }

}