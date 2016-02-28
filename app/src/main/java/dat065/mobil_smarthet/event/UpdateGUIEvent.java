package dat065.mobil_smarthet.event;

/**
 * Created by elias on 2016-02-28.
 */
public class UpdateGUIEvent {
    int type;
    Object data;

    public UpdateGUIEvent(int type) {
        this(type,null);
    }
    public UpdateGUIEvent(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
