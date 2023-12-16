package warehouse.dao.datawarehouse;

import warehouse.dao.ControlDAO;
import warehouse.dao.DBContext;
import warehouse.dao.LogDAO;
import warehouse.dao.staging.LotteryResultDAOStaging;
import warehouse.entity.*;
import org.jdbi.v3.core.Handle;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class LotteryDAOWareHouse {


    private boolean isRewardIdValid(Handle handle, int rewardId) {
        String checkRewardExistenceQuery = "SELECT COUNT(*) FROM reward_dim WHERE id = ?";
        int rewardCount = handle.createQuery(checkRewardExistenceQuery)
                .bind(0, rewardId)
                .mapTo(Integer.class)
                .findOnly();

        return rewardCount > 0;
    }

    //    INsert INTO LotteryResult
    private void insertLotteryResult(Lottery_Result_Fact result) {
        try (Handle handle = DBContext.connectWarehouse().open()) {
            // Kiểm tra xem id_reward có tồn tại trong bảng reward_dim không
            if (isRewardIdValid(handle, result.getId_reward())) {
                // Thực hiện INSERT dữ liệu vào bảng trong Data Warehouse
                String query = "INSERT INTO lottery_result_fact (id_reward, id_date, id_province, special_prize, first_prize, second_prize, third_prize, fourth_prize, fifth_prize, sixth_prize, seventh_prize, eighth_prize) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                handle.createUpdate(query)
                        .bind(0, result.getId_reward())
                        .bind(1, result.getId_date())
                        .bind(2, result.getId_province())
                        .bind(3, result.getSpecial_prize())
                        .bind(4, result.getFirst_prize())
                        .bind(5, result.getSecond_prize())
                        .bind(6, result.getThird_prize())
                        .bind(7, result.getFourth_prize())
                        .bind(8, result.getFifth_prize())
                        .bind(9, result.getSixth_prize())
                        .bind(10, result.getSeventh_prize())
                        .bind(11, result.getEighth_prize())
                        .execute();
                System.out.println("Insert thành công LotteryResult!");
            } else {
                // Xử lý khi id_reward không hợp lệ (báo lỗi hoặc thực hiện hành động khác theo yêu cầu của bạn)
                System.out.println("Giá trị id_reward không hợp lệ!");
            }
        }
    }

    // INSERT INTO DATE_DIM
    public void insertDateDim(Date_dim dateDim) {
        try (Handle handle = DBContext.connectWarehouse().open()) {
            String query = "INSERT INTO date_dim (full_date, dayOfWeek, month, year) VALUES ( ?, ?, ?, ?)";

            int rowsAffected = handle.createUpdate(query)

                    .bind(0, dateDim.getFull_date())
                    .bind(1, dateDim.getDay_of_week())
                    .bind(2, dateDim.getMonth())
                    .bind(3, dateDim.getYear())
                    .execute();

            if (rowsAffected > 0) {
                System.out.println("Insert thành công! DateDim");
            } else {
                System.out.println("Insert không thành công!");
            }
        }

    }

    //    Insert INTO PROVINCE
    public void insertProvine(Province_Dim provineDim) {
        try (Handle handle = DBContext.connectWarehouse().open()) {
            // Bắt đầu transaction
            handle.begin();

            try {
                // Thực hiện các thao tác cơ sở dữ liệu ở đây
                String query = "INSERT INTO province_dim (name, region) VALUES (?, ?)";
                int rowsAffected = handle.createUpdate(query)
                        .bind(0, provineDim.getName())
                        .bind(1, provineDim.getRegion())
                        .execute();

                if (rowsAffected > 0) {
                    System.out.println("Insert thành công! ProvinceDim");

                    // Thực hiện commit nếu thành công
                    handle.commit();
                } else {
                    // Nếu không có dòng nào được ảnh hưởng, coi đó là lỗi và thực hiện rollback
                    handle.rollback();
                    System.out.println("Insert không thành công!");
                }
            } catch (Exception e) {
                // Xử lý các exception nếu có
                e.printStackTrace();

                // Rollback nếu có lỗi
                handle.rollback();
            }
        }
    }


    //    Insert INTO REWARD
    public void insertReward(String tableName, Reward_dim rewardDim) {
        try (Handle handle = DBContext.connectWarehouse().open()) {
            createTableIfNotExistsForReward(handle, tableName);

            // Insert data into the dynamically created table
            String query = "INSERT INTO " + tableName + "(special_prize, eighth_prize, seventh_prize, sixth_prize, fifth_prize, fourth_prize, third_prize, second_prize, first_prize, date, type) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

            // Kiểm tra xem rewardDim.getDate() có phải là null hay không trước khi sử dụng
            LocalDate date = rewardDim.getDate();
            Timestamp timestamp = (date != null) ? Timestamp.valueOf(date.atStartOfDay()) : null;

            handle.createUpdate(query)
                    .bind(0, rewardDim.getSpecial_prize())
                    .bind(1, rewardDim.getEighth_prize())
                    .bind(2, rewardDim.getSeventh_prize())
                    .bind(3, rewardDim.getSixth_prize())
                    .bind(4, rewardDim.getFifth_prize())
                    .bind(5, rewardDim.getFourth_prize())
                    .bind(6, rewardDim.getThird_prize())
                    .bind(7, rewardDim.getSecond_prize())
                    .bind(8, rewardDim.getFirst_prize())
                    .bind(9, timestamp)  // Sử dụng timestamp thay vì truyền trực tiếp giá trị date
                    .bind(10, rewardDim.getType())
                    .execute();

            System.out.println("Insert thành công!");
        }
    }
//Bảng Fix cho Reward

    //    Tạo Table ReWard
    private void createTableIfNotExistsForReward(Handle handle, String tableName) {
        String checkTableQuery = "SHOW TABLES LIKE '" + tableName + "'";
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "`id` INT PRIMARY KEY AUTO_INCREMENT," +
                "`special_prize` VARCHAR(255)," +
                "`eighth_prize` VARCHAR(255)," +
                "`seventh_prize` VARCHAR(255)," +
                "`sixth_prize` VARCHAR(255)," +
                "`fifth_prize` VARCHAR(255)," +
                "`fourth_prize` VARCHAR(255)," +
                "`third_prize` VARCHAR(255)," +
                "`second_prize` VARCHAR(255)," +
                "`first_prize` VARCHAR(255)," +
                "`date` DATE," +
                "`type` VARCHAR(255)" +
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


    //    Tạo Table Province
    private void createTableIfNotExistsForProvince(Handle handle, String tableName) {
        String checkTableQuery = "SHOW TABLES LIKE '" + tableName + "'";
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "`id` INT PRIMARY KEY AUTO_INCREMENT," +
                "`name` VARCHAR(255)," +
                "`region` VARCHAR(255)" +
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

    //    Tạo Table
    private void createTableIfNotExistsForDate(Handle handle, String tableName) {
        String checkTableQuery = "SHOW TABLES LIKE '" + tableName + "'";
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "`id` INT PRIMARY KEY AUTO_INCREMENT," +
                "`full_date` DATE," +
                "`day_of_week` VARCHAR(20)," +
                "`month` VARCHAR(20)," +
                "`year` VARCHAR(4)" +
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

    //    Tạo Table Lottery_result_fact
    private void createTableIfNotExists(Handle handle, String tableName) {
        // Check if the table exists
        String checkTableQuery = "SHOW TABLES LIKE '" + tableName + "'";
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
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
                "CONSTRAINT `lottery_result_fact_ibfk_1` FOREIGN KEY (`id_reward`) REFERENCES `reward_temporary`(`id`)," +
                "CONSTRAINT `lottery_result_fact_ibfk_2` FOREIGN KEY (`id_date`) REFERENCES `date_temporary`(`id`)," +
                "CONSTRAINT `lottery_result_fact_ibfk_3` FOREIGN KEY (`id_province`) REFERENCES `province_temporary`(`id`)" +
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
            String query = "SELECT d.id FROM datawarehouse.date_dim d WHERE d.full_date =?";
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
            String query = "SELECT d.id FROM datawarehouse.province_dim d WHERE d.name = ?";
            return handle.createQuery(query)
                    .bind(0, "Xổ số " + name)
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
            String query = "SELECT d.id FROM datawarehouse.reward_dim d where d.type=?";
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

    public void transferLotteryResultData() {
        if (new LogDAO().isLastLogStatusRunning("xosohomnay", "Load Data From Staging to Data Warehouse", "Success")) {
            new LogDAO().insertLog("xosohomnay", "Load Data From Staging to Data Warehouse", "Loaded");

            return;
        }
        if (new LogDAO().isLastLogStatusRunning("xosohomnay", "Get data from file to Staging", "Success")) {
            new LogDAO().insertLog("xosohomnay", "Load Data From Staging to Data Warehouse", "Start");

            LotteryResultDAOStaging stagingDAO = new LotteryResultDAOStaging();
            List<Lottery_Result> stagingData = stagingDAO.getAllStagingData(LocalDate.now());
            LotteryDAOWareHouse warehouseDAO = new LotteryDAOWareHouse();

            for (Lottery_Result stagingResult : stagingData) {
                int id_reward_str = 0;

                // Lấy Id của tỉnh, thưởng và ngày
                String province = stagingResult.getName_province();
                //check provine đã có tỏng provine-dim hay ko(select)

//            không insert vào province-dim

                int id_province_str = warehouseDAO.getIdprovince(province);
                String region = "";
                if (id_province_str <= 0) {


                    if (stagingResult.getRegions().toLowerCase().contains("miền nam")) {
                        region = "Miền Nam";
                    } else if (stagingResult.getRegions().toLowerCase().contains("miền trung")) {
                        region = "Miền Trung";

                    } else {
                        region = "Miền Bắc";
                    }
                    warehouseDAO.insertProvine(new Province_Dim("Xổ số "+ province,region));
                    id_province_str = warehouseDAO.getIdprovince(province);
                }

                if (stagingResult.getRegions().toLowerCase().contains("miền nam")||stagingResult.getRegions().toLowerCase().contains("miền trung")) {
                    id_reward_str = warehouseDAO.getReward("1");

                } else {
                    id_reward_str = warehouseDAO.getReward("0");

                }
                int id_date_str = warehouseDAO.getIdDate(stagingResult.getDate());

                // Chuyển đổi từ String sang int
                int id_province = id_province_str;
                int id_reward = id_reward_str;
                int id_date = id_date_str;
                // Map data from Staging to Data Warehouse model
                Lottery_Result_Fact dataWarehouseResult = new Lottery_Result_Fact();
                dataWarehouseResult.setId_province(id_province);
                dataWarehouseResult.setId_reward(id_reward);
                dataWarehouseResult.setId_date(id_date);
                dataWarehouseResult.setSpecial_prize(stagingResult.getGiaiDacBiet());
                dataWarehouseResult.setEighth_prize(stagingResult.getGiaiTam());
                dataWarehouseResult.setSeventh_prize(stagingResult.getGiaiBay());
                dataWarehouseResult.setSixth_prize(stagingResult.getGiaiSau());
                dataWarehouseResult.setFifth_prize(stagingResult.getGiaiNam());
                dataWarehouseResult.setFourth_prize(stagingResult.getGiaiTu());
                dataWarehouseResult.setThird_prize(stagingResult.getGiaiBa());
                dataWarehouseResult.setSecond_prize(stagingResult.getGiaiNhi());
                dataWarehouseResult.setFirst_prize(stagingResult.getGiaiNhat());
                // Insert data into Data Warehouse
                insertLotteryResult(dataWarehouseResult);
            }
            new LogDAO().insertLog("xosohomnay", "Load Data From Staging to Data Warehouse", "Success");
        } else {
            new LogDAO().insertLog("xosohomnay", "Load Data From Staging to Data Warehouse", "Can not run");
        }
    }

    public void transferProvinceData() {
        LotteryResultDAOStaging stagingDAO = new LotteryResultDAOStaging();
        List<Lottery_Result> stagingData = stagingDAO.getAllStagingData(LocalDate.now());

        for (Lottery_Result stagingResult : stagingData) {
            Province_Dim provinceDim = new Province_Dim();

            // Map data from Staging to Data Warehouse model
            provinceDim.setRegion(stagingResult.getRegions());
            provinceDim.setName(stagingResult.getName_province());

            // Insert data into Data Warehouse
            insertProvine(provinceDim);
        }
    }

    public void transferDateData() {
        LotteryResultDAOStaging stagingDAO = new LotteryResultDAOStaging();
        List<Lottery_Result> stagingData = stagingDAO.getAllStagingData(LocalDate.now());

        for (Lottery_Result stagingResult : stagingData) {
            Date_dim dateDim = new Date_dim();

            // Insert data into Data Warehouse
            insertDateDim(dateDim);
        }
    }

    public void transferRewardData() {
        try {
            LotteryResultDAOStaging stagingDAO = new LotteryResultDAOStaging();
            List<Lottery_Result> stagingData = stagingDAO.getAllStagingData(LocalDate.now());

            for (Lottery_Result stagingResult : stagingData) {
                // Extract reward data from stagingResult and map it to Reward_dim model
                String specialPrize = stagingResult.getGiaiDacBiet();
                String eighthPrize = stagingResult.getTienThuong_Tam();
                String sevenPrize = stagingResult.getTienThuong_Bay();
                String sixPrize = stagingResult.getTienThuong_Sau();
                String fivePrize = stagingResult.getTienThuong_Nam();
                String fourPrize = stagingResult.getTienThuong_Tu();
                String threePrize = stagingResult.getTienThuong_Ba();
                String twoPrize = stagingResult.getTienThuong_Nhat();
                String firstPrize = stagingResult.getTienThuong_Nhat();
                LocalDate data_reward = stagingResult.getDate();

                Reward_dim rewardDim = new Reward_dim();
                rewardDim.setSpecial_prize(specialPrize);
                rewardDim.setEighth_prize(eighthPrize);
                rewardDim.setSeventh_prize(sevenPrize);
                rewardDim.setSixth_prize(sixPrize);
                rewardDim.setFifth_prize(fivePrize);
                rewardDim.setFourth_prize(fourPrize);
                rewardDim.setThird_prize(threePrize);
                rewardDim.setSecond_prize(twoPrize);
                rewardDim.setFirst_prize(firstPrize);
                rewardDim.setDate(data_reward);

                insertReward("reward_dim", rewardDim);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getProvinceName(String input) {
        // Tìm vị trí của "Xổ số"
        int index = input.indexOf("Xổ số");

        if (index != -1) {
            // Cắt chuỗi từ vị trí sau "Xổ số" đến hết chuỗi
            return input.substring(index + "Xổ số".length()).trim();
        } else {
            // Nếu không tìm thấy "Xổ số", trả về toàn bộ chuỗi
            return input;
        }
    }

    public static void main(String[] args) {


        try {
            LotteryDAOWareHouse lotteryDAOWareHouse = new LotteryDAOWareHouse();
            lotteryDAOWareHouse.transferLotteryResultData();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

