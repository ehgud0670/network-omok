package kr.ac.ajou.view;

import kr.ac.ajou.protocol.CountDownNum;
import processing.core.PApplet;

public class CountDown implements Displayable {


    private static final int NONE = 0;

    private final int omokPlateLength;
    private final int omokPlateExternalXValue;
    private final int omokPlateExternalYValue;
    private final int textSize;

    private CountDownNum countDownNum;

    public CountDown(int omokPlateLength, int omokPlateExternalXValue, int omokPlateExternalYValue) {
        this.omokPlateLength = omokPlateLength;
        this.omokPlateExternalXValue = omokPlateExternalXValue;
        this.omokPlateExternalYValue = omokPlateExternalYValue;
        textSize = getTextSize();
        countDownNum = new CountDownNum(0);
    }

    private int getTextSize() {
        return omokPlateLength / 12;
    }

    public void display(PApplet p) {
        if (countDownNum.getCountDown() == 0) {
            p.text("", NONE, NONE);

        } else {
            p.fill(Color.BLACK.getValue());
            p.textSize(textSize);
            p.text(countDownNum.getCountDown(), omokPlateExternalXValue + omokPlateLength / 2, omokPlateExternalYValue + omokPlateLength / 2);
        }
    }

    public void setCountDownNum(CountDownNum countDownNum) {
        this.countDownNum = countDownNum;
    }
}
