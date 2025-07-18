package Encoder;
import java.util.ArrayList;

public class Encode {
    private static ArrayList<String> characters;
    private static String VALID_CHARACTERS = "1234567890QWERTYUIOPASDFGHJKLZXCVBNM ";
    
    public static String ConvertToBinaryMessage(String message){
        Parachute p = new Parachute();
        int[] start = new int[4];
        MapOfMessage messageMap = new MapOfMessage();
        try{
            validateCharacters(message);
            removeRepeatedSpaces();
            for (String character : characters){
                messageMap.addCharacter(character);
            }
            messageMap.combineNums();
            messageMap.validate();
        }catch(IllegalArgumentException error){
            Output.handleError(error);
            return p.toString();
        }

        ArrayList<Word> map = messageMap.getWords();
        
        for(int i = 1; i < start.length; i++){
            start[i] = start[i-1];
            try{
                start[i] += map.get(i-1).size()*2;
            }catch(Exception e){}
                start[i] = start[i]%16;
        }

        p.setRows(map);
        p.shiftRows(start);

        return p.toString();
    }

    public static void validateCharacters(String message){
        characters = new ArrayList<String>();
        message = message.toUpperCase();
        for(int i = 0; i < message.length(); i++){
            String character = message.substring(i,i+1);
            if(VALID_CHARACTERS.indexOf(character)!=-1){
                // throw new IllegalArgumentException("Invalid Character: " + character);
                characters.add(character);
            }
        }
    }

    public static void removeRepeatedSpaces(){

        while(characters.size()>0 && characters.get(0).equals(" ")){
            characters.remove(0);
        }

        while(characters.size()>0 && characters.get(characters.size()-1).equals(" ")){
            characters.remove(characters.size()-1);
        }

        for(int i = 0; i < characters.size()-1; i++){
            if (characters.get(i).equals(characters.get(i+1)) && characters.get(i).equals(" ")) {
                characters.remove(i);
                i-=1;
            }
        }
    }
}
