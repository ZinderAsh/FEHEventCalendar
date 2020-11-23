package net.zeathus.fehcalendar;

/**
 * Created by Zeathus on 2018-05-20.
 */
public class EventBlessedGardens extends EventWeekly {

    private Date[] startDates = {
            new Date(5, 05, 2018),
            new Date(12, 05, 2018),
            new Date(19, 05, 2018),
            new Date(26, 05, 2018)
    };

    private String[] gardens = {"Water", "Wind", "Earth", "Fire"};

    private int[] icons = {
            R.drawable.icon_blessing_water,
            R.drawable.icon_blessing_wind,
            R.drawable.icon_blessing_earth,
            R.drawable.icon_blessing_fire
    };

    private String[] rewards = {"5 Orbs", "2,000 Feathers", "20 Divine Dew"};

    private int[] startRewards = {0, 1, 2, 0};

    private int iconId = R.drawable.icon_blessing_water;

    public EventBlessedGardens() {
        super("Blessed Gardens", "NULL", new Date(19, 05, 2018), 7);
    }

    public void offsetDate(int offset) {
        if (offset == 1) {
            startDates[0].nextDate();
            startDates[1].nextDate();
            startDates[2].nextDate();
            startDates[3].nextDate();
            this.getStartDate().nextDate();
        } else if (offset == -1) {
            startDates[0].prevDate();
            startDates[1].prevDate();
            startDates[2].prevDate();
            startDates[3].prevDate();
            this.getStartDate().prevDate();
        }
    }

    @Override
    public void update(Date date) {
        String name = "";
        for (int i = 0; i < startDates.length; i++) {
            if (startDates[i].compare(date) % 28 == 0) {
                int reward = startRewards[i];
                reward -= Math.floor(startDates[i].compare(date) / 28) - 1;
                while (reward > 2) {
                    reward -= 3;
                }
                while (reward < 0) {
                    reward += 3;
                }
                name += "\n";
                name += rewards[reward];
                iconId = icons[i];
                break;
            }
        }
        setName(name);
    }

    @Override
    public int getIconId() {
        return iconId;
    }

}
