package net.zeathus.fehcalendar;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by Zeathus on 2018-05-20.
 */
public class WidgetListAdapterService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetListViewFactory(this.getApplicationContext(), intent);
    }

}
