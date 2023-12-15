package loadToDatamart.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBContext {
    private static HikariDataSource dataSourceControl;
    private static HikariDataSource dataSourceWarehouse;
    private static HikariDataSource dataSourceDataMart;

    static Jdbi jdbiControl;
    static Jdbi jdbiWarehouse;
    static Jdbi jdbiDataMart;

    static Properties prop = new Properties();

    private static void initializeDatabase(HikariDataSource dataSource, String dbName, String password) {
        String serverName = prop.getProperty("serverName");
        String portNumber = prop.getProperty("portNumber");
        String instance = prop.getProperty("instance");

        String userID = prop.getProperty("userID");
        String url = "jdbc:mysql://" + serverName + ":" + portNumber + "/" + dbName;
        if (instance != null && !instance.trim().isEmpty()) {
            url = "jdbc:mysql://" + serverName + ":" + portNumber + "/" + instance + "/" + dbName;
        }

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(userID);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(200);
        dataSource.setMinimumIdle(30);
    }

    public static Jdbi connectControl() {
        try (InputStream input = DBContext.class.getClassLoader().getResourceAsStream("mart.properties")) {
            prop.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String dbName = prop.getProperty("dbNameControl");
        String password = prop.getProperty("passwordControl");

        if (dataSourceControl == null) {
            dataSourceControl = new HikariDataSource();
            initializeDatabase(dataSourceControl, dbName, password);
            jdbiControl = Jdbi.create(dataSourceControl);
        }

        return jdbiControl;
    }

    public static Jdbi connectWarehouse() {
        try (InputStream input = DBContext.class.getClassLoader().getResourceAsStream("mart.properties")) {
            prop.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String dbName = prop.getProperty("dbNameWarehouse");
        String password = prop.getProperty("passwordWarehouse");

        if (dataSourceWarehouse == null) {
            dataSourceWarehouse = new HikariDataSource();
            initializeDatabase(dataSourceWarehouse, dbName, password);
            jdbiWarehouse = Jdbi.create(dataSourceWarehouse);
        }

        return jdbiWarehouse;
    }

    public static Jdbi connectDataMart() {
        try (InputStream input = DBContext.class.getClassLoader().getResourceAsStream("mart.properties")) {
            prop.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String dbName = prop.getProperty("dbNameDataMart");
        String password = prop.getProperty("passwordDataMart");

        if (dataSourceDataMart == null) {
            dataSourceDataMart = new HikariDataSource();
            initializeDatabase(dataSourceDataMart, dbName, password);
            jdbiDataMart = Jdbi.create(dataSourceDataMart);
        }

        return jdbiDataMart;
    }
    public static void renameTable(String oldTableName, String newTableName,String propertydbName,String propertypassword) {
        try (InputStream input = DBContext.class.getClassLoader().getResourceAsStream("mart.properties")) {
            prop.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String jdbcUrl = "jdbc:mysql://" + prop.getProperty("serverName") + ":" + prop.getProperty("portNumber") + "/" + prop.getProperty(propertydbName);
        String username = prop.getProperty("userID");
        String password = prop.getProperty(propertypassword);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            try (Statement statement = connection.createStatement()) {
                String sql = "ALTER TABLE " + oldTableName + " RENAME TO " + newTableName;
                statement.executeUpdate(sql);
                System.out.println("Table renamed successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DBContext() {
    }

    public static Connection getConnection() throws SQLException {
        // Choose the appropriate dataSource based on your requirements
        return dataSourceControl.getConnection();
    }




    public static void main(String[] args) throws SQLException {
//        renameTable("test","testNew", "dbNameDataMart","passwordDataMart");

        String excelFilePath = "C:\\Users\\Asus\\Desktop\\warehouse_ck\\DW\\new_date_dim.csv";


    }
}
