package net.zeathus.fehcalendar;

/**
 * Created by Zeathus on 2018-05-20.
 */
public class Date {

    private int day;
    private int month;
    private int year;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    // Format "YYYY.MM.DD"
    public Date(String date) {
        if (date.contains("-")) {
            date = date.replace("-", ".");
        }
        if (date.indexOf(".") > 2) {
            // YYYY-MM-DD
            String year = date.substring(0, date.indexOf("."));
            String month = date.substring(date.indexOf(".") + 1, date.lastIndexOf("."));
            String day = date.substring(date.lastIndexOf(".") + 1);
            this.day = Integer.parseInt(day);
            this.month = Integer.parseInt(month);
            this.year = Integer.parseInt(year);
        } else {
            // DD-MM-YYYY
            String day = date.substring(0, date.indexOf("."));
            String month = date.substring(date.indexOf(".") + 1, date.lastIndexOf("."));
            String year = date.substring(date.lastIndexOf(".") + 1);
            this.day = Integer.parseInt(day);
            this.month = Integer.parseInt(month);
            this.year = Integer.parseInt(year);
        }
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getMonthDays() {
        int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (month == 2 && year % 4 == 0) {
            return 29;
        } else {
            return monthDays[month - 1];
        }
    }

    public void nextDate() {
        day++;
        if (day > getMonthDays()) {
            day = 1;
            month++;
            if (month > 12) {
                month = 1;
                year++;
            }
        }
    }

    public void prevDate() {
        day--;
        if (day <= 0) {
            month--;
            if (month <= 0) {
                year--;
                month = 12;
            }
            day = getMonthDays();
        }
    }

    public void nextMonth() {
        month++;
        if (month > 12) {
            month = 1;
            year++;
        }
    }

    public Date clone() {
        return new Date(day, month, year);
    }

    public int getTotalDays() {
        int days = day;
        int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        for (int i = 0; i < month - 1; i++) {
            days += monthDays[i];
        }
        days += year * 365;
        days += Math.floor(year / 4);
        return days;
    }

    /*
    Returns the difference in days
     */
    public int compare(Date date) {
        return this.getTotalDays() - date.getTotalDays();
    }

    public String getString() {
        return Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
    }

    public String getDayOfWeek() {
        int totalDays = getTotalDays();
        if (totalDays % 7 == 3) {
            return "Monday";
        } else if (totalDays % 7 == 4) {
            return "Tuesday";
        } else if (totalDays % 7 == 5) {
            return "Wednesday";
        } else if (totalDays % 7 == 6) {
            return "Thursday";
        } else if (totalDays % 7 == 0) {
            return "Friday";
        } else if (totalDays % 7 == 1) {
            return "Saturday";
        } else if (totalDays % 7 == 2) {
            return "Sunday";
        }
        return "";
    }

    public static String getMonthName(int month) {
        if (month == 0) {
            return "Month Name Error";
        }
        String[] months = {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "Desember"};
        return months[((month - 1) % 12)];
    }
}
