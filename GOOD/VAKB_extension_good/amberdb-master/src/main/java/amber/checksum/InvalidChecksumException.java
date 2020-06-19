package amber.checksum;

public class InvalidChecksumException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InvalidChecksumException(String message){
        super(message);
    }
}
