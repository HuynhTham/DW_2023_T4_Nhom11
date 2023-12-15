package warehouse.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.util.Properties;
public class DBContext {
    private static HikariDataSource dataSourceControl;
    private static HikariDataSource dataSourceWarehouse;
    private static HikariDataSource dataSourceStaging;

    static Jdbi jdbiControl;
    static Jdbi jdbiWarehouse;
    static Jdbi jdbiStaging;

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
        try (InputStream input = DBContext.class.getClassLoader().getResourceAsStream("config.properties")) {
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
        try (InputStream input = DBContext.class.getClassLoader().getResourceAsStream("config.properties")) {
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
    public static Jdbi connectStaging() {
        try (InputStream input = DBContext.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String dbName = prop.getProperty("dbNameStaging");
        String password = prop.getProperty("passwordStaging");

        if (dataSourceStaging == null) {
            dataSourceStaging = new HikariDataSource();
            initializeDatabase(dataSourceStaging, dbName, password);
            jdbiStaging = Jdbi.create(dataSourceStaging);
        }

        return jdbiStaging;
    }

    public static Connection getConnection() throws SQLException {
        // Choose the appropriate dataSource based on your requirements
        return dataSourceControl.getConnection();
    }

    public static void main(String[] args) {
        // Gọi phương thức connectControl trước khi sử dụng getConnection
        connectControl();
        connectWarehouse(); // Kết nối đến cơ sở dữ liệu Warehouse
        connectStaging();   // Kết nối đến cơ sở dữ liệu Staging

        try (Connection connection = getConnection()) {
            if (connection.isValid(5000)) {
                System.out.println("Kết nối thành công!");
            } else {
                System.out.println("Kết nối không thành công!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
