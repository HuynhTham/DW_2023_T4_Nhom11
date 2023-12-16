package extract.module;

import Extract.dao.ControlDAO;
import extract.dao.LogDAO;
import Extract.entity.FileConfigs;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class ExtractData {
    public boolean getDataFromDay(int day, int month, int year, String source, int stt, String location, String format, String column) {
        try {
            LocalDate dateLink = LocalDate.of(year, month, day);
            String formattedDate = dateLink.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            Document document;
            List<List<String>> csvData = new ArrayList<>();
//            if (LocalDate.of(year, month, day).isEqual(LocalDate.now())) {
            document = Jsoup.connect(source + "/ket-qua-xo-so/ngay-" + formattedDate).get();
            List<Element> tableElements = getTableElements(document, formattedDate);
            String region = "";
            String _day = "";
            String[] headers = column.split(",\\s*");
            csvData.add(0, Arrays.asList(headers));

            for (Element tableElement : tableElements) {
                Elements rowElements = tableElement.select("table .tblKQTinh");
                region = tableElement.select(".title a").text();
                _day = tableElement.select(".daymonth").text() + "/" + tableElement.select(".year").text();

                processRowElements(csvData, rowElements, _day, region, tableElement);
            }

            List<String> xsmbRow = new ArrayList<>();
            Element xsmb = tableElements.get(1);
            processXSMBRow(xsmbRow, xsmb);
            csvData.add(xsmbRow);

            try (FileOutputStream outputStream = new FileOutputStream(location + extractDomain(source) + "_" + day + "_" + month + "_" + year + stt + "." + format)) {
                Path filePath = Paths.get(location + extractDomain(source) + "_" + day + "_" + month + "_" + year + stt + "." + format);
                System.out.println(filePath.getFileName());
                writeCsv(outputStream, csvData);

            } catch (Exception exception) {
                exception.printStackTrace();

            }
        } catch (Exception e) {

            return false;

        }
        return true;
    }

    private List<Element> getTableElements(Document document, String formattedDate) {
        List<Element> tableElements = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            Element e = document.select("#kqxs_" + i + "-" + formattedDate).first();
            tableElements.add(e);
            System.out.println("#kqxs_" + i + "-" + formattedDate);
        }
        return tableElements;
    }

    private void processRowElements(List<List<String>> csvData, Elements rowElements, String _day, String region, Element tableElement) {
        for (Element rowElement : rowElements) {
            List<String> rowData = new ArrayList<>();
            rowData.add(_day);
            rowData.add(region);

            // Continue processing other columns...

            processColumnData(rowData, rowElement, tableElement);
            csvData.add(rowData);
        }
    }

    private void processColumnData(List<String> rowData, Element rowElement, Element tableElement) {
        // Process each column and add the data to the rowData list
        // Example:
        Element diaDiemElement = rowElement.select(".tentinh .namelong").first();
        String diaDiem = diaDiemElement.text();
        rowData.add(diaDiem);

        Element giaiTamElement = rowElement.select(".giai_tam .lq_1").first();
        rowData.add(giaiTamElement.attr("data"));
        rowData.add(tableElement.selectXpath("//*[@class='ten_giai_tam']").text());

        // Continue processing other columns...
        Element giaiBayElement = rowElement.select(".giai_bay .lq_1").first();
        rowData.add(giaiBayElement.attr("data"));
        rowData.add(tableElement.selectXpath("//*[@class='ten_giai_bay']").text());

        Elements giaiSauElements = rowElement.select(".giai_sau .dayso");
        StringBuilder giaiSau = new StringBuilder();
        for (Element giaiSauElement : giaiSauElements) {
            String giaiSauData = giaiSauElement.attr("data");
            giaiSau.append(giaiSauData).append(";");
        }
        rowData.add(giaiSau.toString().trim());

        rowData.add(tableElement.selectXpath("//*[@class='ten_giai_sau']").text());

        Element giaiNamElement = rowElement.select(".giai_nam .lq_1").first();
        String giaiNam = giaiNamElement.attr("data");
        rowData.add(giaiNam);

        rowData.add(tableElement.selectXpath("//*[@class='ten_giai_nam']").text());

        Elements giaiTuElements = rowElement.select(".giai_tu .dayso");
        StringBuilder giaiTu = new StringBuilder();
        for (Element giaiTuElement : giaiTuElements) {
            String giaiTuData = giaiTuElement.attr("data");
            giaiTu.append(giaiTuData).append(";");
        }
        rowData.add(giaiTu.toString().trim());
        rowData.add(tableElement.selectXpath("//*[@class='ten_giai_tu']").text());

        Elements giaiBaElements = rowElement.select(".giai_ba .dayso");
        StringBuilder giaiBa = new StringBuilder();
        for (Element giaiBaElement : giaiBaElements) {
            String giaiBaData = giaiBaElement.attr("data");
            giaiBa.append(giaiBaData).append(";");
        }
        rowData.add(giaiBa.toString().trim());
        rowData.add(tableElement.selectXpath("//*[@class='ten_giai_ba']").text());

        Elements giaiNhiElements = rowElement.select(".giai_nhi .dayso");
        String giaiNhi = giaiNhiElements.attr("data");
        rowData.add(giaiNhi);

        rowData.add(tableElement.selectXpath("//*[@class='ten_giai_nhi']").text());

        Elements giaiNhatElements = rowElement.select(".giai_nhat .dayso");
        String giaiNhat = giaiNhatElements.attr("data");
        rowData.add(giaiNhat);

        rowData.add(tableElement.selectXpath("//*[@class='ten_giai_nhat']").text());

        Elements giaiDacBietElements = rowElement.select(".giai_dac_biet .dayso");
        String giaiDacBietData = giaiDacBietElements.attr("data");
        rowData.add(giaiDacBietData);

        rowData.add(tableElement.selectXpath("//*[@class='ten_giai_dac_biet']").text());
    }


    private void processXSMBRow(List<String> xsmbRow, Element xsmb) {
        // Process XSMB row and add the data to the xsmbRow list
        // Example:
        xsmbRow.add(xsmb.select(".daymonth").text() + "/" + xsmb.select(".year").text());
        xsmbRow.add(xsmb.select(".title a").text());
        xsmbRow.add(xsmb.select(".phathanh a").text());
        xsmbRow.add("null");
        xsmbRow.add("null");

        processGiaiElements(xsmbRow, xsmb.selectXpath("//*[@class='giai_bay']//*[contains(@class,'lq_')]"), 5, "40N");
        processGiaiElements(xsmbRow, xsmb.selectXpath("//*[@class='giai_sau']//*[contains(@class,'lq_')]"), 7, "100N");
        processGiaiElements(xsmbRow, xsmb.selectXpath("//*[@class='giai_nam']//*[contains(@class,'lq_')]"), 9, "200N");
        processGiaiElements(xsmbRow, xsmb.selectXpath("//*[@class='giai_tu']//*[contains(@class,'lq_')]"), 11, "400N");
        processGiaiElements(xsmbRow, xsmb.selectXpath("//*[@class='giai_ba']//*[contains(@class,'lq_')]"), 13, "1TR");
        processGiaiElements(xsmbRow, xsmb.selectXpath("//*[@class='giai_nhi']//*[contains(@class,'lq_')]"), 15, "5TR");

        xsmbRow.add(xsmb.selectXpath("//*[@class='giai_nhat']//*[contains(@class,'lq_')]").text());
        xsmbRow.add("10TR");
        xsmbRow.add(xsmb.selectXpath("//*[@class='giai_dac_biet']//*[contains(@class,'lq_')]").text());
        xsmbRow.add("1 Tỷ");
    }

    private void processGiaiElements(List<String> xsmbRow, Elements giaiElements, int startIdx, String prizeName) {
        StringBuilder result = new StringBuilder();

        for (Element numberElement : giaiElements) {
            String prizeNumber = numberElement.text();
            result.append(prizeNumber).append("; ");
        }

        xsmbRow.add(result.toString().trim());
        xsmbRow.add(prizeName);
    }


    public static String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();

            // Check if the host is not null and starts with 'www.'
            if (host != null && host.startsWith("www.")) {
                // Remove 'www.' prefix
                host = host.substring(4);
            }

            return host;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null; // Handle the exception according to your requirements
        }
    }

    public static void writeCsv(OutputStream outputStream, List<List<String>> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            for (List<String> rowData : data) {
                csvPrinter.printRecord(rowData);
            }
        }
    }

    public static boolean deleteFileData(String path) {
        try {
            File fileToDelete = new File(path);
            System.out.println(fileToDelete.getAbsolutePath());

            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  // Print the exception details for debugging
        }
        return false;
    }


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
        Session sessionMail = Session.getInstance(props, new javax.mail.Authenticator() {
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

    public void extractData(String source) throws IOException {
        int i = 0;
        LocalDate dataDate = LocalDate.now();
        LogDAO logDAO = new LogDAO();
        ControlDAO controlDAO = new ControlDAO();
        LocalDate currentDate = LocalDate.now();
        LocalDate givenDate = dataDate;
        LocalTime currentTime = LocalTime.now(ZoneId.systemDefault());
        /*
            1. Load những biến trong file extract.properties
            2. Kêt nối database control
            =>DBContext class
            3.Kiểm tra xem giờ lấy dữ liệu từ nguồn có sau 17h hôm nay hoặc không phải là ngày của tương lai hay không
        */
        if ((givenDate.isEqual(currentDate) && currentTime.isBefore(LocalTime.of(17, 00))) || (givenDate.isAfter(currentDate))) {
            /*
                3.1. Nếu đúng 1 trong hai trường hợp trên=> insert 1 dòng vào table control.logs
            */
            logDAO.insertLog(source, "Get data from source", "Should be after 17h");
            /*
                3.2 Gửi mail thông báo
            */
            sendEmail("Extract data from" + source, "Should be after 17h");

        }
        /*
            4. Lấy đường dẫn của tất của file dữ liệu đã extracy ngày hôm qua
        */
        ArrayList<String> file_paths = (ArrayList<String>) controlDAO.getPathFileData(givenDate.minusDays(1));
        /*
            5. Kiểm tra xem ngày hôm nay đã extract data hay chưa
        */
        if (logDAO.isLastLogStatusRunning(source, "Get data from source", "Success")) {
            /*
                5.1. Nếu hôm nay đã Extract dữ liệu=> Insert vào control.logs
            */
            logDAO.insertLog(source, "Get data from source", "Data was got");
            /*
                5.2. Gửi mail thông báo
             */
            sendEmail("Extract data from" + source, "Today, Data was got");

        } else {
            for (String s : file_paths) {
                /*
                    6. Nếu hôm nay chưa Extract dữ liệu=> Xóa tất cả file csv đã lấy ngày trước đó
                */
                boolean isDelete = deleteFileData(s);
                /*
                    7. Kiểm tra đã xóa thành công hay chưa cho từng file
                */
                if (!isDelete) {
                    /*
                        7.1.Nếu thất bại => Insert control.logs với evenType="Remove data file csv: " + tên file" and status="Fail"
                    */
                    logDAO.insertLog(source, "Remove data file csv: " + s, "Fail");

                } else {
                     /*
                        8. Nếu thành công => Insert control.logs với evenType="Remove data file csv: " + tên file" and status="Success"
                    */
                    logDAO.insertLog(source, "Remove data file csv: " + s, "success");
                }
            }
            /*
                9. Lấy ra tất cả dữ liệu config của nguồn lấy dữ liệu (https://xosohomnay.com.vn/)
            */
            Optional<FileConfigs> fileConfigs = controlDAO.getConfigByName("https://xosohomnay.com.vn/");
            /*
                10. Insert vào table control.logsnvới status ="Start"nand evenType="Get data from source"
             */
            logDAO.insertLog(source, "Get data from source", "Start");
            /*
                11. Tiến hành vào nguồn lấy dữ liệu ngày hôm nay bằng thư viện  jsoup
                12. Kiểm tra  đã lấy dữ liệu thành công và lưu xuống file csv hay chưa
             */
            boolean isExtract = new ExtractData().getDataFromDay(givenDate.getDayOfMonth(), givenDate.getMonthValue(), givenDate.getYear(), "https://xosohomnay.com.vn/", i, fileConfigs.get().getLocation(), fileConfigs.get().getFormat(), fileConfigs.get().getColumns());
            if (isExtract) {
                /*
                    13. Nếu thành công=> Tìm file mới nhất trong thư mục được quy định trong bảng configs
                */
                String filepath = controlDAO.findNewestFile(fileConfigs.get().getLocation(), source + ".com.vn_" + givenDate.getDayOfMonth() + "_" + givenDate.getMonthValue() + "_" + givenDate.getYear());
                /*
                    14. Insert vào table data_files với tên file và đường dẫn file
                */
                controlDAO.insertDataFile(filepath, getFileNameAfterBackslash(filepath));
                /*
                    15. Insert vào table control.logs với status ="Sucess" and evenType="Get data from source"
                */
                logDAO.insertLog(source, "Get data from source", "Success");
                /*
                    16. Gửi mail thông báo extract dữ liệu thành công
                    17. Đóng kết nối database control
                */
                sendEmail("Extract data from" + source, "Success");
            } else {
                /*
                    12.1. Nếu Extract dữ liệu thất bại
                    12.2. Gửi mail thông báo quá tình lấy dữ liệu từ nguồn thất bại
                    12.3.Đóng kết nối database control
                 */
                logDAO.insertLog(source, "Get data from source", "Fail");
                sendEmail("Get data from sources", "fail");

            }
        }

    }

    public String getFileNameAfterBackslash(String filePath) {
        // Kiểm tra xem đường dẫn có chứa dấu "\" hay không
        int lastBackslashIndex = filePath.lastIndexOf("\\");

        if (lastBackslashIndex != -1 && lastBackslashIndex < filePath.length() - 1) {
            // Lấy phần sau dấu "\"
            return filePath.substring(lastBackslashIndex + 1);
        } else {
            // Trả về toàn bộ đường dẫn nếu không có dấu "\"
            return filePath;
        }
    }


}
