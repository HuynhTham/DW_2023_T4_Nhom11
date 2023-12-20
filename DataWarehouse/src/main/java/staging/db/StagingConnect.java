package staging.db;
import staging.db.ControlConnect;

import java.io.InputStream;
import java.util.Properties;

public class StagingConnect {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = StagingConnect.class.getResourceAsStream("/staging.properties")) {
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String getJdbcUrl() {
        String s = (new StringBuilder()).append(properties.getProperty("db.jdbcUrl")).append("://").append(getHost()).append(":").append(getPort()).append("/").append(getName()).toString();
        return s;
    }

    public static String getHost() {
        return properties.getProperty("db.host");
    }

    public static String getPort() {
        return properties.getProperty("db.port");
    }

    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }

    public static String getName() {
        return properties.getProperty("db.name");
    }

    public static String getFilePath(){
        return ControlConnect.getFilePath();
    }
    public static void main(String[] args) {
//		System.out.println(getJdbcUrl());
		System.out.println(getFilePath());
//        File f = new File("data/xosohomnay.com.vn_10_12_20230.csv");
//        String str = f.getAbsolutePath();
//        System.out.println(f.getAbsolutePath());
    }
}

