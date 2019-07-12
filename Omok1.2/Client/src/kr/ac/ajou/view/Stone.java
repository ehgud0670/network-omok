package kr.ac.ajou.view;

class Stone {

    static final int NONE_STONE = 0;
    static final int BLACK_STONE = 1;
    static final int WHITE_STONE = 2;

    private int value;

    Stone(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
    }

}
