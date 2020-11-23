package net.zeathus.fehcalendar;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Zeathus on 2018-05-20.
 */

public class LegendaryListAdapter extends ArrayAdapter {

    private final Activity context;
    private ArrayList<String> months;
    private HashMap<String, ArrayList<Integer>> types;
    private HashMap<String, ArrayList<Integer>> elements;
    private HashMap<String, ArrayList<String>> names;

    public LegendaryListAdapter(Activity context, ArrayList<String> months,
                                ArrayList<String[]> heroInfo) {
        super(context, R.layout.legendary_list_item, months);
        this.context = context;
        this.months = months;

        loadInfo(heroInfo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.legendary_list_item, container, false);
        }

        TextView itemCount = convertView.findViewById(R.id.month_name);
        LinearLayout itemHeroes = convertView.findViewById(R.id.heroes);

        itemCount.setText(months.get(position));

        ArrayList<Integer> typeList = types.get(months.get(position));
        ArrayList<Integer> elementList = elements.get(months.get(position));
        ArrayList<String> nameList = names.get(months.get(position));

        itemHeroes.removeAllViews();

        for (int i = 0; i < nameList.size(); i++) {
            View child = context.getLayoutInflater().inflate(R.layout.legendary_list_list_item, null);

            ImageView itemElement = child.findViewById(R.id.hero_element);
            ImageView itemType = child.findViewById(R.id.hero_type);
            TextView itemName = child.findViewById(R.id.hero_name);

            if (typeList.size() >= i) { itemType.setImageResource(typeList.get(i)); }
            if (elementList.size() >= i) { itemElement.setImageResource(elementList.get(i)); }
            itemName.setText(nameList.get(i));

            itemHeroes.addView(child);
        }

        return convertView;
    }

    private void loadInfo(ArrayList<String[]> info) {
        types = new HashMap<>();
        elements = new HashMap<>();
        names = new HashMap<>();
        for (String[] hero : info) {
            if (!types.containsKey(hero[0])) {
                types.put(hero[0], new ArrayList<Integer>());
            }
            if (!elements.containsKey(hero[0])) {
                elements.put(hero[0], new ArrayList<Integer>());
            }
            if (!names.containsKey(hero[0])) {
                names.put(hero[0], new ArrayList<String>());
            }
            types.get(hero[0]).add(IconLib.getWeaponIcon(hero[1], hero[2]));
            elements.get(hero[0]).add(IconLib.getElementIcon(hero[3]));
            names.get(hero[0]).add(hero[4]);
            //Log.e("----ADDED ", hero[3] + " to " + hero[0]);
        }
    }
}