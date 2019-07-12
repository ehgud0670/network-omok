package ajou.main;

import com.google.gson.Gson;
import kr.ac.ajou.protocol.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;


public class SessionThread extends Thread {

    private static final int ROOM_ON = 1;
    private static final int ROOM_OFF = -1;

    private static final int MAX_SIZE = 1024;
    private static final int CLIENT_ON = 1;
    private static final int CLIENT_OFF = -1;

    private Socket socket;
    private List<Socket> socketList;

    private HashMap<ClientNum, ClientInfo> clientInfoMap;
    private MainState mainState;
    private ClientInfo clientInfo;
    private ClientNum clientNum;
    private IdData idData;


    private ClientCount clientCount;
    private TreeSet<ClientNum> deletedClientNumTreeSet;

    private RoomCount roomCount;
    private RoomNum roomNum;
    private TreeSet<RoomNum> deletedRoomNumTreeSet;
    private HashMap<RoomNum, Room> roomHashMap;

    SessionThread(Socket socket, List<Socket> socketList,
                  ClientCount clientCount, TreeSet<ClientNum> deletedClientNumTreeSet,
                  HashMap<ClientNum, ClientInfo> clientInfoMap,
                  RoomCount roomCount, TreeSet<RoomNum> deletedRoomNumTreeSet,
                  HashMap<RoomNum, Room> roomHashMap) {
        //init state
        mainState = MainState.LOGIN_STATE;

        this.clientCount = clientCount;
        this.clientCount.plusClientCount(CLIENT_ON);
        System.out.println("clientCount: " + clientCount.getClientCount());

        this.deletedClientNumTreeSet = deletedClientNumTreeSet;

        this.roomCount = roomCount;
        this.deletedRoomNumTreeSet = deletedRoomNumTreeSet;

        this.roomHashMap = roomHashMap;

        this.socket = socket;
        this.socketList = socketList;
        socketList.add(socket);

        this.clientInfoMap = clientInfoMap;

        clientInfo = new ClientInfo();
        checkClientEnterDate();

        if (!deletedClientNumTreeSet.isEmpty()) {
            clientNum = deletedClientNumTreeSet.pollFirst();
        } else {
            clientNum = new ClientNum(this.clientCount
                    .getClientCount());
        }
        System.out.println("clientNum : " + Objects.
                requireNonNull(this.clientNum).getClientNum());

        sendClientNum();

    }



    private void checkClientEnterDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 " +
                "MM월dd일 " +
                "HH시mm분ss초 ");
        String formatTime = dateFormat.
                format(clientInfo.getEnterTime());
        System.out.println(formatTime);
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
                    close();

                    break;
                }
                int ret = is.read(buf, 0, len);
                String json = new String(buf, 0, ret);

                // 객체로 변환하기 (by Gson)
                Gson gson = new Gson();
                Protocol protocol = gson.fromJson(json, Protocol.class);

                String data = protocol.getData();
                String type = protocol.getType();

                switch (type) {
                    case ConstantProtocol.ID_DATA:

                        idData = gson.fromJson(data, IdData.class);

                        boolean idCheck = checkIdAlreadyExist(idData);

                        if (idCheck) {
                            System.out.println(idData.getId() + " is already exist!");
                            sendIdFail();
                        } else {

                            // note: id를 최종적으로 정하면 ClientInfoMap에
                            //  clientNum를 key로 clientInfo를 value로 넣는다.
                            setClientIdData(idData);
                            addClientInfoMap();

                            sendIdSuccess();

                            // note : MainState: LoginState -> LobbyState
                            setStateLobby();
                            sendMainState();
                        }
                        break;
                    case ConstantProtocol.ROOM_ON:
                        roomCount.plusRoomCount(ROOM_ON);
                        if (!deletedRoomNumTreeSet.isEmpty()) {
                            roomNum = deletedRoomNumTreeSet.pollFirst();
                        } else {
                            roomNum = new RoomNum(roomCount.getRoomCount());
                        }
                        System.out.println("roomNum: " + Objects.requireNonNull(roomNum).getRoomNum());

                        Room room = new Room(clientNum, clientInfo);
                        roomHashMap.put(roomNum, room);

                        setStateRoom();
                        sendMainState();

                        sendRoomNumOn(roomNum);
                        break;
                }

            }

        } catch (SocketException e) {
            close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void close() {
        System.out.println("[클라이언트가 비정상 종료되었습니다.]");

        if (roomNum != null) {
            sendRoomNumOff();
        }

        roomHashMap.remove(roomNum);
        if (roomNum != null) {
            deletedRoomNumTreeSet.add(roomNum);
        }
        roomCount.plusRoomCount(ROOM_OFF);

        deletedClientNumTreeSet.add(clientNum);
        System.out.println("deleted Client Num: " + clientNum.getClientNum());

        clientCount.plusClientCount(CLIENT_OFF);
        System.out.println("clientCount: " + clientCount.getClientCount());

        socketList.remove(socket);
        clientInfoMap.remove(clientNum);
    }

    private void setClientIdData(IdData idData) {
        clientInfo.setIdData(idData);
        System.out.println("ID: " +
                this.idData.getId());
    }


    private void addClientInfoMap() {

        clientInfoMap.put(clientNum, clientInfo);
    }


    private boolean checkIdAlreadyExist(IdData idData) {

        Set<Map.Entry<ClientNum, ClientInfo>> entrySet = clientInfoMap.entrySet();

        for (Map.Entry<ClientNum, ClientInfo> entry : entrySet) {

            IdData tempId = entry.
                    getValue().
                    getIdData();

            if (tempId.getId().equals(idData.getId())) {
                return true;
            }
        }
        return false;
    }

    private void sendClientNum() {
        Gson gson = new Gson();

        String data = gson.toJson(clientNum);
        String type = ConstantProtocol.CLIENT_NUM;

        sendToClient(data, type);
    }

    private void sendIdSuccess() {
        Gson gson = new Gson();

        String data = gson.toJson(idData);
        String type = ConstantProtocol.ID_SUCCESS;

        sendToClient(data, type);
    }

    private void sendIdFail() {
        Gson gson = new Gson();

        String data = gson.toJson(idData);
        String type = ConstantProtocol.ID_FAIL;

        sendToClient(data, type);
    }
    private void sendRoomNumList() {

        Set<RoomNum> roomNumSet = roomHashMap.keySet();

        for(RoomNum roomNum : roomNumSet){
            sendRoomNumOn(roomNum);
        }

    }
    private void sendRoomNumOn(RoomNum roomNum) {
        Gson gson = new Gson();

        String data = gson.toJson(roomNum);
        String type = ConstantProtocol.ROOM_NUM_ON;

        sendToOthers(data, type);
    }

    private void sendRoomNumOff() {
        Gson gson = new Gson();

        String data = gson.toJson(roomNum);
        String type = ConstantProtocol.ROOM_NUM_OFF;

        sendToOthers(data, type);
    }


    private void sendMainState() {

        Gson gson = new Gson();

        String data = gson.toJson(mainState);
        String type = ConstantProtocol.MAIN_STATE;

        sendToClient(data, type);
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

    private void sendToOthers(String data, String type) {
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

    private void setStateLogin() {
        mainState = MainState.LOGIN_STATE;
        System.out.println("state: " + mainState);
    }

    private void setStateLobby() {
        mainState = MainState.LOBBY_STATE;
        System.out.println("state: " + mainState);
    }

    private void setStateRoom() {
        mainState = MainState.ROOM_STATE;
        System.out.println("state: " + mainState);
    }
}
