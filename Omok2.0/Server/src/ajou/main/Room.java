package ajou.main;

import kr.ac.ajou.protocol.ClientInfo;
import kr.ac.ajou.protocol.ClientNum;

import java.util.HashMap;

public class Room {

    private HashMap<ClientNum, ClientInfo> clientInfoHashMap;

    Room(ClientNum clientNum, ClientInfo clientInfo) {
        clientInfoHashMap = new HashMap<>();
        clientInfoHashMap.put(clientNum,clientInfo);
    }

    public void addClient(ClientNum clientNum, ClientInfo clientInfo){
        clientInfoHashMap.put(clientNum,clientInfo);
    }

}
