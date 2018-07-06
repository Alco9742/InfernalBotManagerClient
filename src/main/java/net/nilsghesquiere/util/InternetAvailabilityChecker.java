package net.nilsghesquiere.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternetAvailabilityChecker{
	private static final Logger LOGGER = LoggerFactory.getLogger(InternetAvailabilityChecker.class);
	public static boolean isInternetAvailable(){
		try {
			return isHostAvailable("google.com") || isHostAvailable("amazon.com")
					|| isHostAvailable("facebook.com")|| isHostAvailable("apple.com");
		} catch (IOException e) {
			LOGGER.debug("An error occurred while checking internet connectivity.");
			LOGGER.debug("Exception: ",e);
			return netIsAvailableSecondary();
		}
	}
	
	private static boolean isHostAvailable(String hostName) throws IOException{
		try(Socket socket = new Socket()){
			int port = 80;
			InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);
			socket.connect(socketAddress, 3000);
			socket.close();
			return true;
		}catch(UnknownHostException unknownHost){
			 return false;
		}
	}
	
	private static boolean netIsAvailableSecondary() {
		LOGGER.debug("Checking internet connectivity through the secondary method.");
	    try {
	        final URL url = new URL("http://www.google.com");
	        final URLConnection conn = url.openConnection();
	        conn.connect();
	        conn.getInputStream().close();
	        return true;
	    } catch (MalformedURLException e) {
			LOGGER.debug("Exception: ",e);
			return false;
	    } catch (IOException e) {
			LOGGER.debug("Exception: ",e);
	        return false;
	    }
	}
}

