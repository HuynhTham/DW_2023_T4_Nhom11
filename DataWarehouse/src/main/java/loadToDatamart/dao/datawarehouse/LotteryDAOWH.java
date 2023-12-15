package loadToDatamart.dao.datawarehouse;
;
import loadToDatamart.dao.DBContext;
import loadToDatamart.dao.datamart.LotteryDAOMart;
import loadToDatamart.entity.*;
import loadToDatamart.entity.DateDim;
import loadToDatamart.entity.LotteryResult;
import loadToDatamart.entity.ProvineDim;
import loadToDatamart.entity.RewardDim;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.Query;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class LotteryDAOWH {

    //select all data from table reward_dim table
    public List<RewardDim> getAllReward() {
        try (Handle handle = DBContext.connectWarehouse().open()) {
            String query = "SELECT * FROM `reward_dim`";
            return handle.createQuery(query).mapToBean(RewardDim.class).list();
        }
    }
    //select data today from table reward_dim table

    //select al data from table province_dim table
    public List<ProvineDim> getAllProvince() {
        try (Handle handle = DBContext.connectWarehouse().open()) {
            String query = "SELECT * FROM `province_dim`";
            return handle.createQuery(query).mapToBean(ProvineDim.class).list();
        }
    }


    //select all data from table date_dim table
    public List<DateDim> getAllDate() {
        try (Handle handle = DBContext.connectWarehouse().open()) {
            String query = "SELECT * FROM date_dim";
            Query dateQuery = handle.createQuery(query);
            return dateQuery.mapToBean(DateDim.class).list();
        } catch (Exception e) {
            // Xử lý ngoại lệ (có thể log và/hoặc thông báo)
            e.printStackTrace(); // In ra thông báo lỗi hoặc sử dụng logger để ghi log
            // Có thể ném một ngoại lệ khác hoặc trả về một giá trị mặc định tùy thuộc vào yêu cầu
            return Collections.emptyList(); // Ví dụ: Trả về danh sách trống
        }
    }


    //select all data from lottery_result_fact table
    public List<LotteryResult> getAllLottery() {
        try (Handle handle = DBContext.connectWarehouse().open()) {
            String query = "SELECT * FROM `lottery_result_fact`";
            Query lotteryQuery = handle.createQuery(query);
            return lotteryQuery.mapToBean(LotteryResult.class).list();
        } catch (Exception e) {
            // Xử lý ngoại lệ (có thể log và/hoặc thông báo)
            e.printStackTrace(); // In ra thông báo lỗi hoặc sử dụng logger để ghi log
            // Có thể ném một ngoại lệ khác hoặc trả về một giá trị mặc định tùy thuộc vào yêu cầu
            return Collections.emptyList(); // Ví dụ: Trả về danh sách trống
        }
    }


    private void createTableIfNotExists(Handle handle, String tableName) {
        // Check if the table exists
        String checkTableQuery = "SHOW TABLES LIKE '" + tableName + "'";
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "`sk` INT PRIMARY KEY AUTO_INCREMENT," +
                "`id` INT," +
                "`id_reward` INT," +
                "`id_date` INT," +
                "`id_province` INT," +
                "`special_prize` VARCHAR(255)," +
                "`first_prize` VARCHAR(255)," +
                "`second_prize` VARCHAR(255)," +
                "`third_prize` VARCHAR(255)," +
                "`fourth_prize` VARCHAR(255)," +
                "`fifth_prize` VARCHAR(255)," +
                "`sixth_prize` VARCHAR(255)," +
                "`seventh_prize` VARCHAR(255)," +
                "`eighth_prize` VARCHAR(255)," +
                "FOREIGN KEY (`id_reward`) REFERENCES `reward_dim`(`id`)," +
                "FOREIGN KEY (`id_date`) REFERENCES `date_dim`(`id`)," +
                "FOREIGN KEY (`id_province`) REFERENCES `province_dim`(`id`)" +
                ")";

        boolean tableExists = handle.createQuery(checkTableQuery)
                .mapTo(String.class)
                .findFirst()
                .isPresent();

        // Create the table if it doesn't exist
        if (!tableExists) {
            handle.createUpdate(createTableQuery).execute();
        }
    }
    public int getIdDate(LocalDate date) {
        try (Handle handle = DBContext.connectWarehouse().open()) {
            String query = "SELECT d.id FROM date_dim d WHERE d.full_date =?";
            Integer result = handle.createQuery(query)
                    .bind(0, date)
                    .mapTo(int.class)  // Fix cú pháp ở đây
                    .findFirst().orElse(null);

            return (result != null) ? result : 0;  // hoặc giá trị mặc định khác tùy thuộc vào yêu cầu của bạn
        } catch (Exception e) {
            e.printStackTrace();
            return 0;  // hoặc giá trị mặc định khác tùy thuộc vào yêu cầu của bạn
        }
    }

    //tên tỉnh
    public int getIdprovince(String name) {
        try (Handle handle = DBContext.connectWarehouse().open()) {
            String query = "SELECT d.id FROM province_dim d WHERE d.name = ?";
            return handle.createQuery(query)
                    .bind(0,"Xổ số "+ name)
                    .mapTo(int.class) // Sửa chỗ này để trả về kiểu int
                    .findFirst()
                    .orElse(0); // Hoặc giá trị mặc định khác nếu không tìm thấy
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Hoặc giá trị mặc định khác nếu có lỗi
        }
    }

    public int getReward(String type) {
        try (Handle handle = DBContext.connectWarehouse().open()) {
            String query = "SELECT d.id FROM reward_dim d where d.type=?";
            return handle.createQuery(query)
                    .bind(0, type)
                    .mapTo(int.class)  // Sửa đổi kiểu dữ liệu ở đây
                    .first();  // Sử dụng first() thay vì mapTo(String.class).first()
        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý exception nếu cần
            return -1;  // Giá trị mặc định hoặc giá trị không hợp lệ nếu có lỗi
        }
    }
    public static void main(String[] args) {
        LotteryDAOWH lotteryDAO = new LotteryDAOWH();
        LotteryDAOMart lotteryDAOMart = new LotteryDAOMart();
        List<LotteryResult> listProvine = lotteryDAO.getAllLottery();
        for (LotteryResult pro : listProvine) {
            lotteryDAOMart.insertLottery("lottery_result", pro.getId(),pro.getId_reward(),pro.getId_date(),pro.getId_province(),pro.getSpecial_prize(),pro.getFirst_prize(),pro.getSecond_prize(),pro.getThird_prize(),pro.getFourth_prize(),pro.getFifth_prize(),pro.getSixth_prize(),pro.getSeventh_prize(),pro.getEighth_prize());
        }

    }
}
