package warehouse.dao;

import warehouse.entity.File_ConfigS;
import org.jdbi.v3.core.Handle;

import java.util.Optional;

public class ControlDAO {
    public Optional<File_ConfigS> getConfigByName(String source) {
        try (Handle handle = DBContext.connectControl().open()) {
            String query = "SELECT * FROM file_configs " +
                    "WHERE source_path = ?";
            return handle.createQuery(query)
                    .bind(0, source)
                    .mapToBean(File_ConfigS.class)
                    .findOne();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void main(String[] args) {

    }
}
