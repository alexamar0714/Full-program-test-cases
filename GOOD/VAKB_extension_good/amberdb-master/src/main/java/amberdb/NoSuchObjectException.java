package amberdb;

public class NoSuchObjectException extends RuntimeException {

    private static final long serialVersionUID = 3967425361650030964L;

    public NoSuchObjectException(long objectId) {
        super(PIUtil.format(objectId));
    }

}
