package net.zeathus.fehcalendar;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Season {

    private String primaryArena;
    private String secondaryArena;
    private String primaryAether;
    private String secondaryAether;

    public Season() {
        primaryArena = "?";
        secondaryArena = "?";
        primaryAether = "?";
        secondaryAether = "?";
    }

    public void setElements(String[] elements) {
        primaryArena = elements[0];
        secondaryArena = elements[1];
        primaryAether = elements[2];
        secondaryAether = elements[3];
    }

    public void setElements(String arena1, String arena2, String aether1, String aether2) {
        primaryArena = arena1;
        secondaryArena = arena2;
        primaryAether = aether1;
        secondaryAether = aether2;
    }

    public void setSeasonImages(FrameLayout layout) {

        ImageView arena1 = layout.findViewById(R.id.element1);
        ImageView arena2 = layout.findViewById(R.id.element2);
        ImageView aether1 = layout.findViewById(R.id.element3);
        ImageView aether2 = layout.findViewById(R.id.element4);

        if (primaryAether.equals("None") && secondaryAether.equals("None")) {
            aether1.setImageResource(getElementIconID(primaryArena));
            aether2.setImageResource(getElementIconID(secondaryArena));
            arena1.setImageResource(getElementIconID(primaryAether));
            arena2.setImageResource(getElementIconID(secondaryAether));
        } else {
            arena1.setImageResource(getElementIconID(primaryArena));
            arena2.setImageResource(getElementIconID(secondaryArena));
            aether1.setImageResource(getElementIconID(primaryAether));
            aether2.setImageResource(getElementIconID(secondaryAether));
        }

    }

    public void setSeasonImages(LinearLayout layout) {

        ImageView arena1 = layout.findViewById(R.id.element1);
        ImageView arena2 = layout.findViewById(R.id.element2);
        ImageView aether1 = layout.findViewById(R.id.element3);
        ImageView aether2 = layout.findViewById(R.id.element4);

        if (primaryAether.equals("None") && secondaryAether.equals("None")) {
            aether1.setImageResource(getElementIconID(primaryArena));
            aether2.setImageResource(getElementIconID(secondaryArena));
            arena1.setImageResource(getElementIconID(primaryAether));
            arena2.setImageResource(getElementIconID(secondaryAether));
        } else {
            arena1.setImageResource(getElementIconID(primaryArena));
            arena2.setImageResource(getElementIconID(secondaryArena));
            aether1.setImageResource(getElementIconID(primaryAether));
            aether2.setImageResource(getElementIconID(secondaryAether));
        }

    }

    private int getElementIconID(String element) {

        switch (element) {
            case "Fire":{
                return R.drawable.element_fire;
            }
            case "Water": {
                return R.drawable.element_water;
            }
            case "Earth": {
                return R.drawable.element_earth;
            }
            case "Wind": {
                return R.drawable.element_wind;
            }
            case "Light":{
                return R.drawable.element_light;
            }
            case "Dark": {
                return R.drawable.element_dark;
            }
            case "Astra": {
                return R.drawable.element_astra;
            }
            case "Anima": {
                return R.drawable.element_anima;
            }
            case "?": {
                return R.drawable.element_unknown;
            }
            case "None": {
                return R.drawable.element_none;
            }
        }

        return R.drawable.element_unknown;

    }

}
