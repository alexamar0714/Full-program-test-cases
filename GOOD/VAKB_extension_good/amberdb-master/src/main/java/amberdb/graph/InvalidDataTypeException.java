package amberdb.graph;


@SuppressWarnings("serial")
public class InvalidDataTypeException extends RuntimeException {

    
    public InvalidDataTypeException() {
    }

    
    public InvalidDataTypeException(String message) {
        super(message);
    }

    
    public InvalidDataTypeException(Throwable cause) {
        super(cause);
    }

    
    public InvalidDataTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    
    public InvalidDataTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
