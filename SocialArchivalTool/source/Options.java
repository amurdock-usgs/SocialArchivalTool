package code;

/**	@author amurdock	**/

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.DataInputStream;

/**	Options class.  This class is designed to read functions listed in the options.txt file.  Has no constructor so for accurate performance after the class has been initialized the ReadOptions command should be executed to update the values to the users preferences.	**/

public class Options
{
	private int numberOfTweets = 200;
	private boolean updateMode = false;
	private boolean displayInstructions = true;
	private boolean includeReTweets = true;
	private boolean timedUpdate = false;
	private int timedUpdateHour = 0;
	private int timedUpdateMinute = 0;
	final static String OPTIONS_TXT = "options.txt";
	final static String NUMBEROFTWEETS = "numberOfTweets";
	final static String UPDATEMODE = "updateMode";
	final static String DISPLAYINSTRUCTIONS = "displayInstructions";
	final static String INCLUDERETWEETS = "includeReTweets";
	final static String TIMEDUPDATE = "timedUpdate";
	final static String TIMEDUPDATETIME = "timedUpdateTime";
	Logs logs = new Logs();
	CurrDir currDir = new CurrDir();

	/**
	*	ReadOptions function.  This function is designed to read in the options set in the options.txt file and save them into the private variables in this class.
	**/
	public void ReadOptions()
	{
		File options = new File(currDir.GetCurrentDir() + OPTIONS_TXT);
		if(!options.exists())
		{
			WriteOptions();
		}
		try
		{
			FileInputStream fstream = new FileInputStream(options);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String str;
			String[] commands;
			while((str = br.readLine()) != null)
			{//Reads the commands out of the options file.  Ignore everything on a line following the # sign.
				commands = str.split("#");
				if(commands.length > 1)
				{
					if(commands[0].equals("") || commands[0] == null)
					{//Do nothing, entire line is a comment.
					}
					else
					{
						ExecuteCommand(commands[0]);
					}
				}
				else
				{
					ExecuteCommand(commands[0]);
				}
			}
			in.close();
		}
		catch(Exception e)
		{
			logs.updateLogs("Error: " + e.getMessage());
		}
	}

	/**
	*	ExecuteCommand function.  This function is designed to accept a command in the format of <command name>=<command value> as a String input out of the options.txt file, it then pulls out the command name and sets the corresponding class variable = the command value.
	*	@param	cmd	The command string that needs to be parsed.
	**/
	private void ExecuteCommand(String cmd)
	{
		if(cmd.equals("") || cmd.equals(" "))
		{
			return;
		}
		//Remove spaces before processing
		String [] temp = cmd.split(" ");
		cmd = "";
		for(int i = 0; i < temp.length; i++)
		{
			cmd = cmd + temp[i];
		}
		temp = cmd.split("=");
		if(temp.length > 1)
		{
			if(temp[0].equals(UPDATEMODE))
			{
				if(temp[1].equals("true"))
				{
					updateMode = true;
				}
				else if(temp[1].equals("false"))
				{
					updateMode = false;
				}
				else
				{
					logs.updateLogs("Syntax error: " + cmd);
				}
			}
			if(temp[0].equals(NUMBEROFTWEETS))
			{
				if(Integer.valueOf(temp[1]) > 200 || Integer.valueOf(temp[1]) < 1)
				{
					numberOfTweets = 100;
					logs.updateLogs("numberOfTweets out of bounds, set to 100");
				}
				else
				{
					numberOfTweets = Integer.valueOf(temp[1]);
				}
			}
			if(temp[0].equals(DISPLAYINSTRUCTIONS))
			{
				if(temp[1].equals("true"))
				{
					displayInstructions = true;
				}
				else if(temp[1].equals("false"))
				{
					displayInstructions = false;
				}
				else
				{
					logs.updateLogs("Syntax error: " + cmd);
				}
			}
			if(temp[0].equals(INCLUDERETWEETS))
			{
				if(temp[1].equals("true"))
				{
					includeReTweets = true;
				}
				else if(temp[1].equals("false"))
				{
					includeReTweets = false;
				}
				else
				{
					logs.updateLogs("Syntax error: " + cmd);
				}
			}
			if(temp[0].equals(TIMEDUPDATE))
			{
				if(temp[1].equals("true"))
				{
					timedUpdate = true;
				}
				else if(temp[1].equals("false"))
				{
					timedUpdate = false;
				}
				else
				{
					logs.updateLogs("Syntax error: " + cmd);
				}
			}
			if(temp[0].equals(TIMEDUPDATETIME))
			{
				String [] temp2 = temp[1].split(":");
				if(temp2.length == 1)
				{//Hour only
					try
					{
						timedUpdateHour = Integer.valueOf(temp2[0]);
						timedUpdateMinute = 0;
					}
					catch(NumberFormatException e)
					{
						timedUpdate = false;
						logs.updateLogs("Syntax error: " + cmd + " NumberFormatException: " + e.getMessage());
					}
				}
				else if(temp2.length >= 2)
				{//Assume HH:MM...
					try
					{
						timedUpdateHour = Integer.valueOf(temp2[0]);
						timedUpdateMinute = Integer.valueOf(temp2[1]);
					}
					catch(NumberFormatException e)
					{
						timedUpdate = false;
						logs.updateLogs("Syntax error: " + cmd + " NumberFormatException: " + e.getMessage());
					}
				}
				else
				{
					logs.updateLogs("Syntax error: " + cmd);
				}
			}
		}
		else
		{
			logs.updateLogs("Syntax error: " + cmd);
		}
	}

	/**
	*	WriteOptions function.  This functions is designed to write all the current option variable values from this class into a new options.txt file with the standard comment header.  This deletes the previous file and all options set in it as well as any comments, recommended to run ReadOptions first.
	**/
	public void WriteOptions()
	{
		File options = new File(currDir.GetCurrentDir() + OPTIONS_TXT);
		if(!options.exists())
		{
			options.delete();
		}
		try
		{
			options.createNewFile();
			FileWriter fstream = new FileWriter(options);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("#Options file, used to designate different options for the program.\n");
			out.write(UPDATEMODE + "=" + String.valueOf(updateMode) + "\n");
			out.write(NUMBEROFTWEETS + "=" + String.valueOf(numberOfTweets) + "\n");
			out.write(DISPLAYINSTRUCTIONS + "=" + String.valueOf(displayInstructions) + "\n");
			out.write(INCLUDERETWEETS + "=" + String.valueOf(includeReTweets) + "\n");
			out.write(TIMEDUPDATE + "=" + String.valueOf(timedUpdate) + "\n");
			out.write(TIMEDUPDATETIME + "=" + String.valueOf(timedUpdateHour) + ":" + String.valueOf(timedUpdateMinute) + "#format: HH:mm use 24hour format (0-23)\n");
			out.close();
		}
		catch(IOException e)
		{
			logs.updateLogs("Error: " + e.getMessage());
		}
	}

	/**
	*	GetNumberOfTweets function.  This function is designed to return the value saved in the numberOfTweets variable.
	*	@return		Returns the value of the numberOfTweets private variable.
	**/
	public int GetNumberOfTweets()
	{
		return numberOfTweets;
	}

	/**
	*	SetNumberOfTweets function.  This function is designed to set a new value for the numberOfTweets variable.
	*	@param	tweets	The new value to be assigned to numberOfTweets.
	*	@return		Returns true on success or false on failure.
	**/
	public boolean SetNumberOfTweets(int tweets)
	{
		if(tweets > 0 && tweets <= 200)
		{
			numberOfTweets = tweets;
			return true;
		}
		else
			return false;
	}

	/**
	*	GetUpdateMode function.  This function is designed to return the value of the updateMode variable.
	*	@return		Returns the value of the updateMode variable.
	**/
	public boolean GetUpdateMode()
	{
		return updateMode;
	}

	/**
	*	SetUpdateMode function.  This function is designed to set a new value to the updateMode variable.
	*	@param	mode	The new value to be set to updateMode.
	*	@return		Returns the value of updateMode.
	**/
	public boolean SetUpdateMode(boolean mode)
	{
		updateMode = mode;
		return updateMode;
	}

	/**
	*	GetDisplayInsturctions function.  This function is designed to return the value of displayInstructions.
	*	@return		Returns the value of displayInstructions.
	**/
	public boolean GetDisplayInstructions()
	{
		return displayInstructions;
	}

	/**
	*	SetDisplayInstructions function.  This function is designed to set a new value to the displayInstructions variable.
	*	@param	display	The new value to be set to the displayInstructions variable.
	*	@return		Returns the value of displayInstructions.
	**/
	public boolean SetDisplayInstructions(boolean display)
	{
		displayInstructions = display;
		return displayInstructions;
	}

	/**
	*	GetIncludeReTweets function.  This function is designed to return the value of includeReTweets.
	*	@return		Returns the value of includeReTweets.
	**/
	public boolean GetIncludeReTweets()
	{
		return includeReTweets;
	}

	/**
	*	SetIncludeReTweets function.  This function is designed to set a new value to the includeReTweets variable.
	*	@param	retweets	The new value to be set to the includeReTweets variable.
	*	@return			Returns the value of includeReTweets.
	**/
	public boolean SetIncludeReTweets(boolean retweets)
	{
		includeReTweets = retweets;
		return includeReTweets;
	}

	/**	GetTimedUpdate function.  This function is designed to return the value of timedUpdate.
	*	@return		Returns the value of timedUpdate.
	**/
	public boolean GetTimedUpdate()
	{
		return timedUpdate;
	}

	/**	SetTimedUpdate function.  This function is designed to set the value of timedUpdate.
	*	@param	update	The new value of timedUpdate.
	*	@return		Returns the new value of timedUpdate.
	**/
	public boolean SetTimedUpdate(boolean update)
	{
		timedUpdate = update;
		return timedUpdate;
	}

	/**	GetTimedUpdateTime function.  This function is designed to return timedUpdateHour:timedUpdateMinute.
	*	@return		Returns a string with timedUpdateHour:timedUpdateMinute.
	**/
	public String GetTimedUpdateTime()
	{
		return String.valueOf(timedUpdateHour) + ":" + String.valueOf(timedUpdateMinute);
	}

	/**	SetTimedUpdateTime function.  This function is designed to input new values into timedUpdateHour and timedUpdateMinute.
	*	@param	time	The new time to insert, in the format of hour:minute.
	*	@return		Returns timedUpdateHour:timedUpdateMinute.
	**/
	public String SetTimedUpdateTime(String time)
	{
		timedUpdateHour = Integer.valueOf((time.split(":"))[0]);
		timedUpdateMinute = Integer.valueOf((time.split(":"))[1]);
		return String.valueOf(timedUpdateHour) + ":" + String.valueOf(timedUpdateMinute);
	}
} 
