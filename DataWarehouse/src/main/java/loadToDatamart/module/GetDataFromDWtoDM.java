package loadToDatamart.module;

import loadToDatamart.dao.ControlDAO;
import loadToDatamart.dao.DBContext;
import loadToDatamart.dao.LogDAO;
import loadToDatamart.dao.datamart.LotteryDAOMart;
import loadToDatamart.dao.datawarehouse.LotteryDAOWH;
import loadToDatamart.entity.DateDim;
import loadToDatamart.entity.LotteryResult;
import loadToDatamart.entity.ProvineDim;
import loadToDatamart.entity.RewardDim;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

public class GetDataFromDWtoDM {
    private static final String FROM_EMAIL = "20130115@st.hcmuaf.edu.vn";
    private static final String PASSWORD = "huynhtham3008!!";
    private static final String TO_EMAIL = "huynhtham3008@gmail.com";

    public static void sendEmail(String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "*");
        Session sessionMail = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        MimeMessage message = new MimeMessage(sessionMail);

        try {
            message.setFrom(new InternetAddress(FROM_EMAIL, " Xổ số kiến thiết"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAIL));
            message.setSubject(subject, "UTF-8");
            message.setContent(body, "text/plain; charset=UTF-8");

            Transport.send(message);

            System.out.println("Sent message successfully!");

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void loadDWToDM(String source) {
        LogDAO logDAO = new LogDAO();
        ControlDAO controlDAO = new ControlDAO();
        LotteryDAOWH lotteryWh = new LotteryDAOWH();
        LotteryDAOMart lotteryMart = new LotteryDAOMart();
        boolean isLoadDate = false;
        boolean isLoadProvince = false;
        boolean isLoadReward = false;
        boolean isLoadResult = false;
        /*
            1. Load những biến trong file mart.properties
            2. Kêt nối database control
            =>DBContext class
            3.Kiểm tra đã load data từ staging vào datawarehouse hay chưa
        */
        if (!logDAO.isLastLogStatusRunning("xosohomnay", "Load Data From Staging to Data Warehouse", "Success")) {
            /*
                3.1. Nếu chưa load =>Insert 1 dòng vào control.logs với status="can not run" and event_type="Load data to datamart"
            */
            logDAO.insertLog(source, "Load data to datamart", "can not run");
            /*
                3.2. Đóng kết nối database control
            */
            return;
        }
        /*
            4. Kiểm tra hôm nay đã load data từ warehouse vào data mart
        */
        if (logDAO.isLastLogStatusRunning("xosohomnay", "Load data to datamart", "Success")) {
            /*
                4.1. Nếu đã load rồi=> Insert 1 dòng vào control.logs với status="Loaded" and event_type="Load data to datamart"
            */
            logDAO.insertLog(source, "Load data to datamart", "Loaded");
           /*
                4.2. Đóng kết nối database control
           */
            return;
        }
        /*
            5. kết nối database datawarehouse.db
            6. Kết nối database datamart.db
            => DBContext Class
            7. Insert 1 dòng vào control.logs với status="Start" and event_type="Load data to datamart"
         */
        logDAO.insertLog(source, "Load data to datamart", "Start");
        /*
            8. Lấy tất cả dữ liệu có trong dw.date_dim
        */
        List<DateDim> listDate = lotteryWh.getAllDate();
        /*
            9. Kiểm tra dữ liệu lấy được từ dw.date_dim có null không !listDate.isEmpty()
        */
        if (listDate.isEmpty()) {
        /*
            9.1. Insert control.logs
        */
            logDAO.insertLog(source, "Load data to datamart with table date_dim", "data null");
        } else {
            /*
                10. Load vào datamart.date_temporary
             */
            for (DateDim res : listDate) {
                lotteryMart.insertDate(res.getId(), "date_temporary", res.getFull_date(), res.getDay_of_week(), res.getMonth(), res.getYear());
            }
            isLoadDate = true;

        }
        /*
            11. Lấy tất cả dữ liệu có trong dw.province_dim
        */
        List<ProvineDim> listProvince = lotteryWh.getAllProvince();
        /*
            12. Kiểm tra dữ liệu lấy được từ dw.date_dim có null không listProvince.isEmpty()
        */
        if (listProvince.isEmpty()) {
            /*
            12.1. Insert control.logs
             */
            logDAO.insertLog(source, "Load data to datamart with table province_dim", "data null");

        } else {
            /*
            13. Load dw.province_dim vào bảng tạm province_temporary
             */
            for (ProvineDim pro : listProvince) {
                lotteryMart.insertProvince(pro.getId(), "province_temporary", pro.getName(), pro.getRegion());
            }
            isLoadProvince = true;

        }
            /*
                14. Lấy tất cả dữ liệu có trong dw.reward_dim
            */
        List<RewardDim> listReward = lotteryWh.getAllReward();
        /*
            15. Kiểm tra dữ liệu lấy được từ dw.date_dim có null không listReward.isEmpty()
        */
        if (listReward.isEmpty()) {
            logDAO.insertLog(source, "Load data to datamart with table reward_dim", "data null");
            /*
                15.1. Insert control.logs
            */
        } else {
            /*
                16. Load dw.reward_dim vào bảng tạm reward_temporary
            */
            for (RewardDim reward : listReward) {
                lotteryMart.insertReward(reward.getId(), "reward_temporary", reward.getSpecial_prize(), reward.getFirst_prize(), reward.getSecond_prize(), reward.getThird_prize(), reward.getFourth_prize(), reward.getFifth_prize(), reward.getSixth_prize(), reward.getSeventh_prize(), reward.getEighth_prize(), reward.getDate(), reward.getType());
            }
            isLoadReward = true;
        }
        /*
            17.Cập nhật tất cả tên khóa ngoại của bảng lottery_result
         */
        lotteryMart.renameForeignKey("lottery_result", "lottery_result_reward", lotteryMart.getForeignKeyName("lottery_result", "reward"), "id_reward", "reward");
        lotteryMart.renameForeignKey("lottery_result", "lottery_result_date", lotteryMart.getForeignKeyName("lottery_result", "date"), "id_date", "date");
        lotteryMart.renameForeignKey("lottery_result", "lottery_result_province", lotteryMart.getForeignKeyName("lottery_result", "province"), "id_province", "province");
        /*
            18. Lấy tất cả dữ liệu có trong dw.lottery_result_fact
         */
        List<LotteryResult> listLottery = lotteryWh.getAllLottery();
        /*
        19. Kiểm tra dữ liệu lấy được từ dw.date_dim có null không listLottery.isEmpty()
         */
        if (!listLottery.isEmpty()) {
            /*
                20. Nếu không => Load dw.reward_dim vào bảng tạm lottery_result_temporary
            */
            for (LotteryResult res : listLottery) {
                lotteryMart.insertLottery("lottery_result_temporary", res.getId(), res.getId_reward(), res.getId_date(), res.getId_province(), res.getSpecial_prize(), res.getFirst_prize(), res.getSecond_prize(), res.getThird_prize(), res.getFourth_prize(), res.getFifth_prize(), res.getSixth_prize(), res.getSeventh_prize(), res.getEighth_prize());
            }

            isLoadResult = true;
            /*
                21. Rename datamart.date thành date_new
             */
            DBContext.renameTable("date", "date_new", "dbNameDataMart", "passwordDataMart");
           /*
                22. Rename bảng tạm date_temporary thành datamart.date
            */
            DBContext.renameTable("date_temporary", "date ", "dbNameDataMart", "passwordDataMart");
            /*
                23. Rename datamart.province_dim thành province_new
            */
            DBContext.renameTable("province", "province_new", "dbNameDataMart", "passwordDataMart");
            /*
                24. Rename bảng tạm province_temporary thành datamart.province
            */
            DBContext.renameTable("province_temporary", "province ", "dbNameDataMart", "passwordDataMart");
            /*
                25. Rename datamart.reward thành reward_new
             */
            DBContext.renameTable("reward", "reward_new", "dbNameDataMart", "passwordDataMart");
           /*
                26. Rename bảng tạm reward_temporary thành datamart.reward
            */
            DBContext.renameTable("reward_temporary", "reward ", "dbNameDataMart", "passwordDataMart");
            /*
                27. Rename  datamart.lottery_result thành datamart.lottery_result_new
            */
            DBContext.renameTable("lottery_result", "lottery_result_new", "dbNameDataMart", "passwordDataMart");
            /*
                28.   Rename  datamart.lottery_result_temporary thành datamart.lottery_result
            */
            DBContext.renameTable("lottery_result_temporary", "lottery_result ", "dbNameDataMart", "passwordDataMart");
            /*
                29. Xóa table datamart.lottery_result_new
             */
            lotteryMart.deleteTable("lottery_result_new");
            /*
                30. Xóa table datamart.date_new
             */
            lotteryMart.deleteTable("date_new");
            /*
                31. Xóa table datamart.province_new
             */
            lotteryMart.deleteTable("province_new");
            /*
                32. Xóa table datamart.reward_new
            */
            lotteryMart.deleteTable("reward_new");
            /*
                33.  Insert control.logs.status="Success"and control.logs.event_type=" Load data to datamart"
             */
            if (isLoadResult && isLoadDate && isLoadProvince && isLoadReward) {
                logDAO.insertLog(source, "Load data to datamart", "Success");
                sendEmail("Load data to datamart", "Success");
            } else {
                sendEmail("Load data to datamart", "Fail");
            }
        } else {
            logDAO.insertLog(source, "Load data to datamart with table lottery_result", "data null");
        }
        /*
            34. Đóng kết nối database datawarehouse.db
            35. Đóng kết nối database datamart.db
            36. Đóng kết nối database control.db
        */
    }

    public static void main(String[] args) {
        new GetDataFromDWtoDM().loadDWToDM("xosohomnay");

    }
}
