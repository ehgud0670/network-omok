package kr.ac.ajou.view;

public enum Color {

    BLACK(0), GREY(128), WHITE(255);

    private int value;

    Color(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
