INTRO:
Please read this completely before using InfernalBotManager.
It will explain what you should do before using the program 
aswell as explain every possible entry in the settings.ini file.

REQUIREMENTS:
You need Java 8 (32-bit) to run the program.
It should work on any Windows version but I have only tested it on W10 so far.

INSTRUCTIONS:
Before you're able to use the program you need an account at www.infernalbotmanager.com
After that you'll need to do some basic setup: 
	-Add accounts to the accountlist, there are basically 3 ways to do this right now:
		-Manually add them by pressing the NEW button and editing the required fields
		-Importing from a txt file using the following syntax username:password:region (testAccount:testPassword:EUW) - accepted regions are EUW/NA/EUNE/BR/LAN/LAS/OCE/RU/TR
		-Importing them from your current InfernalBot accountlist by using the 'uploadnewaccount' setting.
	-Set your default IB settings on the server, all your clients will use these settings unless overwritten by the 'overwritesettings' settings.
	-Read the possible entries for the settings.ini file below and edit your settings.ini file according to your needs.

SETTING ENTRIES:
[login] 
username=test.test@gmail.com --> The email adress you signed up with.
password=Test894! --> The password you chose when you signed up.

[clientinfo]
clienttag=TESTSERVER1 --> !!IMPORTANT!! The tag for your current client, this NEEDS to be unique for every client on your account or you WILL run into issues.
region=EUW --> Region for all bots on this client

[clientsettings]
infernalmap=C:/temp/ --> Path to the folder containing infernalbot. Use forwards slashes (/) not backslashes (\).
accounts=5 --> Amount of account to grab from the server (should be 5x the amount of groups on that client). These get taken from the active accounts for that region, with highest priority (closest to 0) with the highest level.
accountbuffer=2 --> Amount of bufferaccount to grab from the server (incase accounts get banned or become unusable for some other reason). These get taken from the active accounts with lowest priority (closest to 9) with the lowest level.
uploadnewaccounts=false --> The manager automatically updates the accounts that are already present on the server. Enabling this will also upload new accounts on the server. !!CAUTION!! you should disable this unless you wish to upload initial accounts that way. If it is enabled and you delete accounts via the web interface they will get re-added because of this.
						--> !!IMPORTANT!! The manager clears the infernalbot database on every startup and exit, so if you have this on false the first run and you don't have the accounts set up on the server they will be deleted!
reboot=false --> Reboot after a given time
reboottime=10800 --> Seconds untill reboot
fetchsettings=true --> Fetch the InfernalBot settings from the server, disableing this will use the settings you have in InfernalBot itself for this client.
overwritesettings=false --> Enable this if you want to fetch the settings from the server but overwrite some of them (see [botsettings] section below).
rebootfrommanager=false --> Enable this to reboot your pc if the manager finds that infernalbot is opened but there are no active queuers. This usually happens if you enable safestop or if the queuers can't start for some reason (no sessions/not enough accounts). Leaving this disabled will instead stop the infernalbot process and restart it.
						--> The recommended usage for this is to enable it and enable Safestop after x games in the InfernalBot settings but disable the reboot options in InfernalBot. That way once the safestop happens InfernalBotManager can safely stop all its running threads, update the accountlist & clientdata and reboot the PC. 

[botsettings] --> This section is not required unless overwritesettings is enabled, it overwrites certain settings received from the default settings on the server. !!NOTE!! these two are currently the only botsettings that can be overwritten. If you need another setting overwritten this way, contact me on discord and I'll add it in a later update.
groups=2  --> Amount of groups to run on this client
clientpath=C:/temp/  --> Path to league of legends on this client

[extra]
readme=read --> Put this on read if you read the readme and understood it (to avoid stupid mistakes)

EXTRA NOTES:
This way of working (ini file) is going to be replaced by a more central system (serverside) in the future but for now this will have to do.
If you have any questions, bugs, suggestions,... you are welcome to share them on our discord server.

