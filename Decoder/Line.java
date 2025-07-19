package Decoder;
import java.util.ArrayList;

public class Line {
    private ArrayList<Integer> sectionIntegers;
    private int startingIndex;
    private int length;

    private final String KEY = "0ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public Line(String line, int index){
        sectionIntegers = new ArrayList<Integer>();
        startingIndex = index%8;
        for(int i = 0; i < line.length(); i+=(line.length()/8)){
             String binary = (line.substring(i+3, i+(line.length()/8)));
             sectionIntegers.add(convertToInt(binary));
        }
        length = 0;
        howLong();
    }

    public void howLong(){
        for(int value : sectionIntegers){
            if(value != 127){ 
                length++;
            }
        }
    }

    public int findEnd(){
        return startingIndex + length;
    }

    public int convertToInt(String binary){
        int sum = 0;
        int step = 1;
        for(int i = binary.length()-1; i >= 0; i--){
            if(binary.substring(i, i+1).equals("1")){
                sum += step;
            }
            step *= 2;
        }
        return sum;
    }

    public String toString(){
        int index = 0;
        String message = "";
        for(int i = 0; i < sectionIntegers.size(); i++){
            index = (startingIndex + i)%8;
            if(sectionIntegers.get(index)!=127){
                try{
                    message += KEY.substring(sectionIntegers.get(index), sectionIntegers.get(index)+1);
                }catch(Exception error){
                    message += sectionIntegers.get(index);
                }
                message += ".";
            }
        }
        return message;
    }
}
