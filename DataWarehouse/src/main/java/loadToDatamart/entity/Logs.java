package loadToDatamart.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
@Getter
@Setter
@ToString
public class Logs {
    private int id;
    private String name;
    private String event_type;
    private String status;
    private Timestamp created_at;


}
