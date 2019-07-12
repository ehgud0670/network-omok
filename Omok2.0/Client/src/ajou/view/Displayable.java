package ajou.view;

import processing.core.PApplet;

public interface Displayable {

    default void setStrokeBlack(PApplet p){
        p.stroke(Color.BLACK.getValueR(),
                Color.BLACK.getValueG(),
                Color.BLACK.getValueB());
    }

    default void setStrokeWhite(PApplet p){
        p.stroke(Color.WHITE.getValueR(),
                Color.WHITE.getValueG(),
                Color.WHITE.getValueB());
    }

    default void setStrokeGrey(PApplet p){
        p.stroke(Color.GREY.getValueR(),
                Color.GREY.getValueG(),
                Color.GREY.getValueB());
    }

    default void setStrokeGreen(PApplet p){
        p.stroke(Color.GREEN.getValueR(),
                Color.GREEN.getValueG(),
                Color.GREEN.getValueB());
    }

    default void fillBlack(PApplet p) {
        p.fill(Color.BLACK.getValueR(),
                Color.BLACK.getValueG(),
                Color.BLACK.getValueB());
    }

    default void fillWhite(PApplet p) {
        p.fill(Color.WHITE.getValueR(),
                Color.WHITE.getValueG(),
                Color.WHITE.getValueB());
    }

    default void fillGrey(PApplet p) {
        p.fill(Color.GREY.getValueR(),
                Color.GREY.getValueG(),
                Color.GREY.getValueB());
    }
    default  void fillGreen(PApplet p){
        p.fill(Color.GREEN.getValueR(),
                Color.GREEN.getValueG(),
                Color.GREEN.getValueB());
    }
    void display(PApplet p);
}
