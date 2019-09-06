package si.gounitis.trimixmix.model;

import java.util.Date;

public class ReadData {
    private String value;
    private Date timestamp;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
