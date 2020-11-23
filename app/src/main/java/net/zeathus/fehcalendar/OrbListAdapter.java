package net.zeathus.fehcalendar;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Zeathus on 2018-05-20.
 */

public class OrbListAdapter extends ArrayAdapter {

    private final Activity context;
    private ArrayList<Integer> orbCountArray;
    private ArrayList<String> dateArray;
    private ArrayList<String> eventsArray;

    public OrbListAdapter(Activity context, ArrayList<Integer> orbCountArray,
                          ArrayList<String> dateArray,
                          ArrayList<String> eventsArray) {
        super(context, R.layout.orb_list_item, dateArray);

        this.context = context;
        this.orbCountArray = orbCountArray;
        this.dateArray = dateArray;
        this.eventsArray = eventsArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.orb_list_item, container, false);
        }

        TextView itemCount = convertView.findViewById(R.id.orb_count);
        TextView itemDate = convertView.findViewById(R.id.date);
        TextView itemEvents = convertView.findViewById(R.id.events);

        itemCount.setText(Integer.toString(orbCountArray.get(position)));
        itemDate.setText(dateArray.get(position));
        itemEvents.setText(eventsArray.get(position));


        return convertView;
    }
}