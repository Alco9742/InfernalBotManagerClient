<h1>LeagueInformer base project</h1>
<h2>InfernalManagerClient</h2>
<h3>TODO BEFORE BETA:</h3>	
<ul>
<li>generate ini file if not found</li>
</ul>


<h3>TODO DURING BETA:</h3>	
<ul>
<li>Client settings fetch from server</li>
<li>Register clients + simple commands</li>
<li>Check server for riot server status --> add to network checks</li>
<li>Change from nullchecks to Optionals</li>
<li>API system from infernal</li>
<li>OAuth2 authentication</li>
</ul>

<h3>Client server connection map(for LolAccounts</h3>
<ol>
<li>Client connects to Infernalbot database and reads the accounts</li>
<li>Loop over the grabbed accounts, launch REST query for each account (get id by accountignorecase) fill in ID's or null</li>
<li>Wrap the Accounts in a LolMixedAccountMap --> New entries seperate from existing ones</li>
<li>Send to a seperate method in the LolAccount REST controller of the server</li>
<li>For new accounts: create the account with AccountStatus NEW, for existing: update account and set AccountStatus according to checks</li>
<li>If this send a success response: delete accounts in infernal DB</li>
<li>Grab new accounts for use from the server according to client parameters, update status to in use and assigned to to client name<li>
<li>Insert new accounts for use into the infernal database</li>
<li>TODO: if infernal client close (crashchecker?) update accounts to ready for use and no longer assigned</li>
</ol>

<h3>Client server connection map(for InfernalSettings</h3>
<ol>
<li>Client connects to Server and grabs InfernalSettings</li>
<li>Client connects to Infernalbot database and reads the default settings(only the ones we don't make editable)</li>
<li>Client puts the default settings into the settings recieved form the server</li>
<li>Client checks if there is a Settings entry with the name InfernalManager: Yes -> Update; No -> Create</li>
<li>Set the ID of the infernalmanager settings as the active ones</li>
</ol>