package warehouse.dao.staging;

import warehouse.dao.DBContext;
import warehouse.entity.Lottery_Result;
import org.jdbi.v3.core.Handle;

import java.util.Collections;
import java.util.List;

public class LotteryResultDAOStaging {

    public List<Lottery_Result> getAllStagingData() {
        try (Handle handle = DBContext.connectStaging().open()) {
            String query = "SELECT * FROM `Lottery_result`";
            return handle.createQuery(query).mapToBean(Lottery_Result.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static void main(String[] args) {
        LotteryResultDAOStaging daoStaging = new LotteryResultDAOStaging();
        List<Lottery_Result> stagingData = daoStaging.getAllStagingData();

        for (Lottery_Result result : stagingData) {
            System.out.println(result); // In thông tin của mỗi dòng dữ liệu
        }
    }
}
