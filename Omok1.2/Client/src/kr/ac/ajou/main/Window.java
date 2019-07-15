package kr.ac.ajou.main;

import com.google.gson.Gson;
import kr.ac.ajou.protocol.*;
import kr.ac.ajou.view.*;
import processing.core.PApplet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Window extends PApplet {

    private int omokEdge;
    private int omokBlock;
    private int omokBlockHalf;

    private float myDiceX;
    private float myDiceY;
    private float oppoDiceX;
    private float oppoDiceY;
    private float diecDiameter;

    private ClientOmokPlate omokPlate;
    private Button readyButton;
    private Button exitButton;

    private PlayersInfo playersInfo;

    private CountDown countDown;

    private GameResult gameResult;
    private float gameResultX;
    private float gameResultY;

    private Dice myDice;
    private Dice opponentDice;
    private int myDiceNum;
    private int opponentDiceNum;


    private boolean myTurn;

    private int myColor;
    private int mouseValue;

    private boolean countDownFlag;
    private boolean resultMessageFlag;

    //client
    private Socket socket;
    private ClientNum clientNum;
    private GameState gameState;
    private WinCheck winCheck;

    //queue
    // TODO : Queue 를 하나로 통일할 것. => 일급객체를 사용할 것.
    Queue<Protocol> protocolQueue = new ConcurrentLinkedQueue<>();


    @Override
    public void settings() {

        connect();

        size(ConstantWindow.WIDTH, ConstantWindow.HEIGHT);

        omokPlate = new ClientOmokPlate(ConstantWindow.OMOK_LENGTH,
                ConstantWindow.OMOK_EXTERNAL_X_VALUE,
                ConstantWindow.OMOK_EXTERNAL_Y_VALUE);

        readyButton = new Button(ConstantWindow.READY_BUTTON_X,
                ConstantWindow.READY_BUTTON_Y,
                ConstantWindow.BUTTON_DIAMETER,
                ConstantWindow.BUTTON_DIAMETER);
        readyButton.setLabel(ConstantWindow.READY_BUTTON_LABEL);
        readyButton.setColor(Color.DARK_GREY.getValue());

        exitButton = new Button(ConstantWindow.EXIT_BUTTON_X,
                ConstantWindow.EXIT_BUTTON_Y,
                ConstantWindow.BUTTON_DIAMETER,
                ConstantWindow.BUTTON_DIAMETER);
        exitButton.setLabel(ConstantWindow.EXIT_BUTTON_LABEL);
        exitButton.setColor(Color.DARK_GREY.getValue());

        playersInfo = new PlayersInfo(ConstantWindow.PLAYERS_INFO_X,
                ConstantWindow.PLAYERS_INFO_Y,
                ConstantWindow.PLAYERS_INFO_WIDTH,
                ConstantWindow.PLAYERS_INFO_HEIGHT);


        omokEdge = getEdge();
        omokBlock = getBlock();
        omokBlockHalf = omokBlock / 2;

        myDiceX = getMyDiceX();
        myDiceY = getMyDiceY();
        oppoDiceX = getOppoDiceX();
        oppoDiceY = getOppoDiceY();
        diecDiameter = getDiecDiameter();


        gameResultX = getGameResultX();
        gameResultY = getGameResultY();

        myTurn = false;
        winCheck = new WinCheck(false);

        myColor = ConstantProtocol.BLACK_STONE;
        gameState = new GameState(GameState.WAITING);
        System.out.println("gameState: WAITING");
    }

    private int getEdge() {
        return ConstantWindow.OMOK_LENGTH / 30;
    }

    private int getBlock() {
        return (ConstantWindow.OMOK_LENGTH - 2 * omokEdge) / (ConstantWindow.OMOK_NUM - 1);
    }

    private float getMyDiceX() {
        return (ConstantWindow.OMOK_EXTERNAL_X_VALUE + omokEdge + 2 * omokBlock + omokBlockHalf);
    }

    private float getMyDiceY() {
        return ConstantWindow.OMOK_EXTERNAL_Y_VALUE + omokEdge + 5 * omokBlock;
    }

    private float getOppoDiceX() {
        return ConstantWindow.OMOK_EXTERNAL_X_VALUE + omokEdge + 8 * omokBlock + omokBlockHalf;
    }

    private float getOppoDiceY() {
        return ConstantWindow.OMOK_EXTERNAL_Y_VALUE + omokEdge + 5 * omokBlock;
    }

    private float getDiecDiameter() {
        return omokBlock * 3;
    }

    private float getGameResultX() {
        return ConstantWindow.OMOK_EXTERNAL_X_VALUE + ConstantWindow.OMOK_LENGTH / 2;
    }

    private float getGameResultY() {
        return ConstantWindow.OMOK_EXTERNAL_Y_VALUE + ConstantWindow.OMOK_LENGTH / 2;
    }

    @Override
    public void draw() {

        this.background(Color.GREY.getValue());
        readyButton.display(this);
        exitButton.display(this);
        omokPlate.display(this);
        playersInfo.display(this);

        checkMouseCursor();

        if (!protocolQueue.isEmpty()) {
            Protocol protocol = protocolQueue.poll();
            String data = protocol.getData();
            String type = protocol.getType();

            Gson gson = new Gson();

            switch (type) {
                case ConstantProtocol.GAME_STATE:
                    gameState = gson.fromJson(data, GameState.class);

                    if (gameState.getGameState() == GameState.WAITING) {
                        init();
                    }

                    System.out.println("gameState: " + gameState.getGameState());
                    break;
                case ConstantProtocol.CLIENT_NUM_MINE:
                    clientNum = gson.fromJson(data, ClientNum.class);
                    System.out.println("clientNum: " + clientNum.getClientNum());
                    setPlayersLabel();
                    break;
                case ConstantProtocol.CLIENT_NUM_OTHER:
                    if (clientNum.getClientNum() == ClientNum.ONE) {
                        playersInfo.setOpponentLabel(PlayersInfo.OPPONENT_LABEL);
                    }
                    break;
                case ConstantProtocol.COUNT_DOWN_NUM:
                    CountDownNum countDownNum = gson.fromJson(data, CountDownNum.class);
                    countDown.setCountDownNum(countDownNum);
                    break;
                case ConstantProtocol.DICE:
                    DiceNum diceNum = gson.fromJson(data, DiceNum.class);
                    setDicesNum(diceNum);
                    makeDices();
                    myTurn = diceNum.getMyTurn();
                    setMyColor(myTurn);
                    break;
                case ConstantProtocol.WIN:
                    winCheck = gson.fromJson(data, WinCheck.class);
                    break;
                case ConstantProtocol.STONE_LOCATION:
                    StoneLocation location = gson.fromJson(data, StoneLocation.class);
                    int row = location.getRow();
                    int col = location.getCol();
                    int stoneColor = location.getColor();

                    omokPlate.recordStone(row, col, stoneColor);
                    myTurn = myColor != stoneColor;

                    System.out.println("row:" + row + " col:" + col);

                    break;
                case ConstantProtocol.EXIT:
                    if (clientNum.getClientNum() == ClientNum.TWO) {
                        clientNum.setClientNum(ClientNum.ONE);
                        System.out.println("clientNum: " + clientNum.getClientNum());
                    }
                    initOpponentLabel();
                    break;
            }

        }


        switch (gameState.getGameState()) {
            case GameState.WAITING:
            case GameState.RUNNING:
                break;
            case GameState.SET_ORDER:
                if (!countDownFlag) {
                    countDownFlag = true;
                    countDown = new CountDown(ConstantWindow.OMOK_LENGTH,
                            ConstantWindow.OMOK_EXTERNAL_X_VALUE,
                            ConstantWindow.OMOK_EXTERNAL_Y_VALUE);
                }
                if (countDown != null) {
                    countDown.display(this);
                }

                if (myDice != null) {
                    myDice.display(this);
                }
                if (opponentDice != null) {
                    opponentDice.display(this);
                }
                break;
            case GameState.GAME_OVER:
                sendInitStone();
                if (!resultMessageFlag) {
                    resultMessageFlag = true;
                    String message = winCheck.getWinCheck() ? "WIN" : "LOSE";
                    gameResult = new GameResult(gameResultX, gameResultY, message);
                }
                if (gameResult != null) {
                    gameResult.display(this);
                }
                break;
        }


    }

    private void init() {
        myTurn = false;
        winCheck = new WinCheck(false);
        countDownFlag = false;
        resultMessageFlag = false;
        omokPlate.initStones();
        while (!protocolQueue.isEmpty()) {
            protocolQueue.remove();
        }
        if (myDice != null) {
            myDice = makeDiceDisable();
        }
        if (opponentDice != null) {
            opponentDice = makeDiceDisable();
        }
        makeReadyButtonGrey();
    }

    private void setDicesNum(DiceNum diceNum) {
        myDiceNum = diceNum.getMyDiceNum();
        opponentDiceNum = diceNum.getOpponentDiceNum();
        System.out.println("myDiceNum: " + myDiceNum);
        System.out.println("oppoDiceNum: " + opponentDiceNum);
    }

    private void makeDices() {
        myDice = new Dice(myDiceX, myDiceY, diecDiameter, myDiceNum);
        myDice.setLabel("mine");
        opponentDice = new Dice(oppoDiceX, oppoDiceY, diecDiameter, opponentDiceNum);
        opponentDice.setLabel("opponent");
    }

    private void setMyColor(boolean myTurn) {
        myColor = myTurn ? ConstantProtocol.BLACK_STONE : ConstantProtocol.WHITE_STONE;
    }

    private void checkMouseCursor() {

        switch (gameState.getGameState()) {
            case GameState.WAITING:
                if (readyButton.overRect(mouseX, mouseY) ||
                        exitButton.overRect(mouseX, mouseY)) {
                    cursor(HAND);
                    mouseValue = HAND;
                } else {
                    cursor(ARROW);
                    mouseValue = ARROW;
                }
                break;
            case GameState.SET_ORDER:
            case GameState.GAME_OVER:
                cursor(ARROW);
                mouseValue = ARROW;
                break;
            case GameState.RUNNING:
                if (omokPlate.overVertex(mouseX, mouseY) && myTurn) {
                    cursor(HAND);
                    mouseValue = HAND;
                } else {
                    cursor(ARROW);
                    mouseValue = ARROW;
                }
                break;
        }

    }

    private void setPlayersLabel() {
        playersInfo.setClientNum(clientNum);
        playersInfo.setMineLabel(PlayersInfo.MINE_LABEL);
        if (clientNum.getClientNum() == ClientNum.TWO) {
            playersInfo.setOpponentLabel(PlayersInfo.OPPONENT_LABEL);
        }
    }

    private void initOpponentLabel() {
        playersInfo.setOpponentLabel(PlayersInfo.NONE_LABEL);
    }

    @Override
    public void mousePressed() {
        if (mouseButton == LEFT && mouseValue == HAND) {
            if (readyButton.overRect(mouseX, mouseY)) {
                if (readyButton.getColor() == Color.DARK_GREY.getValue()) {
                    sendReady();
                    makeReadyButtonBlack();
                } else {
                    sendNotReady();
                    makeReadyButtonGrey();
                }
            } else if (exitButton.overRect(mouseX, mouseY)) {
                exit();
            } else {

                int currentX = mouseX - ConstantWindow.OMOK_EXTERNAL_X_VALUE;
                int currentY = mouseY - ConstantWindow.OMOK_EXTERNAL_Y_VALUE;

                int fixedX = omokPlate.editPosition(currentX);
                int fixedY = omokPlate.editPosition(currentY);

                int row = omokPlate.getIndex(fixedY);
                int col = omokPlate.getIndex(fixedX);

                sendLocation(row, col);
                myTurn = false;

            }
        }
    }

    private void makeReadyButtonGrey() {
        readyButton = new Button(ConstantWindow.READY_BUTTON_X,
                ConstantWindow.READY_BUTTON_Y,
                ConstantWindow.BUTTON_DIAMETER,
                ConstantWindow.BUTTON_DIAMETER);
        readyButton.setLabel(ConstantWindow.READY_BUTTON_LABEL);
        readyButton.setColor(Color.DARK_GREY.getValue());
    }

    private void makeReadyButtonBlack() {
        readyButton = new Button(ConstantWindow.READY_BUTTON_X,
                ConstantWindow.READY_BUTTON_Y,
                ConstantWindow.BUTTON_DIAMETER,
                ConstantWindow.BUTTON_DIAMETER);
        readyButton.setLabel(ConstantWindow.READY_BUTTON_LABEL);
        readyButton.setColor(Color.BLACK.getValue());
    }

    private Dice makeDiceDisable() {
        return new Dice(ConstantWindow.NONE,
                ConstantWindow.NONE,
                ConstantWindow.MIN,
                ConstantWindow.NONE);
    }

    //client
    private void connect() {
        try {

            socket = new Socket();
            socket.connect(new InetSocketAddress("127.0.0.1", 5500));
            System.out.println("[서버 연결 성공]");
            ReceiveThread receiveThread = new ReceiveThread(socket, this);
            receiveThread.start();

        } catch (IOException e) {
            System.out.println("[서버 연결 안됨]");
            exit();
        }
    }

    private void sendReady() {

        ReadyData ready = new ReadyData(ReadyData.READY);
        Gson gson = new Gson();

        String data = gson.toJson(ready);
        String type = ConstantProtocol.READY;

        sendToServer(data, type);

    }

    private void sendNotReady() {

        ReadyData notReady = new ReadyData(ReadyData.NOT_READY);
        Gson gson = new Gson();

        String data = gson.toJson(notReady);
        String type = ConstantProtocol.NOT_READY;
        sendToServer(data, type);

    }

    private void sendLocation(int row, int col) {

        StoneLocation location = new StoneLocation(row, col, myColor);
        Gson gson = new Gson();

        String data = gson.toJson(location);
        String type = ConstantProtocol.STONE_LOCATION;

        sendToServer(data, type);
        System.out.println("row:" + row);
        System.out.println("col:" + col);

    }

    private void sendInitStone() {

        String data = "";
        String type = ConstantProtocol.INIT_STONE;

        sendToServer(data, type);
    }

    private void sendToServer(String data, String type) {

        try {
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            Gson gson = new Gson();

            Protocol protocol = new Protocol(data, type);
            String json = gson.toJson(protocol);

            int len = json.length();

            dos.writeInt(len);
            os.write(json.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
