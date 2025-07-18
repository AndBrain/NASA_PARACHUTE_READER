package Encoder;
public class Section{
    private int lengthSection;
    private int value;
    private String binary;

    public Section(int value, boolean isFluff){
        if(isFluff){
            lengthSection = 3;
            this.value = 0;
        }else{
            lengthSection = 7;
            this.value = value;
        }
        convertToBinary();
    }

    public void convertToBinary(){
        String binary = "00000000";
        int tempValue = value;
        int index = 0;
        for(int i = 128; i >= 1; i/=2){
            if(tempValue>=i){
                tempValue-=i;
                binary = binary.substring(0, index) + "1" + binary.substring(index+1);
            }
            index+=1;
        }
        int i = 8 - lengthSection;
        binary = binary.substring(i);
        this.binary = binary;
    }

    public void setValue(int v){
        value = v;
        convertToBinary();
    }

    public int getValue(){
        return value;
    }

    public String toString(){
        return binary;
    }
}