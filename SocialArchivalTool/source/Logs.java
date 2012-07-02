package code;

/**	@author amurdock	**/

import java.io.*;
import java.awt.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**	Logs class.  This class is designed to write new entries to log files and save the log files.	**/

public class Logs
{
	CurrDir options = new CurrDir();

	/**
	*	Inputs the string into the log file corresponding to the day, or creates the log file for the date if one doesn't already exist.
	*	@param	str	The string to be added to the log file.
	**/
	public void updateLogs(String str)
	{
		//Get the current date
		DateFormat yearFormat = new SimpleDateFormat("yyyy");
		DateFormat monthFormat = new SimpleDateFormat("MM");
		DateFormat dayFormat = new SimpleDateFormat("dd");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String yearStr = yearFormat.format(date).toString();
		String monthStr = monthFormat.format(date).toString();
		String dayStr = dayFormat.format(date).toString();
		String timeStr = timeFormat.format(date).toString();
		File f = new File(options.GetCurrentDir() + "logs");
		if(f.exists() && f.isDirectory())
		{
		}
		else
		{
			f.mkdir();
		}
		f = new File(options.GetCurrentDir() + "logs/" + yearStr + "-" + monthStr + "-" + dayStr + ".txt");
		try
		{
			FileWriter fstream = new FileWriter(f, true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<" + timeStr + "> : " + str + "\n");
			out.close();
		}
		catch(IOException e)
		{
			System.out.println("IOException: " + e.getMessage());
		}
	}
}
