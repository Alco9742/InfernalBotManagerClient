package net.nilsghesquiere.util.error;

public class ServerInternalErrorException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ServerInternalErrorException(String msg) {
		super(msg);
	}
	
}
