package warehouse.example;

import warehouse.dao.CSVReader;
import warehouse.dao.CSVReader_Reward;
import warehouse.dao.CSVResder_Province;
import warehouse.dao.datawarehouse.LotteryDAOWareHouse;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class Main {

    public static class StagingToWarehouse {
        private static final String FROM_EMAIL = "thienpham0712@gmail.com";
        private static final String PASSWORD = "thiennhan07282";
        private static final String TO_EMAIL = "20130410@st.hcmuaf.edu.vn";

        public void sendEmail(String subject, String body) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "*");

            Session sessionMail = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });

            MimeMessage message = new MimeMessage(sessionMail);

            try {
                message.setFrom(new InternetAddress(FROM_EMAIL, "Xổ số kiến thiết"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAIL));
                message.setSubject(subject, "UTF-8");
                message.setContent(body, "text/plain; charset=UTF-8");

                Transport.send(message);

                System.out.println("Sent message successfully!");

            } catch (MessagingException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        LotteryDAOWareHouse lottery =new LotteryDAOWareHouse();
        lottery.transferLotteryResultData();
    }
}
