package kr.ac.ajou.view;

import processing.core.PApplet;

public class Dice implements Displayable {

    private final static int CURVE_VALUE = 20;

    private final float rectX;
    private final float rectY;
    private final float diameter;
    private final int diceNum;

    private float dotExtent;
    private float textSize;

    private String label;

    public Dice(float rectX, float rectY, float diameter, int diceNum) {
        this.rectX = rectX;
        this.rectY = rectY;
        this.diameter = diameter;
        this.diceNum = diceNum;
        dotExtent = getDotExtent();
        textSize = getTextSize();
        label = "";
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private float getDotExtent() {
        return diameter / 5;
    }

    private float getTextSize() {
        return diameter / 4;
    }

    public void display(PApplet p) {

        drawFrame(p);
        drawText(p);
        drawDot(p);
    }

    private void drawFrame(PApplet p) {
        p.fill(Color.WHITE.getValue());
        p.rect(rectX, rectY, diameter, diameter, CURVE_VALUE);
    }

    private void drawText(PApplet p) {
        p.textAlign(p.CENTER, p.CENTER);
        p.textSize(textSize);
        p.fill(Color.BLACK.getValue());
        p.text(label, rectX + (diameter / 2), rectY - textSize);

    }

    private void drawDot(PApplet p) {
        p.fill(Color.BLACK.getValue());
        for (int i = 1; i <= diceNum; i++) {
            if (i <= 3) {
                p.circle(rectX + diameter / 3, rectY + i * diameter / 4, dotExtent);
            } else {
                p.circle(rectX + (2 * diameter) / 3, rectY + ((i - 3) * diameter) / 4, dotExtent);
            }
        }
    }
}