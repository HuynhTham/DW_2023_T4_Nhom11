package loadToDatamart.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString
public class FileConfigs {
    private int id;
    private String name;
    private String description;
    private String source_path;
    private String location;
    private String format;
    private String separator;
    private String columns;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String created_by;
    private String updated_by;

}
