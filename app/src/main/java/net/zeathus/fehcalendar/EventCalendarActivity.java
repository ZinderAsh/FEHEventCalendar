package net.zeathus.fehcalendar;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class EventCalendarActivity extends AppCompatActivity {

    private boolean loaded = false;
    private int mode = -1; // 0 = Upcoming, 1 = Ongoing, 2 = Recent
    private int filter = -1; // 0 = None, 1 = Summoning Focus, 2 = Orbs Rewards, 3 = Quests
    private EventList eventlist;
    private int dateOffset = 0;
    private ArrayList<String[]> seasons;
    private ArrayList<String[]> legendary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_calendar);
        loadSettings();
        new EventCalendarActivity.RetrieveSeasonTask().execute("");
    }

    public void reloadAll() {
        this.recreate();
    }

    protected void onRestart() {
        super.onRestart();
        loadSettings();
        updateDisplay();
    }

    public void loadSettings() {
        SharedPreferences mPrefs = getSharedPreferences("calendar",0);
        dateOffset = mPrefs.getInt("date_offset", 0);
        if (!mPrefs.getBoolean("seen_info", false)) {
            infoDialog();
        }
    }

    public void infoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome to FEH Calendar!");
        builder.setMessage("This calendar's dates are in Pacific Standard Time. " +
                "If these dates do not match your timezone, click the settings button " +
                "at the bottom right of the app to shift events one day forward or backward.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                SharedPreferences mPrefs = getSharedPreferences("calendar", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putBoolean("seen_info", true).commit();
            }
        });
        builder.show();
    }

    public void openOptions(View view) {
        if (!loaded) {
            return;
        }
        String[] options = {"+1", "0", "-1", "Cancel"};

        String currentOption = "Currently: ";
        if (dateOffset == 0) {
            currentOption += "0";
        } else if (dateOffset == 1) {
            currentOption += "+1";
        } else if (dateOffset == -1) {
            currentOption += "-1";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a date offset.\n" +
            currentOption);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i <= 2) {
                    int newOffset = 0;
                    if (i == 0) {
                        newOffset = 1;
                    } else if (i == 1) {
                        newOffset = 0;
                    } else if (i == 2) {
                        newOffset = -1;
                    }
                    SharedPreferences mPrefs = getSharedPreferences("calendar", 0);
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putInt("date_offset", newOffset).commit();
                    loadSettings();
                    reloadAll();
                    //Toast.makeText(getApplicationContext(), "Restart the app to see changes.", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.show();
    }

    public void selectFilter(View view) {
        if (!loaded || mode >= 3) {
            return;
        }
        String[] options = {"None", "Summoning Focus", "Orb Rewards", "Quests", "Seasons"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a filter");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filter = i;
                ImageButton buttonFilter = (ImageButton) findViewById(R.id.button_filter);
                if (filter <= 0) {
                    buttonFilter.setImageResource(R.drawable.button_filter);
                } else {
                    buttonFilter.setImageResource(R.drawable.button_filter_on);
                }
                updateDisplay();
            }
        });
        builder.show();
    }

    public void updateDisplay() {
        if ( mode == 0 ) {
            addListItems(eventlist.getUpcomingEvents(getCurrentDate()));
        } else if ( mode == 1 ) {
            addListItems(eventlist.getOngoingEvents(getCurrentDate()));
        } else if ( mode == 2 ) {
            addListItems(eventlist.getRecentEvents(getCurrentDate()));
        }
    }

    public void selectDisplay(View view) {
        if (!loaded) {
            return;
        }

        String[] options = {"Upcoming", "Ongoing", "Recent", "Orb Calendar", "Legendary Banners"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose what to display");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toggle filter button
                ImageButton buttonFilter = findViewById(R.id.button_filter);
                if (i >= 3) {
                    buttonFilter.setImageResource(R.drawable.button_filter_off);
                } else {
                    if (filter <= 0) {
                        buttonFilter.setImageResource(R.drawable.button_filter);
                    } else {
                        buttonFilter.setImageResource(R.drawable.button_filter_on);
                    }
                }
                switch (i) {
                    case 0: {
                        displayUpcoming();
                        break;
                    }
                    case 1: {
                        displayOngoing();
                        break;
                    }
                    case 2: {
                        displayRecent();
                        break;
                    }
                    case 3: {
                        displayOrbs();
                        break;
                    }
                    case 4: {
                        displayLegendaryBanners();
                        break;
                    }
                }
            }
        });
        builder.show();
    }

    public void displayLegendaryBanners() {
        if (!loaded || mode == 4) {
            return;
        }
        ImageButton btn_display = findViewById(R.id.button_display);
        btn_display.setImageResource(R.drawable.button_legendary);
        mode = 4;

        Date date = getCurrentDate().clone();
        String dateString = String.format(Locale.US, "%d%02d",date.getYear(), date.getMonth());
        int year = date.getYear();
        ArrayList<String> monthArray = new ArrayList<>();
        ArrayList<String[]> heroInfoArray = new ArrayList<>();
        ArrayList<String> includedMonths = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            if (date.getMonth() + i > 12 && year == date.getYear()) {
                year++;
            }
            String monthString = Date.getMonthName(date.getMonth()) + " " + Integer.toString(year);
            monthArray.add(monthString);
            for (String[] hero : legendary) {
                // hero = {date, color, weapon, element, name}
                if (hero[0].equals(dateString)) {
                    String[] heroInfo = {monthString, hero[1], hero[2], hero[3], hero[4]};
                    includedMonths.add(monthString);
                    heroInfoArray.add(heroInfo);
                }
            }
            date.nextMonth();
            dateString = String.format(Locale.US, "%d%02d",date.getYear(), date.getMonth());
        }

        for (int i = 5; i >= 0; i--) {
            if (!includedMonths.contains(monthArray.get(i))) {
                monthArray.remove(i);
            }
        }

        LegendaryListAdapter adapter = new LegendaryListAdapter(this, monthArray, heroInfoArray);

        ListView list = findViewById(R.id.upcoming_list);
        list.setAdapter(adapter);
    }

    public void displayOrbs() {
        if (!loaded || mode == 3) {
            return;
        }
        ImageButton btn_display = findViewById(R.id.button_display);
        btn_display.setImageResource(R.drawable.button_orbs);
        mode = 3;

        Date date = getCurrentDate();
        ArrayList<Integer> orbCountArray = new ArrayList<>();
        ArrayList<String> dateArray = new ArrayList<>();
        ArrayList<String> eventsArray = new ArrayList<>();

        ArrayList<Event> events = eventlist.getUpcomingAndOngoingEvents(date);

        for (int i = 0; i < 40; i++) {
            int total = 0;
            String eventString = "";
            for (Event event : events) {
                int dayOrbs = event.getOrbsAtDate(date);
                if (dayOrbs > 0) {
                    total += dayOrbs;
                    if (eventString.length() > 0) {
                        eventString += "\n";
                    }
                    eventString += event.getDisplayName(true);
                    eventString += ": ";
                    eventString += Integer.toString(dayOrbs);
                }
            }
            if (total > 0) {
                orbCountArray.add(total);
                dateArray.add(getDateString(date));
                eventsArray.add(eventString);
            }
            date.nextDate();
        }

        OrbListAdapter adapter = new OrbListAdapter(this, orbCountArray, dateArray, eventsArray);

        ListView list = (ListView) findViewById(R.id.upcoming_list);
        list.setAdapter(adapter);
    }

    public void displayUpcoming() {
        if (!loaded || mode == 0) {
            return;
        }
        ImageButton btn_display = (ImageButton) findViewById(R.id.button_display);
        btn_display.setImageResource(R.drawable.button_upcoming);
        mode = 0;
        addListItems(eventlist.getUpcomingEvents(getCurrentDate()));
    }

    public void displayOngoing() {
        if (!loaded || mode == 1) {
            return;
        }
        ImageButton btn_display = (ImageButton) findViewById(R.id.button_display);
        btn_display.setImageResource(R.drawable.button_ongoing);
        mode = 1;
        addListItems(eventlist.getOngoingEvents(getCurrentDate()));
    }

    public void displayRecent() {
        if (!loaded || mode == 2) {
            return;
        }
        ImageButton btn_display = (ImageButton) findViewById(R.id.button_display);
        btn_display.setImageResource(R.drawable.button_recent);
        mode = 2;
        addListItems(eventlist.getRecentEvents(getCurrentDate()));
    }

    public void addListItems(ArrayList<Event> events) {

        //Button btn = (Button) findViewById(R.id.button_upcoming);

        if (mode == 0) {
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return lhs.getStartDate().compare(rhs.getStartDate());
                }
            });
        } else if (mode == 1) {
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return lhs.getEndDate().compare(rhs.getEndDate());
                }
            });
        } else if (mode == 2) {
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return rhs.getEndDate().compare(lhs.getEndDate());
                }
            });
        }

        if (filter > 0) {
            for (int i = events.size() - 1; i >= 0; i--) {
                if (filter == 1) {
                    // Summoning Focus
                    if (!events.get(i).getCategory().equals("Summoning Focus")) {
                        events.remove(events.get(i));
                    }
                } else if (filter == 2) {
                    // Orb Rewards
                    if (events.get(i).getTotalOrbs() <= 0) {
                        events.remove(events.get(i));
                    }
                } else if (filter == 3) {
                    // Quests
                    if (!(events.get(i).getCategory().equals("Quests") || events.get(i).getCategory().equals("Monthly"))) {
                        events.remove(events.get(i));
                    }
                } else if (filter == 4) {
                    // Seasons
                    if (!(events.get(i).getCategory().equals("Arena/Aether Season Start"))) {
                        events.remove(events.get(i));
                    }
                }
            }
        }

        ListView list = findViewById(R.id.upcoming_list);
        TextView loading = findViewById(R.id.loading);

        ArrayList<String> nameArray = new ArrayList<>();
        ArrayList<Integer> imageIDArray = new ArrayList<>();
        ArrayList<String> dateArray = new ArrayList<>();
        ArrayList<Integer> orbsArray = new ArrayList<>();

        for (Event event : events) {
            String eventName = event.getDisplayName(false);
            if (eventName.equals("Arena/Aether Season Start")) {
                eventName += "{" + Integer.toString(getWeek(event.getStartDate())) + "}";
            }
            nameArray.add(eventName);
            imageIDArray.add(event.getIconId());

            if (mode == 0) {
                dateArray.add(getDateString(event.getStartDate()));
            } else if (mode == 1) {
                dateArray.add("Ends " + getDateString(event.getEndDate()));
            } else if (mode == 2) {
                dateArray.add("Ended " + getDateString(event.getEndDate()));
            }

            if (mode <= 2) {
                orbsArray.add(event.getTotalOrbs());
            } else {
                orbsArray.add(0);
            }
        }

        UpcomingListAdapter adapter = new UpcomingListAdapter(this, nameArray, dateArray, imageIDArray, orbsArray, seasons);

        list.setAdapter(adapter);

        loading.setVisibility(View.GONE);
    }

    public String getDateString(Date date) {

        int days = date.compare(getCurrentDate());

        if (days >= -10 && days < -1) {
            return date.getDayOfWeek() + " " + Integer.toString(Math.abs(days)) + " days ago";
        } else if (days == -1) {
            return "Yesterday";
        } else if (days == 0) {
            return "Today";
        } else if (days == 1) {
            return "Tomorrow";
        } else if (days > 1 && days <= 10) {
            return date.getDayOfWeek() + " in " + Integer.toString(days) + " days";
        } else {
            return date.getDayOfWeek() + " " + date.getString();
        }
    }

    public Date getCurrentDate() {
        return new Date(Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.YEAR));
    }

    public int getCurrentWeek() {
        Date date = new Date(11, 02, 2019);
        if (dateOffset == 1) {
            date.nextDate();
        } else if (dateOffset == -1) {
            date.prevDate();
        }
        return (int) Math.floor(getCurrentDate().compare(date) / 7.0) + 64;
    }

    public int getWeek(Date date2) {
        Date date = new Date(11, 02, 2019);
        if (dateOffset == 1) {
            date.nextDate();
        } else if (dateOffset == -1) {
            date.prevDate();
        }
        return (int) Math.floor(date2.compare(date) / 7.0) + 64;
    }

    public void refresh(View v) {
        reloadAll();
    }

    private class RetrieveTextTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL textUrl = new URL("http://www.zeathus.net/data/fehcalendar/events.asp");
                //URL textUrl = new URL("http://www.zeathus.net/data/fehcalendar/events_debug.asp");
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

                eventlist = new EventList(getApplicationContext(), false, dateOffset);

                for (String line : lines) {
                    String category = line.substring(0, line.indexOf(";"));
                    line = line.substring(line.indexOf(";") + 1);
                    String name = line.substring(0, line.indexOf(";"));
                    line = line.substring(line.indexOf(";") + 1);
                    String startDateString = line.substring(0, line.indexOf(";"));
                    line = line.substring(line.indexOf(";") + 1);
                    String endDateString = line.substring(0, line.indexOf(";"));
                    Date startDate = new Date(startDateString);
                    Date endDate = new Date(endDateString);
                    line = line.substring(line.indexOf(";") + 1);
                    String orbs = line.substring(0, line.indexOf(";"));
                    eventlist.addEvent(new Event(category, name, startDate, endDate, orbs));
                }

                loaded = true;
                displayUpcoming();

            }
        }
    }

    private class RetrieveSeasonTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL textUrl = new URL("http://www.zeathus.net/data/fehcalendar/seasons.asp");
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

                seasons = new ArrayList<>();

                for (String line : lines) {

                    String[] elements = {"?", "?", "?", "?"};
                    int index = 0;

                    while (line.contains(";")) {
                        elements[index] = line.substring(0, line.indexOf(";"));
                        line = line.substring(line.indexOf(";") + 1);
                        index++;
                    }

                    seasons.add(elements);
                }

                Season season = new Season();
                season.setElements(seasons.get(getCurrentWeek() - 1));
                FrameLayout layout = findViewById(R.id.seasons);
                season.setSeasonImages(layout);

                new EventCalendarActivity.RetrieveLegendaryTask().execute("");

            }
        }
    }

    private class RetrieveLegendaryTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL textUrl = new URL("http://www.zeathus.net/data/fehcalendar/upcoming_legends.asp");
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
                ArrayList<String> lines = new ArrayList<>();
                while (string.contains("<br>")) {
                    lines.add(string.substring(0, string.indexOf("<br>")));
                    string = string.substring(string.indexOf("<br>") + 4);
                }

                legendary = new ArrayList<>();

                for (String line : lines) {
                    if (!line.contains("#")) {
                        String month = line.substring(0, line.indexOf(";"));
                        line = line.substring(line.indexOf(";") + 1);
                        String color = line.substring(0, line.indexOf(";"));
                        line = line.substring(line.indexOf(";") + 1);
                        String weapon = line.substring(0, line.indexOf(";"));
                        line = line.substring(line.indexOf(";") + 1);
                        String element = line.substring(0, line.indexOf(";"));
                        line = line.substring(line.indexOf(";") + 1);
                        String name = line.substring(0, line.indexOf(";"));
                        String[] hero = {month, color, weapon, element, name};
                        legendary.add(hero);
                    }
                }

                new EventCalendarActivity.RetrieveTextTask().execute("");
            }
        }

    }
}
