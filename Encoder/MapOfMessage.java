package Encoder;
import java.util.ArrayList;

public class MapOfMessage{
    private ArrayList<Word> map;
    private int wordIndex;

    public MapOfMessage(){
        map = new ArrayList<Word>();
        map.add(new Word());
        wordIndex = 0;
    }

    public ArrayList<Word> getWords(){
        return map;
    }

    public void addCharacter(String character){
        if(!character.equals(" ")){
            map.get(wordIndex).addElement(character);
        }else{
            map.add(new Word());
            wordIndex += 1;
        }
    }

    public void validate(){
        if(map.size()>4){
            throw new IllegalArgumentException("Too Many Words");
        }
        for(Word word : map){
            word.validate();
        }
    }

    public void combineNums(){
        for (Word word : map){
            word.combineNums();
        }
    }

    public Word getWord(int index) {
        return map.get(index);
    }

    public String toString(){
        String output = "";
        for (Word word : map){
            output += word.toString() + "\n"; 
        }
        return output;
    }
}