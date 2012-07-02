package code;

/**	@author amurdock	**/

import javax.swing.JTextArea;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;

/**	The Updater class.  This class is used for a runnable class to update the archives, can be implemented in the UpdateArchives frame for use ability or in the main-class to update the archives then quit.	**/

public class Updater extends Thread implements Runnable
{
        JTextArea textArea;
        String[] list = {""};
	FeedsLocation location = new FeedsLocation();
	OAuth twitterOAuth = new OAuth();
	boolean stop = false;
	Logs logs = new Logs();

	/**
	*	Class constructor.
	*	@param ta	The JTextArea that will be updated.
	*	@param stopper	Used to let the updater know if it should run in update only mode or in user mode.
	**/
        public Updater(JTextArea ta, int stopper)
        {
                textArea = ta;
                list = getAccounts();
		location.getFeedsLocation();
                if(!location.checkFeedsLocation(""))
                {
                        System.exit(-1);
                }
		if(stopper != 0)
		{//Update only mode, ends the program after updating the accounts
			stop = true;
		}
        }

	/**
	*	Overload of the run function for this runnable thread.  Updates all of the accounts that have tokens and are in the accounts.txt file.
	**/
        public void run()
        {
                for(int i = 0; i < list.length; i++)
                {
                        if(list[i].equals(""))
                        {
                        }
                        else
                        {
                                if(location.checkFeedsLocation(list[i]))
                                {
                                        if(queryTwitter(list[i]))
                                        {
                                                textArea.setText(textArea.getText() + list[i] + " updated.\n");
                                        }
                                        else
                                        {
                                                textArea.setText(textArea.getText() + list[i] + " failed to update.\n");
                                                logs.updateLogs(list[i] + " failed.");
                                        }
                                }
                                else
                                {
                                        textArea.setText(textArea.getText() + list[i] + " failed to update.\n");
                                        logs.updateLogs("No folder for " + list[i] + " for the archives to be saved in.");
                                }
                        }
                }
                textArea.setText(textArea.getText() + "Updates complete.\n");
                logs.updateLogs("Updates complete.");
		if(stop == true)
		{
			System.exit(-1);
		}
	}

	/**
	*	Calls the function to query twitter for an account.
	*	@param account	The account to query for information.
	*	@return		Success or failure of the query.
	**/
        public boolean queryTwitter(String account)
        {
                //Query twitter for the given account name and update the associated archives located at feeds/<account>/<current year>/<month>.txt
		Options options = new Options();
		options.ReadOptions();
		return twitterOAuth.QueryTwitter(account);
        }

	/**
	*	Retrieves all the accounts listed in the accounts.txt file.
	*	@return		A string array of all the accounts in the accounts.txt file.
	**/
        public String[] getAccounts()
        {
                String[] s = {""};
                //Put all the names in the accounts.txt file into a list.
                try
                {
                        FileInputStream fstream = new FileInputStream("accounts.txt");
                        DataInputStream in = new DataInputStream(fstream);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String strLine;
                        int i = 0;
                        while((strLine = br.readLine()) != null)
                        {
                                //Add to string array.
                                if(s.length <= i)
                                {
                                        String[] temp = new String[s.length + 1];
                                        for(int j = 0; j < s.length; j++)
                                        {
                                                temp[j] = s[j];
                                        }
                                        s = new String[temp.length];
                                        for(int j = 0; j < temp.length; j++)
                                        {
                                                s[j] = temp[j];
                                        }
                                }
                                s[i] = strLine;
                                i++;
                        }
                        in.close();
                }
                catch(Exception e)
                {
                        logs.updateLogs("accounts.txt does not exist, creating now.");
                        try
                        {
                                File f = new File("accounts.txt");
                                f.createNewFile();
                        }
                        catch(Exception exception)
                        {
                                logs.updateLogs("Failed to create accounts.txt");
                        }
                }
                return s;
        }
}
