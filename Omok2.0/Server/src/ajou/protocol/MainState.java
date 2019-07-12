package ajou.protocol;

public enum MainState {


    LOGIN_STATE(0),
    LOBBY_STATE(1),
    ROOM_STATE(2);

    public static final int LOGIN_VALUE = 0;
    public static final int LOBBY_VALUE = 1;
    public static final int ROOM_VALUE = 2;

    private int state;

    MainState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
