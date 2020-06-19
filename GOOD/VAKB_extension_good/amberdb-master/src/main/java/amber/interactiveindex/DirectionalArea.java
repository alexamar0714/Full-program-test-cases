package amber.interactiveindex;

public class DirectionalArea {
    private String direction;
    private String objectId;

    public DirectionalArea(){

    }

    public DirectionalArea(String direction, String objectId) {
        this.direction = direction;
        this.objectId = objectId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
