package net.zeathus.fehcalendar;

public class IconLib {

    public static int getWeaponIcon(String color, String weapon) {
        switch(color) {
            case "Red": {
                switch(weapon) {
                    case "Sword": { return R.drawable.type_red_sword; }
                    case "Tome": { return R.drawable.type_red_tome; }
                    case "Breath": { return R.drawable.type_red_breath; }
                    case "Bow": { return R.drawable.type_red_bow; }
                    case "Dagger": { return R.drawable.type_red_dagger; }
                    case "Beast": { return R.drawable.type_red_beast; }
                }
                break;
            }
            case "Blue": {
                switch(weapon) {
                    case "Lance": { return R.drawable.type_blue_lance; }
                    case "Tome": { return R.drawable.type_blue_tome; }
                    case "Breath": { return R.drawable.type_blue_breath; }
                    case "Bow": { return R.drawable.type_blue_bow; }
                    case "Dagger": { return R.drawable.type_blue_dagger; }
                    case "Beast": { return R.drawable.type_blue_beast; }
                }
                break;
            }
            case "Green": {
                switch(weapon) {
                    case "Axe": { return R.drawable.type_green_axe; }
                    case "Tome": { return R.drawable.type_green_tome; }
                    case "Breath": { return R.drawable.type_green_breath; }
                    case "Bow": { return R.drawable.type_green_bow; }
                    case "Dagger": { return R.drawable.type_green_dagger; }
                    case "Beast": { return R.drawable.type_green_beast; }
                }
                break;
            }
            case "Colorless": {
                switch(weapon) {
                    case "Staff": { return R.drawable.type_colorless_staff; }
                    case "Breath": { return R.drawable.type_colorless_breath; }
                    case "Bow": { return R.drawable.type_colorless_bow; }
                    case "Dagger": { return R.drawable.type_colorless_dagger; }
                    case "Beast": { return R.drawable.type_colorless_beast; }
                }
                break;
            }
        }

        return R.drawable.type_unknown;
    }

    public static int getElementIcon(String element) {

        switch (element) {
            case "Fire": {
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
            case "Light": {
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
        }

        return R.drawable.element_unknown;
    }

}
