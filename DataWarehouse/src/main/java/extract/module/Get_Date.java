package Extract.module;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Months;
import org.joda.time.Period;

public class Get_Date {
    public static final String OUT_FILE = "new_date_dim.csv";
    public static final int NUMBER_OF_RECORD = 7670;
    public static final String TIME_ZONE = "PST8PDT";

    public static void main(String[] args) {
        DateTimeZone dateTimeZone = DateTimeZone.forID(TIME_ZONE);
        int count = 0;
        int date_sk = 0;
        int month_since_2023 = 1;
        int day_since_2023 = 0;
        PrintWriter pr = null;

        try {
            File file = new File(OUT_FILE);
            if (file.exists()) {
                file.delete();
            }
            pr = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DateTime startDateTime = new DateTime(2022, 12, 31, 0, 0, 0); // Năm 2013
        DateTime startDateTimeforMonth = startDateTime.plus(Period.days(1));

        while (count <= NUMBER_OF_RECORD) {
            startDateTime = startDateTime.plus(Period.days(1));
            java.util.Date startDate = startDateTime.toDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

//            date_sk += 1;
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            String full_date = dt.format(calendar.getTime());

            day_since_2023 += 1;
            int month_since_2023_temp = Months.monthsBetween(
                    startDateTimeforMonth.toLocalDate(),
                    startDateTime.toLocalDate()).getMonths();

            month_since_2023 = month_since_2023_temp + 1;

            String day_of_week = calendar.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.LONG, Locale.US);

            String calendar_month = calendar.getDisplayName(Calendar.MONTH,
                    Calendar.LONG, Locale.US);

            dt = new SimpleDateFormat("yyyy");
            String calendar_year = dt.format(calendar.getTime());
            String calendar_month_short = calendar.getDisplayName(
                    Calendar.MONTH, Calendar.SHORT, Locale.US);

            String calendar_year_month = calendar_year + "-"
                    + calendar_month_short;

            int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);
            int day_of_year = calendar.get(Calendar.DAY_OF_YEAR);

            Calendar calendar_temp = calendar;

            int week_of_year_sunday = lastDayOfLastWeek(calendar_temp).get(
                    Calendar.WEEK_OF_YEAR);

            int year_sunday = lastDayOfLastWeek(calendar).get(Calendar.YEAR);

            String year_week_sunday = "";

            if (week_of_year_sunday < 10) {
                year_week_sunday = year_sunday + "-" + "W0"
                        + week_of_year_sunday;
            } else {
                year_week_sunday = year_sunday + "-" + "W"
                        + week_of_year_sunday;
            }

            calendar_temp = Calendar.getInstance(Locale.US);
            calendar_temp.setTime(calendar.getTime());
            calendar_temp.set(Calendar.DAY_OF_WEEK,
                    calendar_temp.getFirstDayOfWeek());
            dt = new SimpleDateFormat("yyyy-MM-dd");
            String week_sunday_start = dt.format(calendar_temp.getTime());

            DateTime startOfWeek = startDateTime.weekOfWeekyear()
                    .roundFloorCopy();

            int week_of_year_monday = startOfWeek.getWeekOfWeekyear();

            dt = new SimpleDateFormat("yyyy");
            int year_week_monday_temp = startOfWeek.getYear();

            String year_week_monday = "";

            if (week_of_year_monday < 10) {
                year_week_monday = year_week_monday_temp + "-W0"
                        + week_of_year_monday;
            } else {
                year_week_monday = year_week_monday_temp + "-W"
                        + week_of_year_monday;
            }

            dt = new SimpleDateFormat("yyyy-MM-dd");
            String week_monday_start = dt.format(startOfWeek.toDate());
//
//            String outputs = date_sk + "," + full_date + "," + day_since_2023
//                    + "," + month_since_2023 + "," + day_of_week + ","
//                    + calendar_month + "," + calendar_year + ","
//                    + calendar_year_month + "," + day_of_month + ","
//                    + day_of_year + "," + week_of_year_sunday + ","
//                    + year_week_sunday + "," + week_sunday_start + ","
//                    + week_of_year_monday + "," + year_week_monday + ","
//                    + week_monday_start + "," + "Non-Holiday" + ","
//                    + isWeekend(day_of_week);
            String output =  full_date + "," + day_of_week + ","
                    + calendar_month + "," + calendar_year ;

            count++;

            pr.println(output);
            pr.flush();
        }
    }

    public static String getWeekOfYearSunday(Calendar calendar) {
        java.util.Date date = getFirstDayOfWeekDate(calendar);
        Calendar newCalendar = Calendar.getInstance(Locale.US);
        newCalendar.setTime(date);
        int result = newCalendar.getWeeksInWeekYear();
        return "" + result;
    }

    public static String getFirstDayOfWeekString(Calendar calendar) {
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date now = calendar.getTime();
        java.util.Date temp = new java.util.Date(now.getTime() - 24 * 60 * 60 * 1000 * (week - 1));
        String result = dt.format(temp);
        return result;
    }

    public static java.util.Date getFirstDayOfWeekDate(Calendar calendar) {
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        java.util.Date now = calendar.getTime();
        java.util.Date temp = new java.util.Date(now.getTime() - 24 * 60 * 60 * 1000 * (week - 1));
        return temp;
    }

    public static Calendar getDateOfMondayInCurrentWeek(Calendar c) {
        c.setFirstDayOfWeek(Calendar.MONDAY);
        int today = c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DAY_OF_WEEK, -today + Calendar.MONDAY);
        return c;
    }

    public static Calendar firstDayOfLastWeek(Calendar c) {
        c = (Calendar) c.clone();
        c.add(Calendar.WEEK_OF_YEAR, -1);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c;
    }

    public static Calendar lastDayOfLastWeek(Calendar c) {
        c = (Calendar) c.clone();
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c;
    }

    public static String isWeekend(String day) {
        if (day.equalsIgnoreCase("Saturday") || day.equalsIgnoreCase("Sunday")) {
            return "Weekend";
        } else {
            return "Weekday";
        }
    }

    public static String getQuarter(int month) {
        int quarter = month % 3 == 0 ? (month / 3) : (month / 3) + 1;
        String result = "Q" + quarter;
        return result;
    }
}
