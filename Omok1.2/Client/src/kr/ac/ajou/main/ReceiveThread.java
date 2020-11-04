package kr.ac.ajou.main;

import kr.ac.ajou.protocol.Protos;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

public class ReceiveThread extends Thread {
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
            while (true) {
                int len;
                try {
                    len = dis.readInt();
                } catch (EOFException e) {
                    System.out.println("[서버가 비정상 종료되었습니다.]");
                    window.exit();
                    break;
                }
                byte[] buf = new byte[len];
                int ret = is.read(buf, 0, len);
                if (ret == -1) {
                    System.out.println("ret error");
                    window.exit();
                    break;
                }
                // Protocol
                Protos.Protocol protocol = Protos.Protocol.parseFrom(buf);
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
