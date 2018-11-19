import java.util.ArrayList;
import java.util.List;

/**
 * Created by Таня on 13.11.2018.
 */
public class Reminder {
    private List<ReminderEntry> days;

    public Reminder() {
        days = new ArrayList<>();
    }

    public List<ReminderEntry> getDays() {
        return days;
    }

    public void setDays(List<ReminderEntry> days) {
        this.days = days;
    }
}
