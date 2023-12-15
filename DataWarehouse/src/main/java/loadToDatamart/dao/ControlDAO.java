package loadToDatamart.dao;

import loadToDatamart.entity.FileConfigs;
import org.jdbi.v3.core.Handle;

import java.io.File;
import java.io.IOException;
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
    public static void main(String[] args) throws IOException {
//        insertControl("Extract Data","running","Start");
//        deleteControlById(1);
//        System.out.println(new ControlDAO().getPathFileData(LocalDate.of(2023, 11, 28)));
//        System.out.println(new ControlDAO().getConfigByName("https://xosohomnay.com.vn/"));
//      new ControlDAO().insertDataFile("data/xosohomnay.com.vn_3_12_20230.csv","test.com");
        // Đường dẫn đến project của bạn
        String projectPath = "C:\\Users\\Asus\\Desktop\\warehouse_ck\\data\\xosohomnay.com.vn_12_12_20230.csv";


        // Tạo đối tượng File với đường dẫn hoàn chỉnh
        File file = new File(projectPath );

        // Kiểm tra xem file có tồn tại hay không
        if (file.exists()) {
            System.out.println("File tồn tại.");
        } else {
            System.out.println("File không tồn tại.");
        }
    }
}
