package kr.ac.ajou.main;

import com.google.gson.Gson;
import kr.ac.ajou.Dice;
import kr.ac.ajou.protocol.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class CheckThread extends Thread {

    private static final int FIRST_CLIENT = 0;
    private static final int SECOND_CLIENT = 1;
    private static final int MAX_CLINET_NUM = 2;

    private static final int INIT_COUNT = 0;

    private static int clientCount;

    private ReadyData readyCount;
    private GameState gameState;
    private List<SessionThread> sessionThreadList;

    private List<Socket> socketList;
    //queue
    Queue<Protocol> protocolQueue = new ConcurrentLinkedQueue<>();


    CheckThread(List<Socket> socketList, List<SessionThread> sessionThreadList) {
        gameState = new GameState(GameState.WAITING);
        System.out.println("gameState: WAITING");
        this.socketList = socketList;
        this.sessionThreadList = sessionThreadList;
        readyCount = new ReadyData(0);
    }

    @Override
    public void run() {

        while (true) {


            Gson gson = new Gson();
            if (!protocolQueue.isEmpty()) {
                Protocol protocol = protocolQueue.poll();
                String data = protocol.getData();
                String type = protocol.getType();

                switch (type) {
                    case ConstantProtocol.READY:
                    case ConstantProtocol.NOT_READY:
                        ReadyData readyData = gson.fromJson(data, ReadyData.class);
                        readyCount.plusReadyNum(readyData.getReadyNum());
                        System.out.println("readyCount: " + readyCount.getReadyNum());
                        break;
                    case ConstantProtocol.GAME_STATE:
                        gameState = gson.fromJson(data, GameState.class);
                        System.out.println("gameState: " + gameState.getGameState());
                        break;
                }

            }

            if (gameState.getGameState() == GameState.WAITING && readyCount.getReadyNum() == 2) {
                setGameStateSetOrder();
                sendGameState();
            }

            if (gameState.getGameState() == GameState.SET_ORDER) {
                sendCountDown();
            }
            if (gameState.getGameState() == GameState.SET_ORDER) {
                sendDiceNum();
            }
            if (gameState.getGameState() == GameState.SET_ORDER) {
                setGameStateRunning();
                sendGameState();
            }

            if (gameState.getGameState() == GameState.RUNNING) {
                if (socketList.size() < 2) {
                    System.out.println("[게임 도중 클라이언트가 나갔습니다. WAITING 상태로 변경합니다.]");
                    init();
                    setGameStateWaiting();
                    sendGameState();
                }
            }
            if (gameState.getGameState() == GameState.GAME_OVER) {
                init();
                sendGameState();
                try {
                    Thread.sleep(3000);
                    setGameStateWaiting();
                    sendGameState();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }


    private void init() {
        readyCount.setReadyNum(INIT_COUNT);
        while (!protocolQueue.isEmpty()) {
            protocolQueue.remove();
        }
    }

    private void sendCountDown() {

        CountDownNum countDownNum;
        Gson gson = new Gson();
        String data;
        String type;
        for (int i = 3; i >= 0; i--) {
            if (socketList.size() == MAX_CLINET_NUM) {
                countDownNum = new CountDownNum(i);
                data = gson.toJson(countDownNum);
                type = ConstantProtocol.COUNT_DOWN_NUM;
                broadcast(data, type);
                if (i != 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("[카운트다운 도중 클라이언트가 나갔습니다. WAITING 상태로 변경합니다.]");
                init();
                setGameStateWaiting();
                sendGameState();
                break;
            }
        }

    }

    private void sendDiceNum() {
        while (true) {
            int diceNum1 = Dice.getDice();
            int diceNum2 = Dice.getDice();

            if (diceNum1 != diceNum2) {
                System.out.println("diceNum1:" + diceNum1);
                System.out.println("diceNum2:" + diceNum2);
                try {
                    //client1
                    DiceNum clientOneDiceNumInfo;
                    boolean clientOneTurn;

                    clientOneTurn = diceNum1 > diceNum2;
                    if (clientOneTurn) {
                        sessionThreadList.get(FIRST_CLIENT).myTurnQueue.add(true);
                    }
                    clientOneDiceNumInfo = new DiceNum(diceNum1, diceNum2, clientOneTurn);

                    Gson gson = new Gson();
                    String data = gson.toJson(clientOneDiceNumInfo);
                    String type = ConstantProtocol.DICE;

                    OutputStream os = socketList.get(FIRST_CLIENT).getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);

                    Protocol protocol = new Protocol(data, type);

                    String json = gson.toJson(protocol);

                    int len = json.length();
                    dos.writeInt(len);
                    os.write(json.getBytes());

                    //client2
                    DiceNum clientTwoDiceNumInfo;
                    boolean clientTwoTurn;
                    clientTwoTurn = diceNum2 > diceNum1;
                    if (clientTwoTurn) {
                        sessionThreadList.get(SECOND_CLIENT).myTurnQueue.add(true);
                    }
                    clientTwoDiceNumInfo = new DiceNum(diceNum2, diceNum1, clientTwoTurn);

                    data = gson.toJson(clientTwoDiceNumInfo);
                    type = ConstantProtocol.DICE;

                    os = socketList.get(SECOND_CLIENT).getOutputStream();
                    dos = new DataOutputStream(os);

                    protocol = new Protocol(data, type);

                    json = gson.toJson(protocol);

                    len = json.length();
                    dos.writeInt(len);
                    os.write(json.getBytes());

                    Thread.sleep(2000);
                    if (socketList.size() < 2) {
                        System.out.println("[주사위 출력 중 클라이언트가 나갔습니다. WAITING 상태로 변경합니다.]");
                        init();
                        setGameStateWaiting();
                        sendGameState();
                    }
                    break;
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("[카운트다운 도중 클라이언트가 나갔습니다. WAITING 상태로 변경합니다.]");
                    init();
                    setGameStateWaiting();
                    sendGameState();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private void sendGameState() {

        Gson gson = new Gson();
        String data = gson.toJson(gameState);
        String type = ConstantProtocol.GAME_STATE;
        broadcast(data, type);

    }

    private void broadcast(String data, String type) {
        try {
            for (Socket socket : socketList) {
                OutputStream os = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                Gson gson = new Gson();

                Protocol protocol = new Protocol(data, type);
                String json = gson.toJson(protocol);
                int len = json.length();

                dos.writeInt(len);
                os.write(json.getBytes());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setGameStateWaiting() {
        gameState = new GameState(GameState.WAITING);
        System.out.println("gameState: WAITING");
    }

    private void setGameStateSetOrder() {
        gameState = new GameState(GameState.SET_ORDER);
        System.out.println("gameState: SET_ORDER");
    }

    private void setGameStateRunning() {
        gameState = new GameState(GameState.RUNNING);
        System.out.println("gameState: RUNNING");
    }

    static int getClientCount() {
        return clientCount;
    }

    static int clientConnected() {
        return ++clientCount;
    }

    static void clientDisconnected() {
        --clientCount;
    }
}
