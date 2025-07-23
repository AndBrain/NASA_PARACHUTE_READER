package Encoder;
import java.util.ArrayList;

public class Word{
    private ArrayList<Element> elements;

    private final String ALLOWED_NUMS = "1234567890";
    //Maximum number of letters in a word
    private final int MAX_LETTERS = 8;
    private final String KEY = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /*NASA's method of encoding numbers makes no sense when automated as they origninally encoded numbers that were used as coordinates, 
    thus they sometimes would allow the encoding of three digit numbers (like 115) whilst other times would seperate them into two digit numbers (like 1 15).
    This sadly means that to get a NASA accurate parachute as seen in the Perserverance Parachute we have to jimmy-rig the code to account for this difference.
    It is better to have this value set to True, HOWEVER, if you are trying to recreate the NASA Perseverance Parahcute you will sadly need to change this value to False,
    Causing the code to use a worse and jimmy-rigged encoding method for it's numbers. 
    
    TLDR: ONLY SET TO FALSE WHEN TRYING TO REPLICATE THE PERSEVERANCE PARACHUTE AND ITS COORDS!  
    */
    private final boolean MAX_NUM_STORAGE = false;

    public Word(){
        elements = new ArrayList<Element>();
    }

    public void addElement(String element){
        if(ALLOWED_NUMS.indexOf(element)==-1){
            elements.add(new Element(element, false));
        }else{
            elements.add(new Element(element, true));
        }
    }

    public void combineNums(){
        for(int i = 0; i < elements.size()-1; i++){
            if (elements.get(i).getIsNum() && elements.get(i+1).getIsNum()) {
                if(validCombine(elements.get(i).getValue(), elements.get(i+1).getValue())){
                    combineNums(i);
                    elements.remove(i+1);
                    if(MAX_NUM_STORAGE || (i==4)){
                        i-=1;
                    }
                }
            }
        }
    }

    public boolean validCombine(String first, String second){
        return ((Integer.parseInt(first)*10 + Integer.parseInt(second)) <= 126 && Integer.parseInt(first)!=0);
    }

    public void combineNums(int index){
        int tensPlace = 10 * Integer.parseInt(elements.get(index).getValue());
        int onesPlace = Integer.parseInt(elements.get(index+1).getValue());
        elements.get(index).setValue(""+(tensPlace + onesPlace));
    }

    public void validate(){
        if(elements.size() > MAX_LETTERS){
            throw new IllegalArgumentException("To Many Letters In " + toString());
        }
        for(Element element : elements){
            element.validate();
        }
    }

    public int getScore(int index){
        Element e = elements.get(index);
        if(e.getIsNum()){
            return Integer.parseInt(e.getValue());
        }else{
            return KEY.indexOf(e.getValue());
        }
    }

    public ArrayList<Element> getElements(){
        return elements;
    }

    public int size(){
        return elements.size();
    }

    public String toString(){
        String output = "";
        for (Element element: elements){
            output += element.getValue() + "";
        }
        return output;
    }
}