package ajou.main;

import kr.ac.ajou.protocol.ClientNum;

import java.util.Comparator;

public class ClientNumAscendingComparator implements Comparator<ClientNum> {
    @Override
    public int compare(ClientNum o1, ClientNum o2) {
        return Integer.compare(o1.getClientNum(), o2.getClientNum());
    }
}
