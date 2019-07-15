package kr.ac.ajou.main;

import com.google.gson.Gson;
import kr.ac.ajou.protocol.Protocol;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;


public class ReceiveThread extends Thread {

    private static final int MAX_SIZE = 1024;
    private static final int CLIENT_NUM_FIRST = 1;

    private Socket socket;
    private Window window;

    ReceiveThread(Socket socket, Window window) {
        this.socket = socket;
        this.window = window;
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
                    System.out.println("[서버가 비정상 종료되었습니다.]");
                    window.exit();
                    break;
                }
                int ret = is.read(buf, 0, len);
                if (ret == -1) {
                    System.out.println("ret error");
                    window.exit();
                    break;
                }
                
                String json = new String(buf, 0, ret);

                // Protocol
                Gson gson = new Gson();
                Protocol protocol = gson.fromJson(json, Protocol.class);
                window.protocolQueue.add(protocol);
            }
        } catch (SocketException e) {
            System.out.println("[서버가 비정상 종료되었습니다.]");
            window.exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
