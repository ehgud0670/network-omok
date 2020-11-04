package kr.ac.ajou.protocol;

public class StoneLocation {
    private int row;
    private int col;
    private int color;

    public StoneLocation(int row, int col, int color) {
        this.row = row;
        this.col = col;
        this.color = color;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getColor() {
        return color;
    }
}
