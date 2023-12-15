package loadToDatamart.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class DataFiles {
    private int id;
    private int config_id;
    private String name;
    private int row_count;
    private String status;
    private Date data_range_from;
    private Date data_range_to;
    private String note;
    private Timestamp create_at;
    private Timestamp update_at;
    private String create_by;
    private String update_by;

}
