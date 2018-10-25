package objects;

public class Status {

    private String data;
    private String status;

    public Status(String data, String status) {
        this.data = data;
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
