package kr.ac.ajou.protocol;

public class DiceNum {
    private int myDiceNum;
    private int opponentDiceNum;
    private boolean myTurn;

    public DiceNum(int myDiceNum, int opponentDiceNum, boolean myTurn) {
        this.myDiceNum = myDiceNum;
        this.opponentDiceNum = opponentDiceNum;
        this.myTurn = myTurn;
    }

    public int getMyDiceNum() {
        return myDiceNum;
    }

    public int getOpponentDiceNum() {
        return opponentDiceNum;
    }

    public boolean getMyTurn() {
        return myTurn;
    }

}
