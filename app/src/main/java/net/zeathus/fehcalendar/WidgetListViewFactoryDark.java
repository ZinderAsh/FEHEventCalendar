package net.zeathus.fehcalendar;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/**
 * Created by Zeathus on 2018-05-20.
 */
public class WidgetListViewFactoryDark implements RemoteViewsService.RemoteViewsFactory {

    private Context context = null;
    private int appWidgetId;

    private ArrayList<String> nameArray;
    private ArrayList<Integer> imageIDArray;
    private EventList eventList;

    public WidgetListViewFactoryDark(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        SharedPreferences mPrefs = context.getSharedPreferences("calendar",0);
        int dateOffset = mPrefs.getInt("date_offset",0);
        this.eventList = new EventList(context, true, dateOffset);
        loadEvents();
    }

    @Override
    public void onCreate() {

    }

    public void loadEvents() {
        if (FEHCalendarWidgetProviderDark.reload) {
            SharedPreferences mPrefs = context.getSharedPreferences("calendar",0);
            int dateOffset = mPrefs.getInt("date_offset",0);
            this.eventList = new EventList(context, true, dateOffset);
            FEHCalendarWidgetProviderDark.reload = false;
        }
        ArrayList<Event> events = eventList.getEventsAtDate(FEHCalendarWidgetProviderDark.date);
        nameArray = new ArrayList<>();
        imageIDArray = new ArrayList<>();
        for (Event event : events) {
            event.update(FEHCalendarWidgetProviderDark.date);
            nameArray.add(event.getDisplayName(true));
            imageIDArray.add(event.getIconId());
        }
    }

    @Override
    public void onDataSetChanged() {
        loadEvents();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return nameArray.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.widget_item_dark);

        row.setTextViewText(R.id.item_text, nameArray.get(position));
        row.setImageViewResource(R.id.item_icon, imageIDArray.get(position));
        row.setImageViewResource(R.id.item_icon2, imageIDArray.get(position));

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
