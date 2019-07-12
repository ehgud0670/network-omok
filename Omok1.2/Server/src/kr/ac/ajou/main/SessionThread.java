package kr.ac.ajou.main;

import com.google.gson.Gson;
import kr.ac.ajou.protocol.*;
import kr.ac.ajou.strategy.ServerOmokPlate;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class SessionThread extends Thread {
    private static final int MAX_SIZE = 1024;
    private static final int CLIENT_ON = 1;
    private static final int CLIENT_OFF = -1;

    private boolean myTurn;
    Queue<Boolean> myTurnQueue = new ConcurrentLinkedQueue<>();

    private int clientNum;

    private Socket socket;
    private List<Socket> socketList;
    private List<SessionThread> sessionThreadList;
    private ServerOmokPlate omokPlate;
    private CheckThread checkThread;


    SessionThread(Socket socket, List<Socket> socketList, List<SessionThread> sessionThreadList, CheckThread checkThread) {
        this.socket = socket;

        this.socketList = socketList;
        socketList.add(socket);

        this.sessionThreadList = sessionThreadList;
        sessionThreadList.add(this);

        this.checkThread = checkThread;

        omokPlate = new ServerOmokPlate();

        clientNum = CheckThread.clientConnected();
        System.out.println("clientNum: " + clientNum);
        System.out.println("clientCount: " + CheckThread.getClientCount());

        sendClientNum(); // send clientNum to the Client

    }

    @Override
    public void run() {
        try (InputStream is = socket.getInputStream();
             DataInputStream dis = new DataInputStream(is)) {

            byte[] buf = new byte[MAX_SIZE];
            while (true) {
                int len;
                try {
                    len = dis.readInt();
                } catch (EOFException e) {
                    System.out.println("[클라이언트가 비정상 종료되었습니다.]");
                    sendExit();
                    socketList.remove(socket);
                    sessionThreadList.remove(this);
                    System.out.println("sessionThreadList's size:" + sessionThreadList.size());

                    CheckThread.clientDisconnected();
                    System.out.println("clientCount: " + CheckThread.getClientCount());
                    break;
                }
                int ret = is.read(buf, 0, len);
                String json = new String(buf, 0, ret);

                // 객체로 변환하기
                Gson gson = new Gson();
                Protocol protocol = gson.fromJson(json, Protocol.class);

                String data = protocol.getData();
                String type = protocol.getType();

                switch (type) {
                    case ConstantProtocol.READY:
                    case ConstantProtocol.NOT_READY:
                        checkThread.protocolQueue.add(protocol);
                        break;
                    case ConstantProtocol.STONE_LOCATION:

                        if (!myTurnQueue.isEmpty()) {
                            myTurn = myTurnQueue.poll();
                        }

                        if (myTurn) {
                            Location location = gson.fromJson(data, Location.class);
                            int row = location.getRow();
                            int col = location.getCol();
                            int stoneColor = location.getColor();

                            omokPlate.putStone(row, col, stoneColor);

                            if (omokPlate.winCheck(row, col)) {
                                broadcast(data, type);
                                sendWinInfo();
                                Thread.sleep(100);

                                GameState gameState = new GameState(GameState.GAME_OVER);
                                String gameStateData = gson.toJson(gameState);
                                String gameStateType = ConstantProtocol.GAME_STATE;
                                protocol = new Protocol(gameStateData, gameStateType);

                                checkThread.protocolQueue.add(protocol);

                            } else {
                                broadcast(data, type);
                            }
                            myTurn = false;
                            sendToOtherTurn();
                        }
                        break;
                    case ConstantProtocol.INIT_STONE:
                        omokPlate.initStones();
                }

            }
        } catch (SocketException e) {
            System.out.println("[클라이언트가 비정상 종료되었습니다.]");
            sendExit();

            socketList.remove(socket);
            sessionThreadList.remove(this);

            CheckThread.clientDisconnected();
            System.out.println("clientCount: " + CheckThread.getClientCount());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Network
    private void sendWinInfo() {
        Gson gson = new Gson();
        WinCheck winCheck = new WinCheck(true);

        String data = gson.toJson(winCheck);
        String type = ConstantProtocol.WIN;

        sendToClient(data, type);
    }


    private void sendClientNum() {

        Gson gson = new Gson();
        ClientNum clientNo = new ClientNum(clientNum);

        String data = gson.toJson(clientNo);
        String type = ConstantProtocol.CLIENT_NUM_MINE;

        sendToClient(data, type);

        type = ConstantProtocol.CLIENT_NUM_OTHER;
        sendToOther(data, type);

    }

    private void sendExit() {

        String data = "";
        String type = ConstantProtocol.EXIT;

        sendToOther(data, type);

    }


    private void sendToClient(String data, String type) {
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

    private void sendToOther(String data, String type) {
        for (Socket socket : socketList) {
            if (socket == this.socket) {
                continue;
            }

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


    private void sendToOtherTurn() {

        for (SessionThread sessionThread : sessionThreadList) {
            if (sessionThread != this) {
                sessionThread.myTurnQueue.add(true);
            }
        }

    }
}
