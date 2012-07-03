package code;

/**	@author amurdock	**/

import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

/**	Designed to retrieve the location of the running jar file and save it to the currDir variable.	**/

public class CurrDir
{
	String currDir = "";

	/**	
	*	Class constructor.
	**/
	public CurrDir()
	{
		String path = SocialArchivalTool.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = "";
		try
		{
			decodedPath = URLDecoder.decode(path, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			System.err.println("Error: " + e.getMessage());
		}
		if(decodedPath.endsWith(".jar"))
		{
			String[] temp = decodedPath.split("SocialArchivalTool[-]?[0-9.]*jar");
			currDir = temp[0];
		}
		else
		{
			currDir = decodedPath;
		}
	}

	/**	GetCurrentDir function.  This funciton is designed to output the location of the jar file that is running.
	*	@return		Returns the directory that the .jar file is located in.
	**/
	public String GetCurrentDir()
	{
		return currDir;
	}
}
