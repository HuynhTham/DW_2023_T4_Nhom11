package Extract.entity;

import lombok.*;

import java.sql.Timestamp;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Logs {
    private int id;
    private String name;
    private String event_type;
    private String status;
    private Timestamp created_at;


}
