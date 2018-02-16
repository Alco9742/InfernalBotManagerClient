package net.nilsghesquiere.enums;

public enum ProxyTypeEnum {
	HTTP("HTTP"),
	SOCKS5("SOCKS5");

	private String name;

	private ProxyTypeEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
