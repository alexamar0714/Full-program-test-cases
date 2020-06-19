package amberdb.util;


import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;


public class ExternalToolConverter {
    
    private static final Logger log = LoggerFactory.getLogger(ExternalToolConverter.class);

    protected Tika tika = new Tika();

    /**
     * Constructor.
     */
    public ExternalToolConverter() {
        super();
    }

    /**
     * Execute the command in a new operating system process.
     * @param cmd           the command along with the command line arguments to execute
     * @throws ExternalToolException if the command returned an exit code > 0
     */
    protected void executeCmd(String... cmd) throws ExternalToolException {
        // Log command
        log.debug("Run command: {}", StringUtils.join(cmd, ' '));
    
        // Execute command
        ProcessBuilder builder = new ProcessBuilder(cmd);
        builder.redirectError(Redirect.INHERIT).redirectOutput(Redirect.INHERIT);
        try {
            Process p = builder.start();
            p.waitFor();
            int exitVal = p.exitValue();
            if (exitVal > 0) {
                throw new ExternalToolException("Error in executeCmd", exitVal);
            }
        } catch (IOException | InterruptedException e) {
            throw new ExternalToolException("Error in executeCmd", e); 
        }
    }
    
    /**
     * An exception used to report the failure of the external processing task.
     */
    public static class ExternalToolException extends Exception {

        /** generated serial version id */
        static final long serialVersionUID = 555617641780262960L;

        public final int errorCode;
        
        /**
         * Constructor.
         * @param message       the error message
         * @param errorCode     the exit value returned by the external process 
         */
        public ExternalToolException(String message, int errorCode) {
            super(message);
            this.errorCode = errorCode;
        }
        
        /**
         * Constructor.
         * @param message       the error message
         * @param th            the exception that caused the external process to fail
         * @param errorCode     the exit value returned by the external process 
         */
        public ExternalToolException(String message, Throwable th) {
            super(message, th);
            this.errorCode = -1;
        }
    }

}