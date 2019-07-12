package ajou.view;

import processing.core.PApplet;

public class RoomButton implements Button{

    private static final String LABEL = "room";
    private static int TEXT_SIZE = 20;

    private final float rectX;
    private final float rectY;
    private final float width;
    private final float height;

    RoomButton(float rectX, float rectY, float width, float height){
        this.rectX = rectX;
        this.rectY = rectY;
        this.width = width;
        this.height = height;
    }


    @Override
    public void display(PApplet p) {
        drawFrame(p);
        drawText(p);
    }
    private void drawFrame(PApplet p) {
        setStrokeBlack(p);
        fillBlack(p);
        p.rect(rectX, rectY, width, height);
    }
    private void drawText(PApplet p) {
        p.textAlign(p.CENTER, p.CENTER);
        fillWhite(p);
        p.textSize(TEXT_SIZE);
        p.text(LABEL, rectX + (width / 2), rectY + (height / 2));
    }

    @Override
    public boolean overButton(int mouseX, int mouseY) {
        return mouseX >= rectX && mouseX <= rectX + width &&
                mouseY >= rectY && mouseY <= rectY + height;
    }
}
