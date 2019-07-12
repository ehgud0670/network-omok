package ajou.test;

import kr.ac.ajou.main.Window;
import processing.core.PApplet;

public class WindowExample {
    public static void main(String[] args) {
        try {
            PApplet.main(Window.class);
        } catch(NullPointerException e){
            e.printStackTrace();
        }
    }
}
