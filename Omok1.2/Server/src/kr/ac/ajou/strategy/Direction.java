package kr.ac.ajou.strategy;

public enum Direction {
    RIGHT{
        @Override
        int moveRow(int times) {
            return 0;
        }

        @Override
        int moveCol(int times) {
            return +times;
        }
    },
    LEFT{
        @Override
        int moveRow(int times) {
            return 0;
        }

        @Override
        int moveCol(int times) {
            return -times;
        }
    },
    DOWN{
        @Override
        int moveRow(int times) {
            return +times;
        }

        @Override
        int moveCol(int times) {
            return 0;
        }
    },
    UP{
        @Override
        int moveRow(int times) {
            return -times;
        }

        @Override
        int moveCol(int times) {
            return 0;
        }
    },
    DOWN_RIGHT{
        @Override
        int moveRow(int times) {
            return +times;
        }

        @Override
        int moveCol(int times) {
            return +times;
        }
    },
    UP_LEFT{
        @Override
        int moveRow(int times) {
            return -times;
        }

        @Override
        int moveCol(int times) {
            return -times;
        }
    },
    DOWN_LEFT{
        @Override
        int moveRow(int times) {
            return +times;
        }

        @Override
        int moveCol(int times) {
            return -times;
        }
    },
    UP_RIGHT{
        @Override
        int moveRow(int times) {
            return -times;
        }

        @Override
        int moveCol(int times) {
            return +times;
        }
    };

    abstract int moveRow(int times);
    abstract int moveCol(int times);
}
