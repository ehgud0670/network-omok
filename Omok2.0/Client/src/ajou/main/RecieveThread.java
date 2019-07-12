package ajou.main;

import com.google.gson.Gson;
import kr.ac.ajou.protocol.Protocol;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

public class RecieveThread extends Thread {


    private static final int MAX_SIZE = 1024;
    private Socket socket;
    private Window window;


    RecieveThread(Socket socket, Window window) {
        this.socket = socket;
        this.window = window;
    }

    @Override
    public void run() {
        try (InputStream is = socket.getInputStream();
             DataInputStream dis = new DataInputStream(is)) {

            byte[] buf = new byte[MAX_SIZE];
            int len;

            while (true) {

                try {
                    len = dis.readInt();
                } catch (EOFException e) {
                    System.out.println("[서버가 비정상 종료되었습니다.]");
                    window.exit();
                    break;
                }

                int ret = is.read(buf, 0, len);
                String json = new String(buf, 0, ret);

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
