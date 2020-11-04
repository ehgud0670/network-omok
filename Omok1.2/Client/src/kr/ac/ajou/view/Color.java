package kr.ac.ajou.view;

public enum Color {
    BLACK(0), DARK_GREY(110),GREY(150), LIGHT_GREY(180), WHITE(255);

    private int value;

    Color(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
