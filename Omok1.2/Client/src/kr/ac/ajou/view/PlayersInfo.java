package kr.ac.ajou.view;

import kr.ac.ajou.protocol.ClientNum;
import processing.core.PApplet;

public class PlayersInfo implements Displayable {

    private static final int TEXT_SIZE = 35;
    private static final int NONE = 0;

    public static final String MINE_LABEL = "Mine";
    public static final String OPPONENT_LABEL = "Enemy";
    public static final String NONE_LABEL = "";

    private final float rectX;
    private final float rectY;
    private final float width;
    private final float height;

    private String mineLabel;
    private String opponentLabel;

    private ClientNum clientNum;


    public PlayersInfo(float rectX, float rectY, float width, float height) {
        this.rectX = rectX;
        this.rectY = rectY;
        this.width = width;
        this.height = height;
        clientNum = new ClientNum(NONE);
        mineLabel = "";
        opponentLabel = "";
    }

    @Override
    public void display(PApplet p) {
        drawFrame(p);
        drawMineLabel(p);
        drawOpponentLabel(p);
    }

    private void drawFrame(PApplet p) {
        p.fill(Color.GREY.getValue());
        p.rect(rectX, rectY, width, height);
    }

    private void drawMineLabel(PApplet p) {
        p.textAlign(p.CENTER, p.CENTER);
        p.fill(Color.BLACK.getValue());
        p.textSize(TEXT_SIZE);
        if (clientNum.getClientNum() == 1) {
            p.text(mineLabel, rectX + (3 * width / 5), rectY + (height / 4));
        } else if (clientNum.getClientNum() == 2) {
            p.text(mineLabel, rectX + (3 * width / 5), rectY + ((2 * height) / 3));
        }
    }

    private void drawOpponentLabel(PApplet p) {
        p.textAlign(p.CENTER, p.CENTER);
        p.fill(Color.BLACK.getValue());
        p.textSize(TEXT_SIZE);
        if (clientNum.getClientNum() == 1) {
            p.text(opponentLabel, rectX + (3 * width / 5), rectY + ((2 * height) / 3));
        } else if (clientNum.getClientNum() == 2) {
            p.text(opponentLabel, rectX + (3 * width / 5), rectY + (height / 4));
        }
    }

    public void setMineLabel(String mineLabel) {
        this.mineLabel = mineLabel;
    }

    public void setOpponentLabel(String opponentLabel) {
        this.opponentLabel = opponentLabel;
    }

    public void setClientNum(ClientNum clientNum) {
        this.clientNum = clientNum;
    }
}
