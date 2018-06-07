package net.nilsghesquiere.util.error;

import org.springframework.security.core.AuthenticationException;

public class RESTAuthenticationException extends AuthenticationException{
	private static final long serialVersionUID = 1L;

	public RESTAuthenticationException(String msg) {
		super(msg);
	}
	
}
