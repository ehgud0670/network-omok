package ajou.protocol;

public class RoomNum {

    private int roomNum;

    public RoomNum(int roomNum){
        this.roomNum = roomNum;
    }

    public int getRoomNum() {
        return roomNum;
    }

    @Override
    public int hashCode() {
        return roomNum;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  RoomNum){
            RoomNum roomNum = (RoomNum) obj;
            return this.roomNum == roomNum.getRoomNum();
        }
        return false;
    }
}
