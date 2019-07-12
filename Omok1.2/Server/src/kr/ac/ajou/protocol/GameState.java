package kr.ac.ajou.protocol;

public class GameState {
    public static final int WAITING = 0;
    public static final int SET_ORDER = 1;
    public static final int RUNNING = 2;
    public static final int GAME_OVER = 3;

    private int gameState;

    public GameState(int gameState) {
        this.gameState = gameState;
    }

    public int getGameState() {
        return gameState;
    }
}
