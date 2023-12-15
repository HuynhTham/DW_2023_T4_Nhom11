package warehouse.dao;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jdbi.v3.core.Handle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CSVResder_Province {
    public static void main(String[] args) {
//

        String csvFilePath_Dim_Province = "D:\\datawarehouse\\staginToWareHouse\\staginToWareHouse\\provine_dim.csv";  // Điều chỉnh đường dẫn CSV cho Province
        String tableName_Province = "province_dim";

        try (FileReader fileReaderProvince = new FileReader(new File(csvFilePath_Dim_Province));
             CSVParser csvParserProvince = new CSVParser(fileReaderProvince, CSVFormat.DEFAULT.withHeader())) {

            // Assuming your CSV file has headers
            readAndInsertData_Province(csvParserProvince.getRecords(), tableName_Province);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readAndInsertData_Province(List<CSVRecord> records, String tableName_province) {
        for (CSVRecord record : records) {
            String region = record.get("name");
            String name = record.get("region");  // Corrected field name

            insertProvince(tableName_province, name, region);
        }
    }

    private static void insertProvince(String tableName, String region, String name) {
        try (Handle handle = DBContext.connectWarehouse().open()) {

            // Insert data into the dynamically created table
            String query = "INSERT INTO " + tableName + "(`name`, `region`) VALUES (?, ?)";
            handle.createUpdate(query)
                    .bind(0, name)
                    .bind(1, region)
                    .execute();
        }
    }
}
