package staging.modules;

import staging.db.ControlConnect;
import  staging.db.StagingConnect;
import  staging.Services.Email;
import  staging.services.EmailUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Staging {

    /*3. Kiểm tra xem dư liệu của ngày hôm nay đã được thêm vào chưa?*/
    public static void run_process(){
        if ((ControlConnect.checkLog("xosohomnay","Get data from file to Staging","Success")==false)&&
                (ControlConnect.checkLog("xosohomnay","Get data from source","Success")==true)){
            process();
        }else {
            /*3.1. Gửi Thông báo đã có dữ liệu về Email admin */
            if (ControlConnect.checkLog("xosohomnay","Get data from source","Success")==false){
                sendEmail("Get data from source not yet completed!!!");
                System.out.println("Get data from source not yet completed!!!");

            }

            sendEmail("The data download was skipped because it was already done on "+ LocalDate.now());
            System.out.println("The data download was skipped because it was already done on "+ LocalDate.now());
        }
    }
    public static void process() {
        Connection connection = null;
        try {
            /* 4. Lấy đường dẫn đến file csv mới */
            String csvFilePath = ControlConnect.getFilePath();
            /*	6. Load các biến cục bộ kết nối với StagingConnect */
            String jdbcURL = StagingConnect.getJdbcUrl();
            String username = StagingConnect.getUsername();
            String password = StagingConnect.getPassword();

            /* 7. Kết nối với Staging.db */
            connection = DriverManager.getConnection(jdbcURL, username, password);

            /*5. Xử lý dữ liệu (Đọc file, load nd file,...) */
            try (InputStreamReader streamReader = new InputStreamReader(new FileInputStream(csvFilePath), StandardCharsets.UTF_8);

                 CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(streamReader);) {
                /* 8. Cập nhật status cho envent được thực hiện */
                ControlConnect.insertLog("xosohomnay", "Get data from file to Staging", "Start");

                // Lấy ngày, miền, tên tỉnh để kiểm tra sự tồn tại của dữ liệu
                String checkExistenceQuery = "SELECT COUNT(*) FROM lottery_result WHERE date = ? AND regions = ? AND name_province = ?";

                // INSERT dữ liệu vào database staging
                String insertQuery = "INSERT INTO lottery_result (date, regions, name_province, GiaiTam, TienThuong_Tam, GiaiBay, TienThuong_Bay, GiaiSau, TienThuong_Sau, GiaiNam, TienThuong_Nam, GiaiTu, TienThuong_Tu, GiaiBa, TienThuong_Ba, GiaiNhi, TienThuong_Nhi, GiaiNhat, TienThuong_Nhat, GiaiDacBiet, TienThuong_DB) "
                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


                try (PreparedStatement existenceStatement = connection.prepareStatement(checkExistenceQuery);
                     PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    /* 9. Xử lý trùng lắp dữ liệu */
                    for (CSVRecord record : csvParser) {
                        /* Lấy dữ liệu từ CSV Record */
                        String date = record.get("Ngay");
                        String regions = record.get("Mien");
                        String name_province = record.get("TenDai");
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                        // Chuyển đổi chuỗi ngày thành LocalDate
                        LocalDate localDate = LocalDate.parse(date, inputFormatter);

                        // Định dạng của LocalDate đầu ra
                        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                        // Chuyển đổi thành định dạng mới
                        LocalDate formattedDate = LocalDate.parse(localDate.format(outputFormatter));

                        existenceStatement.setDate(1, Date.valueOf(formattedDate));
                        existenceStatement.setString(2, regions);
                        existenceStatement.setString(3, name_province);

                        try (ResultSet resultSet = existenceStatement.executeQuery()) {
                            if (resultSet.next() && resultSet.getInt(1) > 0) {
                                String message = "Data already exists for date: " + date + ", regions: " + regions
                                        + ", name_province: " + name_province;
                                /* 9.1. Gửi Thông báo trùng lắp về Email admin */
                                sendEmail(message);
                                /* 9.2. Ghi log trạng thái trùng lấp và chuyển sang dòng tiếp theo */
                                ControlConnect.insertLog("xosohomnay", "Get data from file to Staging", "Data already exists");
                                System.out.println(message);
                                continue;
                            }
                        }
					/*
						Thiết lập giá trị cho PreparedStatement
					 */
                        String[] columnNames = {"Ngay", "Mien", "TenDai", "GiaiTam", "TienThuong_Tam", "GiaiBay",
                                "TienThuong_Bay", "GiaiSau", "TienThuong_Sau", "GiaiNam", "TienThuong_Nam", "GiaiTu",
                                "TienThuong_Tu", "GiaiBa", "TienThuong_Ba", "GiaiNhi", "TienThuong_Nhi", "GiaiNhat",
                                "TienThuong_Nhat", "giaidacbiet", "TienThuong_DB"};

                        preparedStatement.setString(1, String.valueOf(formattedDate));
                        for (int i = 1; i < columnNames.length; i++) {
                            preparedStatement.setString(i + 1, record.get(columnNames[i]));
                        }

                        /* 10. Insert dữ liệu vào Staging.db*/
                        preparedStatement.executeUpdate();
                    }
                }

                System.out.println("Data loaded into Staging successfully.");
                /* 11. Thông báo trạng thái về Email admin */
                sendEmail("Data loaded into Staging database successfully.");
                /* 12. Cập nhật lại status trong logs sau khi đã insert thành công */
                ControlConnect.insertLog("xosohomnay", "Get data from file to Staging", "Success");

            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /* 13. Đóng kết nối */
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Connection closed.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* Gửi email thông báo xự kiện insert đã diễn ra*/
    private static void sendEmail(String messageBody) {
        Email email = new Email();
        email.setFrom("dothanhlike10102002@gmail.com");
        email.setFromPassword("kedm slxz ubvb fkvj");
        email.setTo("phelimhr@gmail.com");
        email.setSubject("Notifications");
        StringBuilder sb = new StringBuilder();
        sb.append(messageBody);

        email.setContent(sb.toString());
        try {
            EmailUtils.send(email);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        Staging.run_process();
    }
}
