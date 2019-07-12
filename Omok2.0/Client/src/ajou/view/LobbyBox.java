package ajou.view;

import kr.ac.ajou.protocol.RoomNum;
import processing.core.PApplet;

public class LobbyBox implements Displayable {

    private final float rectX;
    private final float rectY;
    private final float width;
    private final float height;

    private final float roomWidth;
    private final float roomHeight;
    private final float edge;

    private MakeRoomButton makeRoomButton;
    private RoomButton firstRoomButton;
    private RoomButton secondRoomButton;
    private RoomButton thirdRoomButton;
    private RoomButton fourthRoomButton;


    public LobbyBox(float rectX, float rectY, float width, float height) {
        this.rectX = rectX;
        this.rectY = rectY;
        this.width = width;
        this.height = height;
        roomWidth = getRoomWidth();
        roomHeight = getRoomHeight();
        edge = getEdge();


        makeRoomButton = new MakeRoomButton(rectX + width / 3, rectY + height / 2 - 80,
                width / 3, 40);

    }

    private float getRoomWidth() {
        return width / 3;
    }

    private float getRoomHeight() {
        return height / 6;
    }

    private float getEdge() {
        return width / 9;
    }

    @Override
    public void display(PApplet p) {
        drawFrame(p);
        drawTitle(p);
        makeRoomButton.display(p);
        drawRoomFrames(p);

        if(firstRoomButton !=null){
            firstRoomButton.display(p);
        }
        if(secondRoomButton !=null){
            secondRoomButton.display(p);
        }
        if (thirdRoomButton !=null){
            thirdRoomButton.display(p);
        }

        if (fourthRoomButton !=null){
            fourthRoomButton.display(p);
        }


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
        p.text("LOBBY", rectX + width / 2, rectY + height / 4);
    }

    private void drawRoomFrames(PApplet p) {
        drawFirstRoomFrame(p);
        drawSecondRoomFrame(p);
        drawThirdRoomFrame(p);
        drawFourthRoomFrame(p);
    }

    private void drawFirstRoomFrame(PApplet p) {
        setStrokeBlack(p);
        p.strokeWeight(2);
        fillGrey(p);
        p.rect(rectX + edge, rectY + height / 2, roomWidth, roomHeight);
    }

    private void drawSecondRoomFrame(PApplet p) {
        setStrokeBlack(p);
        p.strokeWeight(2);
        fillGrey(p);
        p.rect(rectX + edge, rectY + height / 2 + roomHeight + edge, roomWidth, roomHeight);
    }

    private void drawThirdRoomFrame(PApplet p) {
        setStrokeBlack(p);
        p.strokeWeight(2);
        fillGrey(p);
        p.rect(rectX + 2 * edge + roomWidth, rectY + height / 2, roomWidth, roomHeight);
    }

    private void drawFourthRoomFrame(PApplet p) {
        setStrokeBlack(p);
        p.strokeWeight(2);
        fillGrey(p);
        p.rect(rectX + 2 * edge + roomWidth, rectY + height / 2 + roomHeight + edge, roomWidth, roomHeight);
    }

    public MakeRoomButton getMakeRoomButton() {
        return makeRoomButton;
    }

    public RoomButton getFirstRoomButton() {
        return firstRoomButton;
    }

    public RoomButton getSecondRoomButton() {
        return secondRoomButton;
    }

    public RoomButton getThirdRoomButton() {
        return thirdRoomButton;
    }

    public RoomButton getFourthRoomButton() {
        return fourthRoomButton;
    }

    public void updateRoomNum(RoomNum roomNumOn) {
        System.out.println("roomNumOn: " + roomNumOn.getRoomNum());
        switch (roomNumOn.getRoomNum()) {
            case 1:
                firstRoomButton = new RoomButton(rectX + edge, rectY + height / 2,
                        roomWidth, roomHeight);
                break;
            case 2:
                secondRoomButton = new RoomButton(rectX + edge, rectY + height / 2 + roomHeight + edge,
                        roomWidth, roomHeight);
                break;
            case 3:
                thirdRoomButton = new RoomButton(rectX + 2 * edge + roomWidth, rectY + height / 2,
                        roomWidth, roomHeight);
                break;
            case 4:
                fourthRoomButton = new RoomButton(rectX + 2 * edge + roomWidth, rectY + height / 2 + roomHeight + edge,
                        roomWidth, roomHeight);
                break;
        }
    }

    public void removeRoomNum(RoomNum roomNumOff) {
        System.out.println("roomNumOff: " + roomNumOff.getRoomNum());
        switch (roomNumOff.getRoomNum()){
            case 1:
                firstRoomButton = null;
                break;
            case 2:
                secondRoomButton = null;
                break;
            case 3:
                thirdRoomButton = null;
                break;
            case 4:
                fourthRoomButton = null;
                break;
        }
    }
}
