package ajou.protocol;

import java.util.Comparator;

public class RoomNumAscendingComparator implements Comparator<RoomNum> {
    @Override
    public int compare(RoomNum o1, RoomNum o2) {
        return Integer.compare(o1.getRoomNum(),o2.getRoomNum());
    }
}
