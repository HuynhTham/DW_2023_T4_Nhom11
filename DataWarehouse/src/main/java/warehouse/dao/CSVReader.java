package warehouse.dao;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.jdbi.v3.core.Handle;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jdbi.v3.core.Handle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CSVReader {
    public static void main(String[] args) {
        String csvFilePath_Dim = "D:\\datawarehouse\\staginToWareHouse\\staginToWareHouse\\date_dim.csv";
        String tableName = "date_dim";


        try (FileReader fileReader = new FileReader(new File(csvFilePath_Dim));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withHeader())) {

            // Assuming your CSV file has headers
            readAndInsertData(csvParser.getRecords(), tableName);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void readAndInsertData(Iterable<CSVRecord> records, String tableName) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");

        for (CSVRecord record : records) {
            LocalDate fullDate = LocalDate.parse(record.get("full_date"), dateFormatter);
            String dayOfWeek = record.get("day_of_week");
            String month = record.get("month");
            String year = record.get("year");

            insertDate(tableName, fullDate, dayOfWeek, month, year);
        }
    }


    private static void insertDate(String tableName, LocalDate fullDate, String dayOfWeek, String month, String year) {
        try (Handle handle = DBContext.connectWarehouse().open()) {

            // Insert data into the dynamically created table
            String query = "INSERT INTO " + tableName + "(`full_date`, `day_of_week`, `month`, `year`) VALUES (?,?,?,?)";
            handle.createUpdate(query)
                    .bind(0, java.sql.Date.valueOf(fullDate))
                    .bind(1, dayOfWeek)
                    .bind(2, month)
                    .bind(3, year)
                    .execute();
        }
    }



}
