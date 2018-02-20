package net.nilsghesquiere.enums;

public enum ProxyType {
	HTTP("HTTP"),
	SOCKS5("SOCKS5");

	private String name;

	private ProxyType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
