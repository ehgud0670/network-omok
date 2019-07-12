package ajou.view;

import processing.core.PApplet;

// todo: 문자열 입력 StringBuilder 사용하기
public class LoginBox implements Displayable {


    private static final String TITLE = "Network Omok";

    private static final int TITLE_SIZE = 60;
    private static final int ID_TITLE_SIZE = 30;
    private static final int ID_TEXT_SIZE = 20;

    private final float rectX;
    private final float rectY;
    private final float width;
    private final float height;


    private LoginButton loginButton;
    private StringBuilder id;

    public LoginBox(float rectX, float rectY, float width, float height) {
        this.rectX = rectX;
        this.rectY = rectY;
        this.width = width;
        this.height = height;

        loginButton = new LoginButton(width / 3, height / 2 + 80, 200, 60);
        id = new StringBuilder();
    }


    @Override
    public void display(PApplet p) {

        drawFrame(p);
        drawTitle(p);
        drawIdTitle(p);
        drawIdField(p);
        drawIdText(p);

        loginButton.display(p);
    }

    private void drawFrame(PApplet p) {
        setStrokeGrey(p);
        fillGrey(p);
        p.rect(rectX, rectY, width, height);
    }

    private void drawTitle(PApplet p) {
        fillBlack(p);
        p.textAlign(PApplet.CENTER);
        p.textSize(TITLE_SIZE);
        p.text(TITLE, width / 2, height / 4);
    }

    private void drawIdTitle(PApplet p) {
        fillBlack(p);
        p.textAlign(PApplet.LEFT);
        p.textSize(ID_TITLE_SIZE);
        p.text("id", width / 3, height / 2);
    }

    private void drawIdField(PApplet p) {
        setStrokeWhite(p);
        fillWhite(p);
        p.rect(width / 3 + 40, height / 2 - 25, 160, 30);
    }

    private void drawIdText(PApplet p) {
        fillBlack(p);
        p.textAlign(PApplet.LEFT);
        p.textSize(ID_TEXT_SIZE);
        p.text(id.toString(), width / 3 + 50, height / 2);
    }

    public void plusIdText(char key) {
        id.append(key);
    }

    public void backSpaceIdText() {
        try {
            id.delete(id.length() - 1, id.length());
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("경고! 범위를 넘었습니다: String index out of range: -1");
        }
    }

    public LoginButton getLoginButton() {
        return loginButton;
    }

    public StringBuilder getId() {
        return id;
    }
}
