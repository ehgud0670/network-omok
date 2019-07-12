package ajou.protocol;


public class ClientNum{

    private int clientNum;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  ClientNum){
            ClientNum clientNum = (ClientNum) obj;
            return this.clientNum == clientNum.getClientNum();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return clientNum;
    }

    public ClientNum(int clientNum) {
        this.clientNum = clientNum;
    }

    public int getClientNum() {
        return clientNum;
    }
}
