package staging.db.init;

import java.sql.Timestamp;
public class Logs {
    private int id;
    private String name;
    private String event_type;
    private String status;
    private Timestamp created_at;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Logs[" +
                "id= " + id +
                ", name= '" + name + '\'' +
                ", event_type= '" + event_type + '\'' +
                ", status= '" + status + '\'' +
                ", created_at= " + created_at +
                ']';
    }
}