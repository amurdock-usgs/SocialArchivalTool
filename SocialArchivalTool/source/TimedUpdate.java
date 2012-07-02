package code;

/**	@author amurdock	**/

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.*;

/**	TimedUpdate function.  This function is designed to run continuously while the program is running and monitor the current time then compare it to the time given as a set update time.	**/

public class TimedUpdate implements Runnable
{
	private Thread thread = null;
	DateFormat timeFormat = new SimpleDateFormat("HH:mm");
	Date date;
	String timeStr;
	String hour, minute;
	JTextArea textArea = new JTextArea("");
	Logs logs;
	Options options;
	boolean updated = false;

	public TimedUpdate(Options opts)
	{
		options = opts;
		thread = new Thread(this);
		thread.start();
	}

	public void run()
	{
		while(true)
		{
			date = new Date();
			timeStr = timeFormat.format(date).toString();
			String hourStr = (timeStr.split(":"))[0];
			String minStr = (timeStr.split(":"))[1];
			hour = (options.GetTimedUpdateTime().split(":"))[0];
			minute = (options.GetTimedUpdateTime().split(":"))[1];
			if(hour.equals(hourStr) && minute.equals(minStr) && !updated)
			{
				Updater updater = new Updater(textArea, 0);
				updater.start();
				updated = true;
			}
			else
			{
				updated = false;
			}
			try
			{
				Thread.sleep(30000);//Sleep for 1/2 a minute
			}
			catch(InterruptedException ie)
			{
				logs.updateLogs(ie.getMessage());
			}
		}
	}
}
