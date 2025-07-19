package Encoder;
import java.util.ArrayList;

public class Encode {
    private static ArrayList<String> characters;
    private static String VALID_CHARACTERS = "1234567890QWERTYUIOPASDFGHJKLZXCVBNM ";
    
    /**
     * Converts a given message into binary that can be fed into the GUI. 
     * If the message can't be enocoded it sends the specific reason to the GUI.
     * @param message Non-cleaned message that will be enocded into binary
     * @return A String of the binary encoded message (ie. "1010100...")
     */
    public static String ConvertToBinaryMessage(String message){
        //Parachute holds the rew binary enocoded message. Holds all 0's to begin with
        Parachute p = new Parachute();
        
        //messageMap is a middle man that maps and breaks down the message to make it easier to understand
        MapOfMessage messageMap = new MapOfMessage();

        //Make list of characters that we will try to be encode in the message
        validateCharacters(message);
        removeRepeatedSpaces();

        /*Creates a "diagram" of the message structure and validates that it can be encoded
        If can't be encoded then will return the current all-zeroed binary and throw an error
        */
        try{
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

        //Creates a list that indicates where each rings start point for the encoded message is
        int[] start = new int[4];       
        for(int i = 1; i < start.length; i++){
            start[i] = start[i-1];
            try{
                start[i] += map.get(i-1).size()*2;
            }catch(Exception e){}
                start[i] = start[i]%16;
        }

        //Goes off of the message map to create the raw binary encoded message and then formats the message as dictated by start
        p.setRows(map);
        p.shiftRows(start);

        return p.toString();
    }

    /**
    Intitializes a new ArrayList containing valid, uppercase characters of the message (one character per index)
    */
    public static void validateCharacters(String message){
        characters = new ArrayList<String>();
        message = message.toUpperCase();
        for(int i = 0; i < message.length(); i++){
            String character = message.substring(i,i+1);
            if(VALID_CHARACTERS.indexOf(character)!=-1){
                characters.add(character);
            }
        }
    }

    /**
     * Cleans the character arraylist so that we can use the spaces to seperate and identify words
     */
    public static void removeRepeatedSpaces(){
        //Removes spaces from the front of the message
        while(characters.size()>0 && characters.get(0).equals(" ")){
            characters.remove(0);
        }
        //Removes spaces from the back of the message
        while(characters.size()>0 && characters.get(characters.size()-1).equals(" ")){
            characters.remove(characters.size()-1);
        }
        //Remove duplicated spaces
        for(int i = 0; i < characters.size()-1; i++){
            if (characters.get(i).equals(characters.get(i+1)) && characters.get(i).equals(" ")) {
                characters.remove(i);
                i-=1;
            }
        }
    }
}
