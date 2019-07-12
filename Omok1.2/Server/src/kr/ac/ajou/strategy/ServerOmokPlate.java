package kr.ac.ajou.strategy;

import kr.ac.ajou.protocol.ConstantProtocol;

import java.util.Objects;


public class ServerOmokPlate {

    private static final int NUM = 15;
    private static final int FIRST_TIME = 1;
    private static final int LAST_TIME = 5;

    private static final int IS_EQUAL = 1;
    private static final int ALL_EQUALS = 4;


    private Stone[][] stones;

    public ServerOmokPlate() {
        stones = new Stone[NUM][NUM];
        initStones();
    }


    private boolean checkHasStone(int row, int col) {
        return stones[row][col].getValue() == Stone.NONE_STONE;
    }

    public void putStone(int row, int col, int stoneColor) {

        if (checkHasStone(row, col)) {

            int value = stoneColor == ConstantProtocol.BLACK_STONE ? Stone.BLACK_STONE : Stone.WHITE_STONE;
            stones[row][col].setValue(value);

        }else{
            System.out.println("stone exist!");
            throw new AssertionError();
        }
    }


    public void initStones() {
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                stones[i][j] = new Stone(Stone.NONE_STONE);
            }
        }
    }

    public boolean winCheck(int row, int col) {
        // Horizontal
        return checkColorEqual(row, col, FIRST_TIME, Direction.RIGHT) + checkColorEqual(row, col, FIRST_TIME, Direction.LEFT) == ALL_EQUALS ||
                checkColorEqual(row, col, FIRST_TIME, Direction.DOWN) + checkColorEqual(row, col, FIRST_TIME, Direction.UP) == ALL_EQUALS ||
                checkColorEqual(row, col, FIRST_TIME, Direction.DOWN_RIGHT) + checkColorEqual(row, col, FIRST_TIME, Direction.UP_LEFT) == ALL_EQUALS ||
                checkColorEqual(row, col, FIRST_TIME, Direction.DOWN_LEFT) + checkColorEqual(row, col, FIRST_TIME, Direction.UP_RIGHT) == ALL_EQUALS;

    }

    private int checkColorEqual(int row, int col, int times, Direction direction) {

        try {
            if (stones[row][col].getValue() == Objects.requireNonNull(getStoneAround(row, col, times, direction)).getValue()) {
                if (times == LAST_TIME) {
                    return IS_EQUAL;
                } else {
                    return IS_EQUAL + checkColorEqual(row, col, times + 1, direction);
                }
            } else {
                return 0;
            }
        } catch (NullPointerException ignored) {
        }
        return 0;
    }

    private Stone getStoneAround(int row, int col, int times, Direction direction) {
        try {
            return stones[row + direction.moveRow(times)][col + direction.moveCol(times)];
        } catch (ArrayIndexOutOfBoundsException ignored) {

        }
        return null;
    }

}
