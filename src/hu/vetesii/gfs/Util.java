package hu.vetesii.gfs;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
	static final Logger LOG = LoggerFactory.getLogger(Util.class);
	
	/**
	 * Can handle both HTTP request and HTTP response (uses HTTPMessage interface as parameter)
	 * @param httpMessage
	 */
	public static void logHeaderToDebug(HttpMessage httpMessage){
		for (Header item : httpMessage.getAllHeaders())			
			LOG.debug("{}={}", item.getName(), item.getValue());
	}
	
	
}
