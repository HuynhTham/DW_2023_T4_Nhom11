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

public class CSVReader_Reward {
    public static void main(String[] args) {
        String csvFilePath_Dim = "D:\\datawarehouse\\staginToWareHouse\\staginToWareHouse\\reward_dim.csv";
        String tableName = "reward_dim";


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
            String specialPrize = record.get("special_prize");
            String eighthPrize = record.get("eighth_prize");
            String seventhPrize = record.get("seventh_prize");
            String sixthPrize = record.get("sixth_prize");
            String fifthPrize = record.get("fifth_prize");
            String fourthPrize = record.get("fourth_prize");
            String thirdPrize = record.get("third_prize");
            String secondPrize = record.get("second_prize");
            String firstPrize = record.get("first_prize");
            LocalDate date = LocalDate.parse(record.get("date"), dateFormatter);
            String type = record.get("type");

            insertReward(tableName, specialPrize, eighthPrize, seventhPrize, sixthPrize, fifthPrize,
                    fourthPrize, thirdPrize, secondPrize, firstPrize, date, type);
        }
    }

    private static void insertReward(String tableName, String specialPrize, String eighthPrize, String seventhPrize,
                                     String sixthPrize, String fifthPrize, String fourthPrize, String thirdPrize,
                                     String secondPrize, String firstPrize, LocalDate date, String type) {
        try (Handle handle = DBContext.connectWarehouse().open()) {

            // Insert data into the dynamically created table
            String query = "INSERT INTO " + tableName + "(`special_prize`, `eighth_prize`, `seventh_prize`, `sixth_prize`, " +
                    "`fifth_prize`, `fourth_prize`, `third_prize`, `second_prize`, `first_prize`, `date`, `type`) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            handle.createUpdate(query)
                    .bind(0, specialPrize)
                    .bind(1, eighthPrize)
                    .bind(2, seventhPrize)
                    .bind(3, sixthPrize)
                    .bind(4, fifthPrize)
                    .bind(5, fourthPrize)
                    .bind(6, thirdPrize)
                    .bind(7, secondPrize)
                    .bind(8, firstPrize)
                    .bind(9, java.sql.Date.valueOf(date))
                    .bind(10, type)
                    .execute();
        }
    }



}
