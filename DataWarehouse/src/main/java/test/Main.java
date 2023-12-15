package Test;
import loadToDatamart.module.GetDataFromDWtoDM;
import warehouse.dao.datawarehouse.LotteryDAOWareHouse;
import Extract.module.ExtractData;
import staging.Modules.Staging;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Main {
    private static boolean setUTF8Output() {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        new ExtractData().extractData("xosohomnay");
        if (!setUTF8Output()) {
            return;
        }
        Staging.run_process();

        try {
            LotteryDAOWareHouse lotteryDAOWareHouse = new LotteryDAOWareHouse();
            lotteryDAOWareHouse.transferLotteryResultData();

        } catch (Exception e) {
            e.printStackTrace();
        }
        new GetDataFromDWtoDM().loadDWToDM("xosohomnay");

    }
}