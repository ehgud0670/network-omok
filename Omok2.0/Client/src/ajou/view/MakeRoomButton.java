package ajou.view;

import processing.core.PApplet;

public class MakeRoomButton implements Button {

    private static final int TEXT_SIZE = 20;
    private static final String MAKE_ROOM_LABEL = "create a Room";

    private final float rectX;
    private final float rectY;
    private final float width;
    private final float height;


    MakeRoomButton(float rectX, float rectY, float width, float height) {
        this.rectX = rectX;
        this.rectY = rectY;
        this.width = width;
        this.height = height;
    }


    @Override
    public boolean overButton(int mouseX, int mouseY) {
        return mouseX >= rectX && mouseX <= rectX + width &&
                mouseY >= rectY && mouseY <= rectY + height;
    }

    @Override
    public void display(PApplet p) {
        drawFrame(p);
        drawText(p);
    }
    private void drawFrame(PApplet p) {
        setStrokeGreen(p);
        fillGreen(p);
        p.rect(rectX, rectY, width, height);
    }

    private void drawText(PApplet p) {
        p.textAlign(p.CENTER, p.CENTER);
        fillBlack(p);
        p.textSize(TEXT_SIZE);
        p.text(MAKE_ROOM_LABEL, rectX + (width / 2), rectY + (height / 2));
    }
}
