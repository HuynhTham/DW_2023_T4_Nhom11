package loadToDatamart.module;

import loadToDatamart.dao.*;

import loadToDatamart.dao.datamart.LotteryDAOMart;
import loadToDatamart.dao.datawarehouse.LotteryDAOWH;
import loadToDatamart.entity.*;

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

        //kiem tra da load du lieu vao dw Success hay không
        if (!logDAO.isLastLogStatusRunning("xosohomnay", "Get data from file to Staging", "Success")) {
            logDAO.insertLog(source, "Load data to datamart", "can not run");
            return;
        }
        if (logDAO.isLastLogStatusRunning("xosohomnay", "Load data to datamart", "Success")) {
            logDAO.insertLog(source, "Load data to datamart", "Loaded");
            return;
        }
        logDAO.insertLog(source, "Load data to datamart", "Start");

        //load dữ liệu từ datawarehouse database
        List<DateDim> listDate = lotteryWh.getAllDate();
        if (!listDate.isEmpty()) {
            //insert vào datamart
            for (DateDim res : listDate) {
                lotteryMart.insertDate(res.getId(), "date_temporary", res.getFull_date(), res.getDay_of_week(), res.getMonth(), res.getYear());
            }
            isLoadDate = true;

        } else {
            logDAO.insertLog(source, "Load data to datamart with table date_dim", "data null");
        }
        List<ProvineDim> listProvince = lotteryWh.getAllProvince();
        if (!listProvince.isEmpty()) {
            for (ProvineDim pro : listProvince) {
                lotteryMart.insertProvince(pro.getId(), "province_temporary", pro.getName(), pro.getRegion());
            }
            isLoadProvince = true;

        } else {
            logDAO.insertLog(source, "Load data to datamart with table province_dim", "data null");
        }

        List<RewardDim> listReward = lotteryWh.getAllReward();
        if (!listReward.isEmpty()) {
            for (RewardDim reward : listReward) {
                lotteryMart.insertReward(reward.getId(), "reward_temporary", reward.getSpecial_prize(), reward.getFirst_prize(), reward.getSecond_prize(), reward.getThird_prize(), reward.getFourth_prize(), reward.getFifth_prize(), reward.getSixth_prize(), reward.getSeventh_prize(), reward.getEighth_prize(), reward.getDate(), reward.getType());
            }
            isLoadReward = true;


        } else {
            logDAO.insertLog(source, "Load data to datamart with table reward_dim", "data null");
        }
        lotteryMart.renameForeignKey("lottery_result", "lottery_result_reward", lotteryMart.getForeignKeyName("lottery_result", "reward"), "id_reward", "reward");
        lotteryMart.renameForeignKey("lottery_result", "lottery_result_date", lotteryMart.getForeignKeyName("lottery_result", "date"), "id_date", "date");
        lotteryMart.renameForeignKey("lottery_result", "lottery_result_province", lotteryMart.getForeignKeyName("lottery_result", "province"), "id_province", "province");

        //load dữ liệu từ datawarehouse database
        List<LotteryResult> listLottery = lotteryWh.getAllLottery();

        if (!listLottery.isEmpty()) {


            for (LotteryResult res : listLottery) {
//

                lotteryMart.insertLottery("lottery_result_temporary", res.getId(), res.getId_reward(), res.getId_date(), res.getId_province(), res.getSpecial_prize(), res.getFirst_prize(), res.getSecond_prize(), res.getThird_prize(), res.getFourth_prize(), res.getFifth_prize(), res.getSixth_prize(), res.getSeventh_prize(), res.getEighth_prize());
            }

            isLoadResult = true;
            DBContext.renameTable("date", "date_new", "dbNameDataMart", "passwordDataMart");
            DBContext.renameTable("date_temporary", "date ", "dbNameDataMart", "passwordDataMart");

            DBContext.renameTable("province", "province_new", "dbNameDataMart", "passwordDataMart");
            DBContext.renameTable("province_temporary", "province ", "dbNameDataMart", "passwordDataMart");

            DBContext.renameTable("reward", "reward_new", "dbNameDataMart", "passwordDataMart");
            DBContext.renameTable("reward_temporary", "reward ", "dbNameDataMart", "passwordDataMart");


            DBContext.renameTable("lottery_result", "lottery_result_new", "dbNameDataMart", "passwordDataMart");
            DBContext.renameTable("lottery_result_temporary", "lottery_result ", "dbNameDataMart", "passwordDataMart");


            lotteryMart.deleteTable("lottery_result_new");
            lotteryMart.deleteTable("date_new");
            lotteryMart.deleteTable("province_new");
            lotteryMart.deleteTable("reward_new");
            if (isLoadResult && isLoadDate && isLoadProvince && isLoadReward) {
                logDAO.insertLog(source, "Load data to datamart", "Success");
                sendEmail("Load data to datamart", "Success");
            } else {
                sendEmail("Load data to datamart", "Fail");
            }
        } else {
            logDAO.insertLog(source, "Load data to datamart with table lottery_result", "data null");
        }

    }

    public static void main(String[] args) {
        new GetDataFromDWtoDM().loadDWToDM("xosohomnay");

    }
}
