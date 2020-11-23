package net.zeathus.fehcalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Zeathus on 2018-05-19.
 */
public class FEHCalendarWidgetProviderDark extends AppWidgetProvider {

    private static final String NEXT_CLICKED = "fehCalendarWidgetNextClicked";
    private static final String PREV_CLICKED = "fehCalendarWidgetPrevClicked";
    private static final String DATE_CLICKED = "fehCalendarWidgetDateClicked";
    private static final String REFRESH_CLICKED = "fehCalendarWidgetRefreshClicked";
    private static final String ACTION_SCHEDULED_UPDATE = "fehCalendarWidgetUpdate";
    public static final String UPDATE_BROADCAST = "fehCalendarWidgetUpdate";

    public static Date date = new Date(Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
            Calendar.getInstance().get(Calendar.MONTH) + 1,
            Calendar.getInstance().get(Calendar.YEAR));
    public static boolean reload = false;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            setupButtons(context, appWidgetManager, appWidgetIds[i]);
            displayEvents(context, appWidgetManager, appWidgetIds[i]);
        }

        scheduleNextUpdate(context);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private static void scheduleNextUpdate(Context context) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Substitute AppWidget for whatever you named your AppWidgetProvider subclass
        Intent intent = new Intent(context, FEHCalendarWidgetProviderDark.class);
        intent.setAction(ACTION_SCHEDULED_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Get a calendar instance for midnight tomorrow.
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        // Schedule one second after midnight, to be sure we are in the right day next time this
        // method is called.  Otherwise, we risk calling onUpdate multiple times within a few
        // milliseconds
        midnight.set(Calendar.SECOND, 1);
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.add(Calendar.DAY_OF_YEAR, 1);

        // For API 19 and later, set may fire the intent a little later to save battery,
        // setExact ensures the intent goes off exactly at midnight.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), pendingIntent);
        }
    }

    public void setupButtons(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidgetdark);
        ComponentName widget = new ComponentName(context, FEHCalendarWidgetProviderDark.class);
        views.setOnClickPendingIntent(R.id.button_next, getPendingSelfIntent(context, NEXT_CLICKED));
        views.setOnClickPendingIntent(R.id.button_prev, getPendingSelfIntent(context, PREV_CLICKED));
        views.setOnClickPendingIntent(R.id.widget_text, getPendingSelfIntent(context, DATE_CLICKED));
        views.setOnClickPendingIntent(R.id.refresh_widget, getPendingSelfIntent(context, REFRESH_CLICKED));

        Intent intent = new Intent(context, EventCalendarActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.open_activity, pendingIntent);

        appWidgetManager.updateAppWidget(widget, views);
    }

    public void displayEvents(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidgetdark);
        Intent intent = new Intent(context, WidgetListAdapterServiceDark.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        views.setRemoteAdapter(R.id.list_events, intent);
        views.setTextViewText(R.id.widget_text, getDateString());
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_SCHEDULED_UPDATE.equals(intent.getAction())) {
            date = getCurrentDate();
            updateList(context, intent);
            scheduleNextUpdate(context);

        } else if (NEXT_CLICKED.equals(intent.getAction())) {
            date.nextDate();
            updateList(context, intent);

        } else if (PREV_CLICKED.equals(intent.getAction())) {
            date.prevDate();
            updateList(context, intent);

        } else if (DATE_CLICKED.equals(intent.getAction())) {
            date = getCurrentDate();
            updateList(context, intent);

        } else if (UPDATE_BROADCAST.equals(intent.getAction())) {
            date = getCurrentDate();
            updateList(context, intent);

        } else if (REFRESH_CLICKED.equals(intent.getAction())) {
            date = getCurrentDate();
            reload = true;
            updateList(context, intent);

        } else if (intent.getAction().equals("com.sec.android.widgetapp.APPWIDGET_RESIZE")) {
            handleLauncherRestart(context, intent);

        } else if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE_OPTIONS")) {
            handleLauncherRestart(context, intent);

        }

        super.onReceive(context, intent);
    }

    public void handleLauncherRestart(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        setupButtons(context, appWidgetManager, appWidgetId);
        displayEvents(context, appWidgetManager, appWidgetId);
    }

    public void updateList(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews views;
        ComponentName widget;

        views = new RemoteViews(context.getPackageName(), R.layout.appwidgetdark);
        widget = new ComponentName(context, FEHCalendarWidgetProviderDark.class);

        views.setTextViewText(R.id.widget_text, getDateString());

        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(widget);

        appWidgetManager.updateAppWidget(widget, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_events);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onAppWidgetChanged(Context context, AppWidgetManager appWidgetManager,
                                   int appWidgetId, Bundle newOptions) {
        displayEvents(context, appWidgetManager, appWidgetId);
    }

    public void onDeleted(Context context, int[] ints) {

    }

    public void onEnabled(Context context) {

    }

    public void onDisabled(Context context) {

    }

    public String getDateString() {
        Date current = getCurrentDate();
        if (date.compare(current) == 0) {
            return "Today" + "\n" + date.getString();
        } else if (date.compare(current) == -1) {
            return "Yesterday" + "\n" + date.getString();
        } else if (date.compare(current) == 1) {
            return "Tomorrow" + "\n" + date.getString();
        } else {
            return date.getDayOfWeek() + "\n" + date.getString();
        }
    }

    public Date getCurrentDate() {
        return new Date(Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.YEAR));
    }

}
