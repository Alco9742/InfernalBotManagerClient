package net.nilsghesquiere.entities;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nilsghesquiere.enums.ProxyType;

@Data
public class Proxy implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String handle;
	private String proxyHost;
	private String proxyPort;
	private String proxyUser;
	private String proxyPassword;	
	private ProxyType proxyType;
	
	public Proxy() {}
	
	public Proxy(String handle, String proxyHost, String proxyPort,
			String proxyUser, String proxyPassword, ProxyType proxyType) {
		super();
		this.handle = handle;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyUser = proxyUser;
		this.proxyPassword = proxyPassword;
		this.proxyType = proxyType;
	}
	
}
