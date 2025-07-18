package Decoder;
import java.util.ArrayList;

public class Decode {
    private static ArrayList<Line> lines;
   
    public static String decodeBinaryMessage(String message){
        lines = new ArrayList<Line>();
        int index = 0;
        for(int i = 0; i < message.length(); i+=(message.length()/4)){
            lines.add(new Line(message.substring(i, i+(message.length()/4)), index));
            index = lines.get(i/(message.length()/4)).findEnd();
        }
        return createMessage();
    }

    public static String createMessage(){
        String message = "";
        for(Line l : lines){
            message += l.toString();
        }
        return message;
    }
}