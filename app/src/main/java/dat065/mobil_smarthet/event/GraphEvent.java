package dat065.mobil_smarthet.event;

import java.util.ArrayList;

/**
 * Created by elias on 2016-03-07.
 */
public class GraphEvent {
    ArrayList[] data;
    int graphType;

    public GraphEvent(ArrayList[] data, int graphType) {
        this.data = data;
        this.graphType = graphType;
    }

    public ArrayList[] getData() {
        return data;
    }

    public int getGraphType() {
        return graphType;
    }
}

