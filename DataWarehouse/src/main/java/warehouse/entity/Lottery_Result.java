package warehouse.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Lottery_Result {
    private int id;
    private LocalDate date;
    private String regions;
    private String name_province;
    private String giaiTam;
    private String tienThuong_Tam;
    private String giaiBay;
    private String tienThuong_Bay;
    private String giaiSau;
    private String tienThuong_Sau;
    private String giaiNam;
    private String tienThuong_Nam;
    private String giaiTu;
    private String tienThuong_Tu;
    private String giaiBa;
    private String tienThuong_Ba;
    private String giaiNhi;
    private String tienThuong_Nhi;
    private String giaiNhat;
    private String tienThuong_Nhat;
    private String giaiDacBiet;
    private String tienThuong_DB;


}
