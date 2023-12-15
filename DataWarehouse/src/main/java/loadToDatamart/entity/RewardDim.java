package loadToDatamart.entity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RewardDim {
    private int id;
    private String special_prize;
    private String eighth_prize;
    private String seventh_prize;
    private String sixth_prize;
    private String fifth_prize;
    private String fourth_prize;
    private String third_prize;
    private String second_prize;
    private String first_prize;
    private LocalDate date;
    private String type;

}
