package net.zeathus.fehcalendar;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Zeathus on 2018-05-20.
 */
public class WidgetListAdapterServiceDark extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetListViewFactoryDark(this.getApplicationContext(), intent);
    }

}
