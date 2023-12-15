package loadToDatamart.dao.datamart;

import loadToDatamart.dao.DBContext;
import org.jdbi.v3.core.Handle;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

public class LotteryDAOMart {
    //insert data from table reward_dim datawaremart
    public void insertReward(int id,String tableName, String special_prize, String first_prize, String second_prize, String third_prize, String fourth_prize, String fifth_prize, String sixth_prize, String seventh_prize, String eighth_prize, LocalDate date, String type) {
        try (Handle handle = DBContext.connectDataMart().open()) {
            createTableIfNotExistsForReward(handle, tableName);

            // Insert data into the dynamically created table
            String query = "INSERT INTO " + tableName + "(id,special_prize, eighth_prize, seventh_prize, sixth_prize, fifth_prize, fourth_prize, third_prize, second_prize, first_prize, date, type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            handle.createUpdate(query)
                    .bind(0, id)
                    .bind(1, special_prize)
                    .bind(2, eighth_prize)
                    .bind(3, seventh_prize)
                    .bind(4, sixth_prize)
                    .bind(5, fifth_prize)
                    .bind(6, fourth_prize)
                    .bind(7, third_prize)
                    .bind(8, second_prize)
                    .bind(9, first_prize)
                    .bind(10, Timestamp.valueOf(date.atStartOfDay()))
                    .bind(11, type)
                    .execute();
        }
    }

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

    //insert data from table province_dim datawaremart
    public void insertProvince(int id,String tableName, String name, String region) {
        try (Handle handle = DBContext.connectDataMart().open()) {
            // Create the table if it doesn't exist
            createTableIfNotExistsForProvince(handle, tableName);

            // Insert data into the dynamically created table
            String query = "INSERT INTO " + tableName + "(`id`,`name`, `region`) VALUES (?,?,?)";
            handle.createUpdate(query)
                    .bind(0, id)
                    .bind(1, name)
                    .bind(2, region)
                    .execute();
        }
    }

    private void createTableIfNotExistsForProvince(Handle handle, String tableName) {
        String checkTableQuery = "SHOW TABLES LIKE '" + tableName + "'";
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "`id` INT PRIMARY KEY," +
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

    //insert data from table date_dim datawaremart
    public void insertDate(int id,String tableName, LocalDate fullDate, String dayOfWeek, String month, String year) {
        try (Handle handle = DBContext.connectDataMart().open()) {
            // Create the table if it doesn't exist
            createTableIfNotExistsForDate(handle, tableName);

            // Insert data into the dynamically created table
            String query = "INSERT INTO " + tableName + "(`id`,`full_date`, `day_of_week`, `month`, `year`) VALUES (?,?,?,?,?)";
            handle.createUpdate(query)
                    .bind(0, id)
                    .bind(1, Timestamp.valueOf(fullDate.atStartOfDay()))
                    .bind(2, dayOfWeek)
                    .bind(3, month)
                    .bind(4, year)
                    .execute();
        }
    }

    private void createTableIfNotExistsForDate(Handle handle, String tableName) {
        String checkTableQuery = "SHOW TABLES LIKE '" + tableName + "'";
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "`id` INT PRIMARY KEY," +
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

    //insert result vào datamart
    public void insertLottery(String tableName, int id, int id_reward, int id_date, int id_province, String special_prize, String first_prize, String second_prize, String third_prize, String fourth_prize, String fifth_prize, String sixth_prize, String seventh_prize, String eighth_prize) {
        try (Handle handle = DBContext.connectDataMart().open()) {
            // Create the table if it doesn't exist
            createTableIfNotExists(handle, tableName);
            // Insert data into the dynamically created table
            String query = "INSERT INTO " + tableName + "(`id`, `id_reward`, `id_date`, `id_province`, `special_prize`, `first_prize`, `second_prize`, `third_prize`, `fourth_prize`, `fifth_prize`, `sixth_prize`, `seventh_prize`, `eighth_prize`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            Timestamp now = Timestamp.from(Instant.now());
            handle.createUpdate(query)
                    .bind(0, id)
                    .bind(1, id_reward)
                    .bind(2, id_date)
                    .bind(3, id_province)
                    .bind(4, special_prize)
                    .bind(5, first_prize)
                    .bind(6, second_prize)
                    .bind(7, third_prize)
                    .bind(8, fourth_prize)
                    .bind(9, fifth_prize)
                    .bind(10, sixth_prize)
                    .bind(11, seventh_prize)
                    .bind(12, eighth_prize)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
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
                "CONSTRAINT `lottery_result_fact_ibfk_11` FOREIGN KEY (`id_reward`) REFERENCES `reward_temporary`(`id`)," +
                "CONSTRAINT `lottery_result_fact_ibfk_22` FOREIGN KEY (`id_date`) REFERENCES `date_temporary`(`id`)," +
                "CONSTRAINT `lottery_result_fact_ibfk_33` FOREIGN KEY (`id_province`) REFERENCES `province_temporary`(`id`)" +
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

    public void deleteTable(String tableName) {
        try (Handle handle = DBContext.connectDataMart().open()) {
            String query = "DROP TABLE IF EXISTS " + tableName;
            handle.execute(query);
            System.out.println("Table deleted successfully: " + tableName);
        } catch (Exception e) {
            // Handle the exception appropriately, e.g., log or rethrow
            e.printStackTrace();
        }
    }

    public static void updateForeignKey(String tableName, String oldFkName, String newFkName, String columnName,
                                        String referencedTable, String referencedColumn) {
        try (Handle handle = DBContext.connectDataMart().open()) {

            // Drop the existing foreign key constraint
            String dropFkQuery = "ALTER TABLE " + tableName + " DROP FOREIGN KEY " + oldFkName;
            handle.createUpdate(dropFkQuery).execute();

            // Add the new foreign key constraint
            String addFkQuery = "ALTER TABLE " + tableName + " ADD CONSTRAINT " + newFkName +
                    " FOREIGN KEY (" + columnName + ") " +
                    " REFERENCES " + referencedTable + "(" + referencedColumn + ")";
            handle.createUpdate(addFkQuery).execute();

            System.out.println("Foreign key updated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renameForeignKey(String tableName, String newFkName, String oldFkName, String column_name, String referencedTableName) {
        try (Handle handle = DBContext.connectDataMart().open()) {
            // Drop the old foreign key
            handle.execute("ALTER TABLE " + tableName +
                    " DROP FOREIGN KEY " + oldFkName);

            // Add the new foreign key with the desired name
            handle.execute("ALTER TABLE " + tableName +
                    " ADD CONSTRAINT " + newFkName +
                    " FOREIGN KEY ("+"`" + column_name +"`"+ ") REFERENCES " + referencedTableName + "(`id`)");
            // Replace 'column_name' with the actual column name in your table
            // Replace 'referenced_table' with the actual referenced table name

            System.out.println("Đổi tên khóa ngoại thành công!");

        } catch (Exception e) {
            // Handle the exception appropriately, e.g., log or rethrow
            e.printStackTrace();
        }
    }



    public String getForeignKeyName(String tableName, String referencedTableName) {
        try (Handle handle = DBContext.connectDataMart().open()) {
            String sql = "SELECT CONSTRAINT_NAME " +
                    "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
                    "WHERE TABLE_NAME = :tableName " +
                    "AND REFERENCED_TABLE_NAME = :referencedTableName";

            return handle.createQuery(sql)
                    .bind("tableName", tableName)
                    .bind("referencedTableName", referencedTableName)
                    .mapTo(String.class)
                    .one();
        } catch (Exception e) {
            // Handle exceptions appropriately, e.g., log or rethrow
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        LotteryDAOMart lotteryMart = new LotteryDAOMart();

//        new LotteryDAOMart().updateForeignKey("lottery_result","lottery_result_ibfk_1","lottery_result_ibfk_1","id_reward","reward","id");
//        System.out.println(new LotteryDAOMart().getForeignKeyName("lottery_result", "reward"));
        lotteryMart.renameForeignKey("lottery_result","lottery_result_reward",lotteryMart.getForeignKeyName("lottery_result", "reward"),"id_reward","reward");
        lotteryMart.renameForeignKey("lottery_result","lottery_result_date",lotteryMart.getForeignKeyName("lottery_result", "date"),"id_date","date");
        lotteryMart.renameForeignKey("lottery_result","lottery_result_province",lotteryMart.getForeignKeyName("lottery_result", "province"),"id_province","province");

    }
}
