package amberdb;

import amberdb.enums.CopyRole;

public class NoSuchCopyException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 2072958966670305442L;
    
    public NoSuchCopyException(long objId, CopyRole copyRole) {
        super("object id: " + objId + ", copy role: " + copyRole.name());
    }
}
