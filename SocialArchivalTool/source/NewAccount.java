package code;

/**	@author amurdock	**/

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;

/**	The NewAccount internal frame class.  This class offers the user a interface to add user accounts to the list of Twitter accounts to follow.  Adding a new account will require authentication with Twitter.  OAuth authentication is done through the Scribe library.	**/

public class NewAccount
{
	Logs logs = new Logs();
	/**
	*	Class constructor.
	*	@param width	The width of the new frame.
	*	@param height	The height of the new frame.
	**/
	public NewAccount()
	{
	}

	/**
	*	Adds the account specified to the accounts.txt file as well as request access to the account on twitter.
	**/
	public boolean AddUserAccount()
	{
		OAuth twitterOAuth = new OAuth();
		String accountsFile = "accounts.txt";
		String accountName = "";

		/* Code to get account verification from Twitter */
		//Obtain the Request Token, returns null on failure or the account name on sucess.
		
		accountName = twitterOAuth.GetToken();
		if(accountName == null)
		{
			logs.updateLogs("Failed to get account name and information");
			return false;
		}
		/* End verification code */

		/* Code to check for name in accounts.txt before adding */
		try
		{
			FileInputStream fstream = new FileInputStream(accountsFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while((strLine = br.readLine()) != null)
			{//Check if name is already in the list, if so return false to show it failed to add the account.
				if(accountName.equals(strLine))
				{
					in.close();
					logs.updateLogs("The feeds from " + accountName + " account are already being monitored.");
					return false;
				}
			}
			in.close();
		}
		catch(Exception e)
		{
			logs.updateLogs("Error: " + e.getMessage());
		}
		/* End check code */

		/* Code to add the username to the accounts.txt file */
		//Exit on failure returning error to user.
		try
		{
			FileWriter fstream = new FileWriter(accountsFile, true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(accountName + "\n");
			out.close();
			logs.updateLogs(accountName + " added to the list to follow");
		}
		catch(Exception e)
		{
			logs.updateLogs("Error: " + e.getMessage());
			return false;
		}
		/* End add username code */

		/* Code to update feeds for the user (first time run) */
		FeedsLocation location = new FeedsLocation();
		location.getFeedsLocation();
		if(location.checkFeedsLocation(accountName))
		{
			if(twitterOAuth.FirstTimeRun(accountName))
			{
				logs.updateLogs("First time run successful for " + accountName);
				return true;
			}
			else
			{
				logs.updateLogs("First time run failed for " + accountName);
				return false;
			}
		}
		else
		{
			logs.updateLogs("Feeds location is invalid for " + accountName + " first time run aborted.");
			return false;
		}
		/* End update feeds code */
	}
}
