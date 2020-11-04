package kr.ac.ajou.protocol;

public class Location {
    private int row;
    private int col;
    private int color;

    public Location(int row, int col, int color) {
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
