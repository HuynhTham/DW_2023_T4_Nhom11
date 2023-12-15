package Extract.dao;

import com.opencsv.CSVReader;
import Extract.entity.FileConfigs;
import org.jdbi.v3.core.Handle;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDate;
import java.nio.file.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class ControlDAO {
    public Optional<FileConfigs> getConfigByName(String source) {
        try (Handle handle = DBContext.connectControl().open()) {
            String query = "SELECT * FROM file_configs " +
                    "WHERE source_path = ?";
            return handle.createQuery(query)
                    .bind(0, source)
                    .mapToBean(FileConfigs.class)
                    .findOne();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public List<String> getPathFileData(LocalDate time) {
        List<String> filePaths = new ArrayList<>();

        try (Handle handle = DBContext.connectControl().open()) {
            String query = "SELECT c.location, c.format, c.separator, f.name, f.data_range_from FROM file_configs c JOIN data_files f ON c.id = f.df_config_id WHERE DATE(f.data_range_from) = ?";

            try (Connection connection = handle.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                // Convert LocalDate to java.sql.Date
                statement.setDate(1, Date.valueOf(time));
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        // Replace "location" with the actual column name in your database
                        String filePath = resultSet.getString("location") + resultSet.getString("name");
                        System.out.println("file path"+filePath);
                        filePaths.add(filePath);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePaths;
    }

    public String getPathFileDataFinal() {
        String filePath = "";

        try (Handle handle = DBContext.connectControl().open()) {
            String query = "SELECT c.location, c.format, c.separator, f.name FROM file_configs c JOIN data_files f ON c.id = f.df_conlig_id Order by f.Created_at DESC limit 1";

            try (Connection connection = handle.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // Replace "location" with the actual column name in your database
                        filePath = resultSet.getString("location") + resultSet.getString("name");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }

    public boolean isFullData(String path) {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
            String[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null) {
                for (String value : nextRecord) {
                    if (containsQuestionMark(value)) {
                        return false; // Dấu chấm hỏi được tìm thấy trong giá trị
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Xử lý ngoại lệ nếu có lỗi trong quá trình đọc tệp
        }

        return true; // Không có dấu chấm hỏi được tìm thấy
    }
    public  void  insertDataFile(String path,String nameFile){
        try (Handle handle = DBContext.connectControl().open()) {
            String query = "INSERT INTO data_files (df_config_id, name, row_count, status, data_range_from,data_range_to,note, Created_at, updated_at,created_by, updated_by) VALUES (?, ?, ?, ?,?,?,?,?,?,?,?)";
            Timestamp now = Timestamp.from(java.time.Instant.now());
            handle.createUpdate(query)
                    .bind(0, 1)
                    .bind(1, nameFile)
                    .bind(2, countLines(path))
                    .bind(3, "Created")
                    .bind(4, LocalDate.now())
                    .bind(5, LocalDate.now())
                    .bind(6, "save file success")
                    .bind(7, now)
                    .bind(8, now)
                    .bind(9,"Huỳnh Thị Thắm" )
                    .bind(10, "Huỳnh Thị Thắm")
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static int countLines(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            int lineCount = 0;
            String line;

            while ((line = br.readLine()) != null) {
                lineCount++;
            }

            return lineCount;
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Trả về -1 nếu có lỗi đọc tệp
        }
    }
    public  String findNewestFile(String folderPath, String name) throws IOException {
        Path folder = Paths.get(folderPath);
        String result = null;

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder)) {
            Path newestFile = null;

            for (Path path : directoryStream) {
                if (Files.isRegularFile(path) && path.getFileName().toString().contains(name)) {
                    if (newestFile == null || Files.getLastModifiedTime(path).compareTo(Files.getLastModifiedTime(newestFile)) > 0) {
                        newestFile = path;
                    }
                }
            }

            if (newestFile != null) {
                result = newestFile.toString();
            }
        }

        return result;
    }


    private boolean containsQuestionMark(String value) {
        return value.contains("?");
    }


    public static void main(String[] args) throws IOException {
//        insertControl("Extract Data","running","Start");
//        deleteControlById(1);
//        System.out.println(new ControlDAO().getPathFileData(LocalDate.of(2023, 11, 28)));
//        System.out.println(new ControlDAO().getConfigByName("https://xosohomnay.com.vn/"));
//      new ControlDAO().insertDataFile("data/xosohomnay.com.vn_3_12_20230.csv","test.com");
        System.out.println(new ControlDAO().findNewestFile("data","xosohomnay.com.vn_30_11_2023"));
    }
}
