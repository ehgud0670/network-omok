package ajou.view;

public enum Color {

    BLACK(0, 0, 0),
    GREY(204, 204, 204),
    WHITE(255, 255, 255),
    GREEN(0, 255, 102);

    private int valueR;
    private int valueG;
    private int valueB;


    Color(int valueR, int valueG, int valueB) {
        this.valueR = valueR;
        this.valueG = valueG;
        this.valueB = valueB;
    }

    public int getValueR() {
        return valueR;
    }

    public int getValueG() {
        return valueG;
    }

    public int getValueB() {
        return valueB;
    }
}
