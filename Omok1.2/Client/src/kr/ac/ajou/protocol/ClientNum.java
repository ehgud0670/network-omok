package kr.ac.ajou.protocol;

public class ClientNum {

    public static final int ONE = 1;
    public static final int TWO = 2;

    private int clientNum;

    public ClientNum(int clientNum) {
        this.clientNum = clientNum;
    }

    public void setClientNum(int clientNum) {
        this.clientNum = clientNum;
    }

    public int getClientNum() {
        return clientNum;
    }
}
