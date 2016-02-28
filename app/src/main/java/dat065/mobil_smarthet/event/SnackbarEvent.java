package dat065.mobil_smarthet.event;

import android.support.design.widget.Snackbar;

/**
 * Created by elias on 2016-02-28.
 */
public class SnackbarEvent {
    String text;
    int duration;

    public SnackbarEvent(String text, int duration) {
        if(duration != Snackbar.LENGTH_LONG && duration != Snackbar.LENGTH_SHORT)
            this.duration = Snackbar.LENGTH_SHORT;
        else
            this.duration = duration;
        this.text = text;
    }

    public SnackbarEvent(String text) {
        this(text,Snackbar.LENGTH_SHORT);
    }

    public int getDuration() {
        return duration;
    }

    public String getText() {
        return text;
    }
}
