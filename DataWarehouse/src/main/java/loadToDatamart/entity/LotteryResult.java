package loadToDatamart.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LotteryResult {
//    private int sk;
    private int id;
    private int id_reward;
    private int id_date;
    private int id_province;
    private String special_prize;
    private String first_prize;
    private String second_prize;
    private String third_prize;
    private String fourth_prize;
    private String fifth_prize;
    private String sixth_prize;
    private String seventh_prize;
    private String eighth_prize;

}
