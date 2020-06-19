package py.gov.stp.tools;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 	@author DGTIC-STP
 *  @email  <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5f3b382b363c1f2c2b2f71383029712f26">[email protected]</a> 
 */
public class TableroUtils {

	private static Properties prop = null;
	
	public static Properties getProperties(String propFileName) throws IOException{
		if(prop == null){
			prop = new Properties();
			InputStream bif =  Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName);
			prop.load(bif);
			bif.close();
		}
        return prop;
	}
}