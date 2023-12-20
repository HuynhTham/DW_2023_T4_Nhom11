package staging.modules;
import staging.db.StagingConnect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
public class test {
    public static void insertDataFromCSV(String csvFilePath) {
        String jdbcUrl = StagingConnect.getJdbcUrl();
        String username = StagingConnect.getUsername();
        String password = StagingConnect.getPassword();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                // Parse and insert data into database
                insertLotteryResult(connection, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static void insertLotteryResult(Connection connection, String[] data) {
        if (data.length < 3) {
            System.out.println("Invalid data: Insufficient elements.");
            return;
        }
        String insertQuery = "INSERT INTO `staging`.`lottery_result` (`date`, `regions`, `name_province`, `GiaiTam`, `TienThuong_Tam`, `GiaiBay`, `TienThuong_Bay`, `GiaiSau`, `TienThuong_Sau`, `GiaiNam`, `TienThuong_Nam`, `GiaiTu`, `TienThuong_Tu`, `GiaiBa`, `TienThuong_Ba`, `GiaiNhi`, `TienThuong_Nhi`, `GiaiNhat`, `TienThuong_Nhat`, `GiaiDacBiet`, `TienThuong_DB`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            // Parse and set values for the PreparedStatement
            String dateString = data[0].replaceAll("[^\\p{ASCII}]", ""); // Xóa ký tự không in thường
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            preparedStatement.setDate(1, java.sql.Date.valueOf(localDate));

            String regions = (data.length > 1) ? data[1] : "DefaultRegions";
            String nameProvince = (data.length > 2) ? data[2] : "DefaultNameProvince";

            preparedStatement.setString(2, regions);
            preparedStatement.setString(3, nameProvince);

            for (int i = 4; i <= 21; i++) {
                preparedStatement.setString(i, data[i - 1]); // Điều chỉnh index và truy cập vào mảng data
            }

            System.out.println(preparedStatement);

            preparedStatement.executeUpdate();

        } catch (DateTimeParseException e) {
            // Xử lý lỗi chuyển đổi ngày
            e.printStackTrace();
        } catch (SQLException e) {
            // Xử lý lỗi SQL
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        String csvFilePath = "D:/datawarehouse/Datawarehouse_Extract/data/dataAll_2023_11_25_new.csv";
        insertDataFromCSV(csvFilePath);
    }
}
