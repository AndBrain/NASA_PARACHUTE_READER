package Encoder;

public class Element{
    private String value;
    private boolean isNum;

    //Maximum number that can be represented in a single element
    private final int MAX_NUM = 126;

    public Element(String value, boolean isNum){
        this.isNum = isNum;
        this.value = value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public boolean getIsNum(){
        return isNum;
    }

    public void validate(){
        if(isNum && (Integer.parseInt(value)>MAX_NUM || Integer.parseInt(value)<0)){
            throw new IllegalArgumentException("Invalid Number: " + value);
        }
    }
}