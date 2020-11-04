package kr.ac.ajou.main;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import kr.ac.ajou.protocol.*;
import kr.ac.ajou.strategy.ServerOmokPlate;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SessionThread extends Thread {
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
                byte[] buf = new byte[len];
                int ret = is.read(buf, 0, len);

                // 객체로 변환하기
                Protos.Protocol protocol = Protos.Protocol.parseFrom(buf);
                ByteString data = protocol.getData();
                ByteString type = protocol.getType();

                ConstantsProto.ConstantProtocol.Type typeValue = null;
                try {
                    typeValue = ConstantsProto.ConstantProtocol.parseFrom(type).getType();

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }

                if (typeValue == null) { return; }

                switch (typeValue) {
                    case READY:
                    case NOT_READY:
                        checkThread.protocolQueue.add(protocol);
                        break;
                    case STONE_LOCATION:
                        if (!myTurnQueue.isEmpty()) {
                            myTurn = myTurnQueue.poll();
                        }

                        if (myTurn) {
                            Protos.StoneLocation location = Protos.StoneLocation.parseFrom(data);
                            int row = location.getRow();
                            int col = location.getCol();
                            int stoneColor = location.getColor();

                            omokPlate.putStone(row, col, stoneColor);

                            if (omokPlate.winCheck(row, col)) {
                                broadcast(data, type);
                                sendWinInfo();
                                Thread.sleep(100);

                                Protos.GameState gameState = Protos.GameState.newBuilder().setGameState(GameState.GAME_OVER).build();
                                ByteString gameStateData = gameState.toByteString();
                                ByteString gameStateType = ConstantsProto.ConstantProtocol.newBuilder()
                                        .setType(ConstantsProto.ConstantProtocol.Type.GAME_STATE).build().toByteString();

                                protocol = Protos.Protocol.newBuilder()
                                        .setData(gameStateData)
                                        .setType(gameStateType)
                                        .build();

                                checkThread.protocolQueue.add(protocol);

                            } else {
                                broadcast(data, type);
                            }
                            myTurn = false;
                            sendToOtherTurn();
                        }
                        break;
                    case INIT_STONE:
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
        Protos.WinCheck winCheck = Protos.WinCheck.newBuilder().setWinCheck(true).build();

        ByteString data = winCheck.toByteString();
        ByteString type = ConstantsProto.ConstantProtocol.newBuilder()
                .setType(ConstantsProto.ConstantProtocol.Type.WIN)
                .build()
                .toByteString();

        sendToClient(data, type);
    }

    private void sendClientNum() {
        Protos.ClientNum clientNo = Protos.ClientNum.newBuilder()
                .setClientNum(clientNum)
                .build();

        ByteString data = clientNo.toByteString();
        ByteString type = ConstantsProto.ConstantProtocol.newBuilder()
                .setType(ConstantsProto.ConstantProtocol.Type.CLIENT_NUM_MINE)
                .build()
                .toByteString();

        sendToClient(data, type);
        type = ConstantsProto.ConstantProtocol.newBuilder()
                .setType(ConstantsProto.ConstantProtocol.Type.CLIENT_NUM_OTHER)
                .build()
                .toByteString();
        sendToOther(data, type);
    }

    private void sendExit() {
        ByteString data = ConstantsProto.ConstantProtocol.newBuilder()
                .setType(ConstantsProto.ConstantProtocol.Type.READY)
                .build()
                .toByteString();
        ByteString type = ConstantsProto.ConstantProtocol.newBuilder()
                .setType(ConstantsProto.ConstantProtocol.Type.EXIT)
                .build()
                .toByteString();

        sendToOther(data, type);
    }

    private void sendToClient(ByteString data, ByteString type) {
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

    private void sendToOther(ByteString data, ByteString type) {
        for (Socket socket : socketList) {
            if (socket == this.socket) {
                continue;
            }

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

    private void broadcast(ByteString data, ByteString type) {
        try {
            for (Socket socket : socketList) {
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
