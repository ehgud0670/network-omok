package ajou.main;

import kr.ac.ajou.protocol.ClientInfo;
import kr.ac.ajou.protocol.ClientNum;
import kr.ac.ajou.protocol.RoomNum;
import kr.ac.ajou.protocol.RoomNumAscendingComparator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

;

public class Server {

    private static final String HOST_NAME = "127.0.0.1";
    private static final int PORT_NUM = 5550;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(HOST_NAME, PORT_NUM));

            List<Socket> socketList = new ArrayList<>();

            ClientCount clientCount = new ClientCount();
            TreeSet<ClientNum> deletedClientNumTreeSet = new TreeSet<>(new ClientNumAscendingComparator());
            HashMap<ClientNum, ClientInfo> clientInfoMap = new HashMap<>();

            RoomCount roomCount = new RoomCount();
            TreeSet<RoomNum> deletedRoomNumTreeSet = new TreeSet<>(new RoomNumAscendingComparator());
            HashMap<RoomNum,Room> roomHashMap = new HashMap<>();

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println();
                System.out.println("[클라이언트 접속]");
                SessionThread sessionThread = new SessionThread(socket, socketList,
                        clientCount, deletedClientNumTreeSet, clientInfoMap,
                        roomCount, deletedRoomNumTreeSet,roomHashMap);
                sessionThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
