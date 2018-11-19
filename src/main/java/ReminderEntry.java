import java.time.LocalDate;

/**
 * Created by Таня on 13.11.2018.
 */
public class ReminderEntry {
    private LocalDate date;
    private String text = "";
    private Double count;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }
}
