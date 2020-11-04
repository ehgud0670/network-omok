package kr.ac.ajou.view;


import kr.ac.ajou.protocol.ConstantProtocol;
import processing.core.PApplet;

public class ClientOmokPlate implements Displayable {
    private static final int NUM = 15;

    private final int length;
    private final int externalXValue;
    private final int externalYValue;

    private final int stoneDiameter;
    private final int edge;
    private final int block;
    private final int rangeSize;

    private Stone[][] stones = new Stone[NUM][NUM];

    public ClientOmokPlate(int length, int externalXValue, int externalYValue) {
        this.length = length;
        this.externalXValue = externalXValue;
        this.externalYValue = externalYValue;

        stoneDiameter = getStoneDiameter();
        edge = getEdge();
        block = getBlock();
        rangeSize = getRangeSize();

        initStones();
    }

    private int getStoneDiameter() {
        return length / 20;
    }

    private int getEdge() {
        return length / 30;
    }

    private int getBlock() {
        return (length - 2 * edge) / (NUM - 1);
    }

    private int getRangeSize() {
        return block / 4;
    }


    @Override
    public void display(PApplet p) {
        drawFrame(p);
        drawCoordinate(p);
        drawFourDot(p);
        drawStones(p);
    }

    private void drawFrame(PApplet p) {
        p.noStroke();
        p.fill(Color.GREY.getValue());
        p.rect(externalXValue, externalYValue, length, length);

        p.fill(Color.LIGHT_GREY.getValue());
        p.stroke(Color.BLACK.getValue());
        p.strokeWeight(2);
        p.rect(externalXValue + edge , externalYValue + edge,  (NUM - 1) * block, (NUM - 1) * block);
    }

    private void drawCoordinate(PApplet p) {
        p.strokeWeight(1);
        for (int i = 0; i < NUM; i++) {
            p.line(externalXValue + edge, externalYValue + edge + i * block,
                    externalXValue + edge + (NUM - 1) * block, externalYValue + edge + i * block);
            p.line(externalXValue + edge + i * block, externalYValue + edge,
                    externalXValue + edge + i * block, externalYValue + edge + (NUM - 1) * block);
        }
    }

    private void drawFourDot(PApplet p) {
        p.fill(Color.BLACK.getValue());
        p.ellipse(externalXValue + edge + 4 * block, externalYValue + edge + 4 * block, 8, 8);
        p.ellipse(externalXValue + edge + 4 * block, externalYValue + edge + 10 * block, 8, 8);
        p.ellipse(externalXValue + edge + 10 * block, externalYValue + edge + 4 * block, 8, 8);
        p.ellipse(externalXValue + edge + 10 * block, externalYValue + edge + 10 * block, 8, 8);
    }

    private void drawStones(PApplet p) {
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                if (stones[i][j].getValue() == Stone.BLACK_STONE) { //Black
                    p.fill(Color.BLACK.getValue());
                    p.ellipse(edge + j * block + externalXValue, edge + i * block + externalYValue,
                            stoneDiameter, stoneDiameter);
                } else if (stones[i][j].getValue() == Stone.WHITE_STONE) { //white
                    p.fill(Color.WHITE.getValue());
                    p.ellipse(edge + j * block + externalXValue, edge + i * block + externalYValue,
                            stoneDiameter, stoneDiameter);
                }
            }
        }
    }

    public int editPosition(int mousePos) {

        int quotient = (mousePos - edge) / block;
        int remainder = (mousePos - edge) % block;

        if (remainder >= block - rangeSize) {
            return (quotient + 1) * block;
        } else if (remainder <= rangeSize) {
            return (quotient) * block;
        }
        return 0;
    }

    public int getIndex(int fixPos) {
        return fixPos / block;
    }

    public boolean overVertex(int mouseX, int mouseY) {

        int currentX = mouseX - externalXValue;
        int currentY = mouseY - externalYValue;

        if (checkRange(currentX, currentY)) {
            return checkHasStone(currentX, currentY);
        }
        return false;
    }

    private boolean checkRange(int currentX, int currentY) {
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                if (((edge - rangeSize + block * i) < currentX) &&
                        ((edge + rangeSize + block * i) > currentX) &&
                        ((edge - rangeSize + block * j) < currentY) &&
                        ((edge + rangeSize + block * j) > currentY)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkHasStone(int currentX, int currentY) {
        return stones[getIndex(editPosition(currentY))][getIndex(editPosition(currentX))].getValue() == 0;
    }


    public void recordStone(int row, int col, int stoneFlag) {
        int value = stoneFlag == ConstantProtocol.BLACK_STONE ? Stone.BLACK_STONE : Stone.WHITE_STONE;
        stones[row][col].setValue(value);
    }

    public void initStones() {
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                stones[i][j] = new Stone(Stone.NONE_STONE);
            }
        }
    }
}
