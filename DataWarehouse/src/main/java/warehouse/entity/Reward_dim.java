package warehouse.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class Reward_dim {
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
