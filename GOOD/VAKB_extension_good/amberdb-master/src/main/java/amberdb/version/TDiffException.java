package amberdb.version;

@SuppressWarnings("serial")
public class TDiffException extends RuntimeException {

    public TDiffException() {
    }

    
    public TDiffException(String message) {
        super(message);
    }

    
    public TDiffException(Throwable cause) {
        super(cause);
    }

    
    public TDiffException(String message, Throwable cause) {
        super(message, cause);
    }

    
    public TDiffException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
