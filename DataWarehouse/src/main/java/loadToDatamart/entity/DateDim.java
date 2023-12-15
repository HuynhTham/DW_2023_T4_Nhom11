package loadToDatamart.entity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DateDim {
    private  int id;
    private LocalDate full_date;
    private String day_of_week;
    private String month;
    private String year;
}
