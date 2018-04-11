package net.nilsghesquiere.enums;

public enum ClientStatus {
	INIT, // Monitor created, no client
	CONNECTED,  //Monitor created, client created, connected.
	UPDATE, //Client is performing an update
	INFERNAL_RUNNING,  //Infernalbot is running
	CLOSE, // Closing client
	CLOSE_REBOOT, // Closing client with reboot
	ERROR //Error
}
