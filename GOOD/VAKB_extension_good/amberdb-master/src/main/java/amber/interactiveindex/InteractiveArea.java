package amber.interactiveindex;

import java.util.ArrayList;
import java.util.List;

public class InteractiveArea {
    private String objectId;
    private List<DirectionalArea> neighbours = new ArrayList<>();

    public InteractiveArea(){

    }

    public InteractiveArea(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public List<DirectionalArea> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(List<DirectionalArea> neighbours) {
        this.neighbours = neighbours;
    }

    public void addDirectionalArea(DirectionalArea directionalArea) {
        this.neighbours.add(directionalArea);
    }
}
