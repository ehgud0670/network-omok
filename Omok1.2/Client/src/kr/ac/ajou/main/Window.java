package kr.ac.ajou.main;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
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
    private float diceDiameter;

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

    //Client
    private Socket socket;
    private ClientNum clientNum;
    private GameState gameState;
    private WinCheck winCheck;

    //queue
    // TODO : Queue 를 하나로 통일할 것. => 일급객체를 사용할 것.
    Queue<Protos.Protocol> protocolQueue = new ConcurrentLinkedQueue<>();


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
        diceDiameter = getDiecDiameter();

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
        return ConstantWindow.OMOK_EXTERNAL_X_VALUE + (ConstantWindow.OMOK_LENGTH >> 1);
    }

    private float getGameResultY() {
        return ConstantWindow.OMOK_EXTERNAL_Y_VALUE + (ConstantWindow.OMOK_LENGTH >> 1);
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
            Protos.Protocol protocol = protocolQueue.poll();
            ByteString data = protocol.getData();
            ConstantsProto.ConstantProtocol.Type type = null;
            try {
                type = ConstantsProto.ConstantProtocol.parseFrom(protocol.getType()).getType();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

            if (type == null) { return; }

            switch (type) {
                case READY:
                    try {
                        gameState = new GameState(Protos.GameState.parseFrom(data).getGameState());
                        if (gameState.getGameState() == GameState.WAITING) {
                            init();
                        }
                        System.out.println("gameState: " + gameState.getGameState());
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                    break;
                case CLIENT_NUM_MINE:
                    try {
                        clientNum = new ClientNum(Protos.ClientNum.parseFrom(data).getClientNum());
                        System.out.println("clientNum: " + clientNum.getClientNum());
                        setPlayersLabel();
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                    break;
                case CLIENT_NUM_OTHER:
                    if (clientNum.getClientNum() == ClientNum.ONE) {
                        playersInfo.setOpponentLabel(PlayersInfo.OPPONENT_LABEL);
                    }
                    break;
                case COUNT_DOWN_NUM:
                    try {
                        CountDownNum countDownNum = new CountDownNum(Protos.CountDownNum.parseFrom(data).getCountDownNum());
                        countDown.setCountDownNum(countDownNum);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                    break;
                case DICE:
                    try {
                        Protos.DiceNum diceNumForProto = Protos.DiceNum.parseFrom(data);
                        DiceNum diceNum = new DiceNum(
                                diceNumForProto.getMyDiceNum(),
                                diceNumForProto.getOpponentDiceNum(),
                                diceNumForProto.getMyTurn());
                        setDicesNum(diceNum);
                        makeDices();
                        myTurn = diceNum.getMyTurn();
                        setMyColor(myTurn);
                        setStoneViewsColor();
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                    break;
                case WIN:
                    try {
                        winCheck = new WinCheck(Protos.WinCheck.parseFrom(data).getWinCheck());
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                    break;
                case STONE_LOCATION:
                    try {
                        Protos.StoneLocation locationForProto = Protos.StoneLocation.parseFrom(data);
                        int row = locationForProto.getRow();
                        int col = locationForProto.getCol();
                        int stoneColor = locationForProto.getColor();

                        omokPlate.recordStone(row, col, stoneColor);
                        myTurn = myColor != stoneColor;

                        System.out.println("row:" + row + " col:" + col);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                    break;
                case EXIT:
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

    private void setStoneViewsColor() {
        if (myColor == ConstantProtocol.BLACK_STONE) {
            playersInfo.setMineStoneView(Color.BLACK.getValue());
            playersInfo.setOpponentStoneView(Color.WHITE.getValue());
        } else if (myColor == ConstantProtocol.WHITE_STONE) {
            playersInfo.setMineStoneView(Color.WHITE.getValue());
            playersInfo.setOpponentStoneView(Color.BLACK.getValue());
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

        playersInfo.stoneViewsInit();
    }

    private void setDicesNum(DiceNum diceNum) {
        myDiceNum = diceNum.getMyDiceNum();
        opponentDiceNum = diceNum.getOpponentDiceNum();
        System.out.println("myDiceNum: " + myDiceNum);
        System.out.println("oppoDiceNum: " + opponentDiceNum);
    }

    private void makeDices() {
        myDice = new Dice(myDiceX, myDiceY, diceDiameter, myDiceNum);
        myDice.setLabel("mine");
        opponentDice = new Dice(oppoDiceX, oppoDiceY, diceDiameter, opponentDiceNum);
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
        Protos.ReadyData.Builder readyBuilder = Protos.ReadyData.newBuilder().setReadyNum(ReadyData.READY);
        ConstantsProto.ConstantProtocol type = ConstantsProto.ConstantProtocol
                .newBuilder()
                .setType(ConstantsProto.ConstantProtocol.Type.READY)
                .build();

        sendToServer(readyBuilder.build().toByteString(), type.toByteString());
    }

    private void sendNotReady() {
        Protos.ReadyData.Builder readyBuilder = Protos.ReadyData.newBuilder().setReadyNum(ReadyData.NOT_READY);
        ConstantsProto.ConstantProtocol type = ConstantsProto.ConstantProtocol
                .newBuilder()
                .setType(ConstantsProto.ConstantProtocol.Type.NOT_READY)
                .build();

        sendToServer(readyBuilder.build().toByteString(), type.toByteString());
    }

    private void sendLocation(int row, int col) {
        Protos.StoneLocation location = Protos.StoneLocation.newBuilder()
                .setRow(row)
                .setCol(col)
                .setColor(myColor)
                .build();
        ConstantsProto.ConstantProtocol type = ConstantsProto.ConstantProtocol
                .newBuilder()
                .setType(ConstantsProto.ConstantProtocol.Type.STONE_LOCATION)
                .build();

        sendToServer(location.toByteString(), type.toByteString());

        System.out.println("row:" + row);
        System.out.println("col:" + col);
    }

    private void sendInitStone() {
        ConstantsProto.ConstantProtocol data = ConstantsProto.ConstantProtocol
                .newBuilder()
                .setType(ConstantsProto.ConstantProtocol.Type.READY)
                .build();
        ConstantsProto.ConstantProtocol type = ConstantsProto.ConstantProtocol
                .newBuilder()
                .setType(ConstantsProto.ConstantProtocol.Type.INIT_STONE)
                .build();

        sendToServer(data.toByteString(), type.toByteString());
    }

    private void sendToServer(ByteString data, ByteString type) {
        try {
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            Protos.Protocol protocol = Protos.Protocol
                    .newBuilder()
                    .setData(data)
                    .setType(type)
                    .build();

            byte[] bytes = protocol.toByteArray();
            int len = bytes.length;
            dos.writeInt(len);
            os.write(bytes, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
