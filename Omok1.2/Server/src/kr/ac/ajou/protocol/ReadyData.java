package kr.ac.ajou.protocol;

public class ReadyData {
    public static int READY = 1;
    public static int NOT_READY = -1;

    private int readyNum;

    public ReadyData(int readyNum) {
        this.readyNum = readyNum;
    }

    public int getReadyNum() {
        return readyNum;
    }

    public void plusReadyNum(int readyNum) {
        this.readyNum += readyNum;
    }

    public void setReadyNum(int readyNum) {
        this.readyNum = readyNum;
    }
}
