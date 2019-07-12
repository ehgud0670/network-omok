package ajou.view;

import processing.core.PApplet;

public class RoomBox implements Displayable {

    private static final String ROOM_TITLE = "ROOM";

    private final float rectX;
    private final float rectY;
    private final float width;
    private final float height;

    public RoomBox(float rectX, float rectY, float width, float height){
        this.rectX = rectX;
        this.rectY = rectY;
        this.width = width;
        this.height = height;
    }

    @Override
    public void display(PApplet p) {
        drawFrame(p);
        drawTitle(p);
    }
    private void drawFrame(PApplet p) {
        setStrokeGrey(p);
        fillGrey(p);
        p.rect(rectX, rectY, width, height);
    }


    private void drawTitle(PApplet p) {
        fillBlack(p);
        p.textAlign(PApplet.CENTER);
        p.textSize(100);
        p.text(ROOM_TITLE, rectX + width / 2, rectY + height / 4);
    }


}
