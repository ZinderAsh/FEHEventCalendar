package net.zeathus.fehcalendar;

import java.util.ArrayList;

/**
 * Created by Zeathus on 2018-05-20.
 */
public class Event {

    private String category;
    private String name;
    private Date startDate;
    private Date endDate;
    private String orbs;

    public Event(String category, String name) {
        this.category = category;
        this.name = name;
        this.startDate = null;
        this.endDate = null;
        this.orbs = "";
    }

    public Event(String category, String name, Date date) {
        this.category = category;
        this.name = name;
        this.startDate = date;
        this.endDate = date;
        this.orbs = "";
    }

    public Event(String category, String name, Date startDate, Date endDate) {
        this.category = category;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.orbs = "";
    }

    public Event(String category, String name, Date startDate, Date endDate, String orbs) {
        this.category = category;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.orbs = orbs;
    }

    public void setOrbs(String orbs) { this.orbs = orbs; }

    public String getOrbs() { return orbs; }

    public int getTotalOrbs() {

        if (orbs.length() <= 0) {
            return 0;
        }

        int total = 0;

        String orbString = orbs.substring(0, orbs.length());

        while (orbString.contains(",")) {
            String count = orbString.substring(0, orbString.indexOf(","));
            total += Integer.parseInt(count);
            orbString = orbString.substring(orbString.indexOf(",") + 1);
        }

        if (orbString.length() > 0) {
            total += Integer.parseInt(orbString);
        }

        return total;
    }

    public int getOrbsAtDate(Date date) {
        if (getTotalOrbs() <= 0) {
            return 0;
        }

        if (date.compare(startDate) < 0) {
            return 0;
        }

        ArrayList<Integer> orbDays = new ArrayList<>();

        String orbString = orbs.substring(0, orbs.length());

        while (orbString.contains(",")) {
            String count = orbString.substring(0, orbString.indexOf(","));
            orbDays.add(Integer.parseInt(count));
            orbString = orbString.substring(orbString.indexOf(",") + 1);
        }

        if (orbString.length() > 0) {
            orbDays.add(Integer.parseInt(orbString));
        }

        int days = date.compare(startDate);

        if (orbDays.size() > days) {
            return orbDays.get(days);
        }

        return 0;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName(boolean widget) {
        if (!name.equals("NULL")) {
            if (!widget) {
                return category + "\n" + name;
            } else {
                return getShortCategory() + " " + name;
            }
        } else {
            return category;
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getCategory() {
        return category;
    }

    public String getShortCategory() {

        switch (category) {
            case "Bound Hero Battle": {
                return "BHB:";
            }
            case "Bound Hero Battle Revival": {
                return "BHB Revival:";
            }
            case "Grand Hero Battle": {
                return "GHB:";
            }
            case "Grand Hero Battle Revival": {
                return "GHB Revival:";
            }
            case "Log-In Bonus": {
                return "Bonus:";
            }
            case "Summoning Focus": {
                return "Focus:";
            }
        }

        return category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAtDate(Date date) {
        if (this.startDate.compare(date) == 0) {
            return true;
        }
        return false;
    }

    public void update(Date date) {};

    public int getIconId() {

        switch (category) {
            case "Monthly":
            case "Quests": {
                return R.drawable.icon_quest;
            }
            case "Forging Bonds": {
                return R.drawable.icon_bonds;
            }
            case "Grand Conquests": {
                return R.drawable.icon_spears;
            }
            case "Illusory Dungeon": {
                return R.drawable.icon_music;
            }
            case "Special Orb Promo":
            case "Daily Log-In Bonus":
            case "Log-In Bonus": {
                return R.drawable.icon_orb;
            }
            case "Summoning Focus": {
                return R.drawable.icon_star;
            }
            case "Tempest Trials": {
                return R.drawable.icon_marth;
            }
            case "Voting Gauntlet": {
                return R.drawable.icon_vote;
            }
            case "Bound Hero Battle":
            case "Bound Hero Battle Revival":
            case "Grand Hero Battle":
            case "Grand Hero Battle Revival":
            case "Legendary Hero Battle": {
                return R.drawable.icon_special;
            }
            case "Arena/Aether Season Start":
            case "Arena/Aether Season Rewards": {
                return R.drawable.icon_swords;
            }
        }

        return R.drawable.icon_default;
    }
}
