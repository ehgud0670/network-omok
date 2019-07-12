package ajou.protocol;


import java.util.Date;

public class ClientInfo {

    private IdData idData;
    private Date enterTime;

    public ClientInfo(){
        enterTime = new Date();
    }

    public void setIdData(IdData idData) {

        this.idData = idData;
    }

    public IdData getIdData() {
        return idData;
    }

    public Date getEnterTime() {
        return enterTime;
    }
}

