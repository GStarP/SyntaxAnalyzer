package entity;

public class Token {

    private int type;
    private String content;

    public Token(int t, String c){
        this.type = t;
        this.content = c;
    }

    public String toString() {
        return "【" + this.type + ", " + this.content + "】";
    }
}
