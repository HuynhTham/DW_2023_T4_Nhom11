package warehouse.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Date_dim {
    private  int id;
    private LocalDate full_date;
    private String day_of_week;
    private String month;
    private String year;
}
