package ajou.protocol;

public class Protocol {

    private String data;
    private String type;

    public Protocol(String data, String type) {
        this.data = data;
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }
}

