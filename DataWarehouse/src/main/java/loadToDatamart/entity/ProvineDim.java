package loadToDatamart.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProvineDim {
    private int id;
    private String name;
    private String region;
}
