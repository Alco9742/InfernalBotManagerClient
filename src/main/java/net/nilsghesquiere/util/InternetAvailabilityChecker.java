package net.nilsghesquiere.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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
			LOGGER.info("An error occurred while checking internet connectivity.");
			LOGGER.debug("Exception: ",e);
			return false;
		}
	}
	
	private static boolean isHostAvailable(String hostName) throws IOException{
		try(Socket socket = new Socket()){
			int port = 80;
			InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);
			socket.connect(socketAddress, 3000);
			return true;
		}catch(UnknownHostException unknownHost){
			 return false;
		}
	}
}

