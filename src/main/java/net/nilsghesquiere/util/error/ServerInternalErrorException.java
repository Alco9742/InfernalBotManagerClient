package net.nilsghesquiere.util.error;

import org.springframework.security.core.AuthenticationException;

public class ServerInternalErrorException extends AuthenticationException{
	private static final long serialVersionUID = 1L;

	public ServerInternalErrorException(String msg) {
		super(msg);
	}
	
}
