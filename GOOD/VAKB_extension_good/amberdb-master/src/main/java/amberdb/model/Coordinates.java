package amberdb.model;

public class Coordinates {

    private String north;
    private String south;
    private String east;
    private String west;

    public Coordinates(String north, String south, String east, String west) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }

    public Coordinates() {
    }

    public String getNorth() {
        return north;
    }

    public void setNorth(String north) {
        this.north = north;
    }

    public String getSouth() {
        return south;
    }

    public void setSouth(String south) {
        this.south = south;
    }

    public String getEast() {
        return east;
    }

    public void setEast(String east) {
        this.east = east;
    }

    public String getWest() {
        return west;
    }

    public void setWest(String west) {
        this.west = west;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        if (north != null ? !north.equals(that.north) : that.north != null) return false;
        if (south != null ? !south.equals(that.south) : that.south != null) return false;
        if (east != null ? !east.equals(that.east) : that.east != null) return false;
        return !(west != null ? !west.equals(that.west) : that.west != null);

    }

    @Override
    public int hashCode() {
        int result = north != null ? north.hashCode() : 0;
        result = 31 * result + (south != null ? south.hashCode() : 0);
        result = 31 * result + (east != null ? east.hashCode() : 0);
        result = 31 * result + (west != null ? west.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "north: " + north + ", south: " + south
                + ", east: " + east + ", west: " + west;
    }
}
