package net.zeathus.fehcalendar;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Zeathus on 2018-05-20.
 */

public class UpcomingListAdapter extends ArrayAdapter {

    private final Activity context;
    private ArrayList<Integer> imageIDArray;
    private ArrayList<String> nameArray;
    private ArrayList<String> dateArray;
    private ArrayList<Integer> orbsArray;
    private ArrayList<String[]> seasonArray;

    public UpcomingListAdapter(Activity context, ArrayList<String> nameArray,
                             ArrayList<String> dateArray, ArrayList<Integer> imageIDArray,
                               ArrayList<Integer> orbsArray, ArrayList<String[]> seasonArray) {
        super(context, R.layout.upcoming_item, nameArray);

        this.context = context;
        this.imageIDArray = imageIDArray;
        this.nameArray = nameArray;
        this.dateArray = dateArray;
        this.orbsArray = orbsArray;
        this.seasonArray = seasonArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.upcoming_item, container, false);
        }

        TextView itemName = convertView.findViewById(R.id.name);
        ImageView itemIcon = convertView.findViewById(R.id.icon);
        ImageView itemIcon2 = convertView.findViewById(R.id.icon2);
        FrameLayout itemSeasonC1 = convertView.findViewById(R.id.season_container1);
        FrameLayout itemSeasonC2 = convertView.findViewById(R.id.season_container2);
        LinearLayout itemSeason1 = convertView.findViewById(R.id.season_icon1);
        LinearLayout itemSeason2 = convertView.findViewById(R.id.season_icon2);
        TextView itemDate = convertView.findViewById(R.id.date);

        if (nameArray.get(position).contains("{") && nameArray.get(position).contains("}")) {
            String weekText = nameArray.get(position);
            weekText = weekText.substring(weekText.indexOf("{") + 1, weekText.indexOf("}"));
            itemName.setText(nameArray.get(position).replace("{" + weekText + "}", ""));
            Season season = new Season();
            int arrayIndex = Integer.parseInt(weekText) - 1;
            if (arrayIndex < seasonArray.size()) {
                season.setElements(seasonArray.get(arrayIndex));
            } else {
                season.setElements("?", "?", "?", "?");
            }
            season.setSeasonImages(itemSeason1);
            season.setSeasonImages(itemSeason2);
            itemIcon.setVisibility(View.GONE);
            itemIcon2.setVisibility(View.GONE);
            itemSeasonC1.setVisibility(View.VISIBLE);
            itemSeasonC2.setVisibility(View.VISIBLE);
        } else {
            itemName.setText(nameArray.get(position));
            itemIcon.setImageResource(imageIDArray.get(position));
            itemIcon2.setImageResource(imageIDArray.get(position));
            itemIcon.setVisibility(View.VISIBLE);
            itemIcon2.setVisibility(View.VISIBLE);
            itemSeasonC1.setVisibility(View.GONE);
            itemSeasonC2.setVisibility(View.GONE);
        }

        itemDate.setText(dateArray.get(position));

        TextView itemOrbs = convertView.findViewById(R.id.orbs);
        if (orbsArray.get(position) > 0) {
            itemOrbs.setVisibility(View.VISIBLE);
            if (orbsArray.get(position) == 1) {
                itemOrbs.setText("Rewards " + orbsArray.get(position).toString() + " Orb");
            } else {
                itemOrbs.setText("Rewards " + orbsArray.get(position).toString() + " Orbs");
            }
        } else {
            itemOrbs.setVisibility(View.GONE);
            itemOrbs.setText("");
        }

        return convertView;
    }
}