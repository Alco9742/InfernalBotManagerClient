package net.nilsghesquiere.util.enums;

//TODO clean this
public enum ClientStatus {
	INIT, // Monitor created, no client
	CONNECTED,  //Monitor created, client created, connected.
	UPDATE, //Client is performing an update
	INFERNAL_STARTUP, //Infernalbot is starting up
	INFERNAL_RUNNING,  //Infernalbot is running
	CLOSE, // Closing client
	CLOSE_REBOOT, // Closing client with reboot
	ERROR, //Error
	UNASSIGNED,
	DISCONNECTED
}
