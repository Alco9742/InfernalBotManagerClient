<h1>LeagueInformer base project</h1>
<h2>InfernalManagerClient</h2>

<h3>TODO Eventualy:</h3>	
<ul>
<li>Check server for riot server status --> add to network checks</li>
<li>Check lol version -> update</li>
<li>Maybe check what version of lol infernal is updated for?</li>
</ul>

<h3>TODO NOW</h3>
<ul>
<li>API system from infernal</li>
<li>Implement riot API, orriana?</li>
<li>Simple commands</li>
</ul>

<h3>REST SYSTEM</h3>
<ul>
<li>Autostart API server & Disable bot autostart</li>
<li>update accountlist (dont empty anymore) -> check every account(done or banned = set on server and remove from infernal, , don't check prio for now)</li>
<li>get amount of accounts we have and we need and fetch the ones we're short</li>
<li>Check how many accounts are 1 level lower than max and fetch that amount of accounts to be sure</li>
<li>Start bot through rest</li>
<li>with accountlist updater do the checks again every minute or so</li>
<li>Keep an automatic buffer in case there are bans, figure out what amount but keep them on lowest priority so they only get used on very rare occasions</li>
</ul>




<h3>Client server connection for settings<h3>
<ol>
<li>Only username, password and tag in ini file<li>
<li>Connect to the server to check if theres a client registered for the user with that tag</li>
<li>If no: end program; if yes: check HWID</li>
<li>If HWID nok: end program, if OK: fill all settings</li>
</ol>

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