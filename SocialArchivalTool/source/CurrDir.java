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
		currDir = decodedPath.length() <= 16 ? decodedPath : decodedPath.substring(0, decodedPath.length() - 16); //Removes the "SocialArchivalTool.jar" from the path.
	}

	/**	GetCurrentDir function.  This funciton is designed to output the location of the jar file that is running.
	*	@return		Returns the directory that the .jar file is located in.
	**/
	public String GetCurrentDir()
	{
		return currDir;
	}
}
