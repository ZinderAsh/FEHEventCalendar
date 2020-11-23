package net.zeathus.fehcalendar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by Zeathus on 2018-05-20.
 */
public class EventList {

    private ArrayList<Event> events;
    private ArrayList<Event> weekly;
    private final Context context;
    private int dateOffset;

    public EventList(Context context, boolean widget) {
        this.dateOffset = 0;
        this.context = context;
        addWeeklyEvents();
        if (widget) {
            getEventsFromWeb();
        } else {
            this.events = new ArrayList<>();
        }
    }

    public EventList(Context context, boolean widget, int dateOffset) {
        this.dateOffset = dateOffset;
        this.context = context;
        addWeeklyEvents();
        if (widget) {
            getEventsFromWeb();
        } else {
            this.events = new ArrayList<>();
        }
    }

    public int length() {
        return events.size();
    }

    public void addEvent(Event event) {
        if (dateOffset == 1) {
            event.getStartDate().nextDate();
            event.getEndDate().nextDate();
        } else if (dateOffset == -1) {
            event.getStartDate().prevDate();
            event.getEndDate().prevDate();
        }
        events.add(event);
    }

    public ArrayList<Event> getEventsAtDate(Date date) {
        ArrayList<Event> output = new ArrayList<Event>();
        for (Event event : events) {
            if (event.isAtDate(date)) {
                output.add(event);
            }
        }
        for (Event event : weekly) {
            if (event.isAtDate(date)) {
                output.add(event);
            }
        }
        return output;
    }

    public ArrayList<Event> getUpcomingAndOngoingEvents(Date date) {
        ArrayList<Event> output = new ArrayList<>();
        for (Event event : events) {
            if (event.getStartDate().compare(date) >= 0) {
                output.add(event);
            } else if (event.getStartDate().compare(date) <= 0 && event.getEndDate().compare(date) >= 0) {
                output.add(event);
            }
        }
        Date iDate = date.clone();
        for (int i = 0; i < 31; i++) {
            for (Event event : weekly) {
                if (event.getTotalOrbs() > 0) {
                    if (event.isAtDate(iDate)) {
                        Event newEvent = new Event(event.getCategory(), event.getName(), iDate.clone(), iDate.clone(), event.getOrbs());
                        output.add(newEvent);
                    }
                }
            }
            iDate.nextDate();
        }
        return output;
    }

    public ArrayList<Event> getUpcomingEvents(Date date) {
        ArrayList<Event> output = new ArrayList<>();
        for (Event event : events) {
            if (event.getStartDate().compare(date) >= 0) {
                output.add(event);
            }
        }
        Date iDate = date.clone();
        for (int i = 0; i < 31; i++) {
            for (Event event : weekly) {
                if (event.getTotalOrbs() > 0) {
                    if (event.isAtDate(iDate)) {
                        Event newEvent = new Event(event.getCategory(), event.getName(), iDate.clone(), iDate.clone(), event.getOrbs());
                        output.add(newEvent);
                    }
                }
            }
            iDate.nextDate();
        }
        return output;
    }

    public ArrayList<Event> getOngoingEvents(Date date) {
        ArrayList<Event> output = new ArrayList<>();
        for (Event event : events) {
            if (event.getStartDate().compare(date) <= 0 && event.getEndDate().compare(date) >= 0) {
                output.add(event);
            }
        }
        return output;
    }

    public ArrayList<Event> getRecentEvents(Date date) {
        ArrayList<Event> output = new ArrayList<>();
        for (Event event : events) {
            if (event.getEndDate().compare(date) <= 0) {
                output.add(event);
            }
        }
        return output;
    }

    public void getEventsFromWeb() {
        this.events = new ArrayList<>();
        new RetrieveTextTask().execute("");
    }

    public void addWeeklyEvents() {
        this.weekly = new ArrayList<>();

        // Weekly Updates
        EventWeekly arenaStart= new EventWeekly("Arena/Aether Season Start", new Date(14, 05, 2018), 7);
        EventWeekly arenaRewards = new EventWeekly("Arena/Aether Season Rewards", new Date(20, 05, 2018), 7);
        arenaStart.setOrbs("1");
        arenaRewards.setOrbs("4");
        weekly.add( arenaStart );
        weekly.add( arenaRewards );
        EventWeekly rivalDomains1 = new EventWeekly("Rival Domains", new Date(28, 9, 2018), 7);
        rivalDomains1.setOrbs("1");
        weekly.add( rivalDomains1 );

        EventWeekly dailyQuest1 = new EventWeekly("Daily Quest", new Date(9, 11, 2018), 7);
        EventWeekly dailyQuest2 = new EventWeekly("Daily Quest", new Date(10, 11, 2018), 7);
        EventWeekly dailyBonus = new EventWeekly("Daily Log-In Bonus", new Date(10, 11, 2018), 7);
        dailyQuest1.setOrbs("1");
        dailyQuest2.setOrbs("1");
        dailyBonus.setOrbs("1");
        weekly.add(dailyQuest1);
        weekly.add(dailyQuest2);
        weekly.add(dailyBonus);

        // Training Maps
        weekly.add( new EventWeekly("Training: Melee", new Date(14, 05, 2018), 5) );
        weekly.add( new EventWeekly("Training: Ranged", new Date(15, 05, 2018), 5) );
        weekly.add( new EventWeekly("Training: Bows", new Date(16, 05, 2018), 5) );
        weekly.add( new EventWeekly("Training: Magic", new Date(17, 05, 2018), 5) );
        weekly.add( new EventWeekly("Training: The Workout", new Date(18, 05, 2018), 5) );

        // Weekend Bonus
        weekly.add( new EventWeekly("Double EXP/SP", new Date(17, 05, 2018), 7) );
        weekly.add( new EventWeekly("Double EXP/SP", new Date(18, 05, 2018), 7) );

        if (dateOffset == 1) {
            for (Event event : weekly) {
                event.getStartDate().nextDate();
            }
        } else if (dateOffset == -1) {
            for (Event event : weekly) {
                event.getStartDate().prevDate();
            }
        }

        EventBlessedGardens gardens = new EventBlessedGardens();
        gardens.offsetDate(dateOffset);
        weekly.add(gardens);
    }

    private class RetrieveTextTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL textUrl = new URL("http://www.zeathus.net/data/fehcalendar/events.asp?short=1");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(textUrl.openStream(), StandardCharsets.UTF_8));
                String stringBuffer;
                String stringText = "";
                while ((stringBuffer = bufferedReader.readLine()) != null) {
                    stringText += stringBuffer;
                }
                bufferedReader.close();
                return stringText;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String string) {
            if (string != null) {
                ArrayList<String> lines = new ArrayList<String>();
                while (string.contains("<br>")) {
                    lines.add(string.substring(0, string.indexOf("<br>")));
                    string = string.substring(string.indexOf("<br>") + 4);
                }

                for (String line : lines) {
                    if (!line.contains("#")) {
                        String category = line.substring(0, line.indexOf(";"));
                        line = line.substring(line.indexOf(";") + 1);
                        String name = line.substring(0, line.indexOf(";"));
                        line = line.substring(line.indexOf(";") + 1);
                        String startDateString = line.substring(0, line.indexOf(";"));
                        line = line.substring(line.indexOf(";") + 1);
                        String endDateString = line.substring(0, line.indexOf(";"));
                        Date startDate = new Date(startDateString);
                        Date endDate = new Date(endDateString);
                        events.add(new Event(category, name, startDate, endDate));
                    }
                }

                if (dateOffset == 1) {
                    for (Event event : events) {
                        event.getStartDate().nextDate();
                    }
                } else if (dateOffset == -1) {
                    for (Event event : events) {
                        event.getStartDate().prevDate();
                    }
                }

                Intent intent = new Intent(context, FEHCalendarWidgetProvider.class);
                intent.setAction(FEHCalendarWidgetProvider.UPDATE_BROADCAST);
                context.sendBroadcast(intent);
            }
        }

    }

}
