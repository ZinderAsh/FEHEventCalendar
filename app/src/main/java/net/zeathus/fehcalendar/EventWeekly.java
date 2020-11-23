package net.zeathus.fehcalendar;

/**
 * Created by Zeathus on 2018-05-20.
 */
public class EventWeekly extends Event {

    private int interval;

    public EventWeekly(String category, Date startDate, int interval) {
        super(category, "NULL", startDate);
        this.interval = interval;
    }

    public EventWeekly(String category, String name, Date startDate, int interval) {
        super(category, name, startDate);
        this.interval = interval;
    }

    public boolean isAtDate(Date date) {
        if (getStartDate().compare(date) % interval == 0) {
            return true;
        }
        return false;
    }
}
