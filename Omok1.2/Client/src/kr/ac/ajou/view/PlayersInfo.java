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

    private StoneView mineStoneView;
    private StoneView opponentStoneView;

    private ClientNum clientNum;


    public PlayersInfo(float rectX, float rectY, float width, float height) {
        this.rectX = rectX;
        this.rectY = rectY;
        this.width = width;
        this.height = height;
        clientNum = new ClientNum(NONE);
        mineLabel = "";
        opponentLabel = "";


        // INIT StoneViews
        mineStoneView = null;
        opponentStoneView = null;
    }

    @Override
    public void display(PApplet p) {
        drawFrame(p);
        drawMineLabel(p);
        drawOpponentLabel(p);

        if (mineStoneView != null) {
            drawMineStoneView(p);
        }

        if (opponentStoneView != null) {
            drawOpponentStoneView(p);
        }
    }

    private void drawMineStoneView(PApplet p) {
        p.fill(mineStoneView.getColor());
        if (clientNum.getClientNum() == 1) {
            p.ellipse(rectX + (1 * width / 5), rectY + (height / 4) + 5, 35, 35);
        } else if (clientNum.getClientNum() == 2) {
            p.ellipse(rectX + (1 * width / 5), rectY + ((2 * height) / 3) + 5, 35, 35);
        }

    }

    private void drawOpponentStoneView(PApplet p) {
        p.fill(opponentStoneView.getColor());
        if (clientNum.getClientNum() == 1) {
            p.ellipse(rectX + (1 * width / 5), rectY + ((2 * height) / 3) + 5, 35, 35);
        } else if (clientNum.getClientNum() == 2) {
            p.ellipse(rectX + (1 * width / 5), rectY + (height / 4) + 5, 35, 35);
        }
    }


    private void drawFrame(PApplet p) {
        p.fill(Color.DARK_GREY.getValue());
        p.strokeWeight(2);
        p.rect(rectX, rectY, width, height);
        p.strokeWeight(1);
    }

    private void drawMineLabel(PApplet p) {
        p.textAlign(p.CENTER, p.CENTER);
        p.fill(Color.LIGHT_GREY.getValue());
        p.textSize(TEXT_SIZE);
        if (clientNum.getClientNum() == 1) {
            p.text(mineLabel, rectX + (3 * width / 5), rectY + (height / 4));
        } else if (clientNum.getClientNum() == 2) {
            p.text(mineLabel, rectX + (3 * width / 5), rectY + ((2 * height) / 3));
        }
    }

    private void drawOpponentLabel(PApplet p) {
        p.textAlign(p.CENTER, p.CENTER);
        p.fill(Color.LIGHT_GREY.getValue());
        p.textSize(TEXT_SIZE);
        if (clientNum.getClientNum() == 1) {
            p.text(opponentLabel, rectX + (3 * width / 5), rectY + ((2 * height) / 3));
        } else if (clientNum.getClientNum() == 2) {
            p.text(opponentLabel, rectX + (3 * width / 5), rectY + (height / 4));
        }
    }

    public void stoneViewsInit() {
        mineStoneView = null;
        opponentStoneView = null;
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

    public void setMineStoneView(int color) {
        mineStoneView = new StoneView(color);
    }

    public void setOpponentStoneView(int color) {
        opponentStoneView = new StoneView(color);
    }
}
