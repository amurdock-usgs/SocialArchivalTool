/**	@author amurdock	**/

import javax.swing.*;
import java.io.*;
import java.net.*;

/**	SocialArchivalTool launcher class.  This class is designed to be stand alone that will run and check the current version of the archival tool (if one exists) against the most up-to-date version on github, if there is a new version then download it and execute the program.  The .jar syntax should be "SocialArchivalTool-version.jar".  The newest available version should be found by looking at the first line of the changelog.	**/

public class SocialArchivalToolLauncher
{
	boolean updateMode = false;
	String updateModeStr = "updateMode";
	/**
	*	Overload of the main class.
	**/
	public static void main(String [] args)
	{
		new SocialArchivalToolLauncher();
	}

	public SocialArchivalToolLauncher()
	{
		JList list;
		String currDirStr = System.getProperty("user.dir");
		File currDir = new File(currDirStr);
		String[] chld = currDir.list();
		double currVer = 0.0;
		double newVer = 0.0;
		String programPageURL = "https://raw.github.com/wiki/amurdock-usgs/SocialArchivalTool/changelog.txt";
		String programName = "SocialArchivalTool Launcher";
		String changelogURL = "";
		if(chld != null)
		{
			for(int i = 0; i < chld.length; i++)
			{
				String[] temp = chld[i].split("-");
				if(temp.length > 1)
				{
					if(temp[0].equals(programName))
					{
						temp = temp[1].split(".jar");
						try
						{
							double tempD = Double.valueOf(temp[0].trim()).doubleValue();
							if(tempD > currVer)
							{
								currVer = tempD;
							}
						}
						catch(NumberFormatException nfe)
						{
							System.err.println("NumberFormatException: " + nfe.getMessage());
						}
					}
				}
			}
		}
		//Should have the current installed version saved in the currVer variable, now check for the newest version and save into the newVer variable.
		//Read the first line of the changelog.txt file.
		try
		{
			URL url = new URL(changelogURL);
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
			String str = "";
			while((str = r.readLine()) != null)
			{
				if(!(new Double(str)).isNaN())
				{
					break;
				}
			}
			newVer = new Double(str);
		}
		catch(MalformedURLException ex)
		{
			System.err.println(changelogURL + " is not a valid URL.");
		}
		catch(IOException ie)
		{
			System.err.println("IO Error: " + ie.getMessage());
		}
		if(newVer > currVer)
		{
			//Download the newest version then delete the current version if it succeeds.
			OutputStream outStream = null;
			URLConnection uCon = null;
			InputStream is = null;
			try
			{
				int size = 1024;
				URL url;
				byte[] buf;
				int ByteRead, ByteWritten = 0;
				url = new URL(programPageURL + programName + "-" + newVer + ".jar");
				outStream = new BufferedOutputStream(new FileOutputStream(programName + "-" + newVer + ".jar"));
				uCon = url.openConnection();
				is = uCon.getInputStream();
				buf = new byte[size];
				while((ByteRead = is.read(buf)) != -1)
				{
					outStream.write(buf, 0, ByteRead);
					ByteWritten += ByteRead;
				}
				System.out.println("Downloaded version " + newVer + " successfully.");
				if(currVer != 0.0)
				{
					System.out.print("Deleting old version now...");
					File oldVer = new File(programName + "-" + currVer + ".jar");
					if(oldVer.delete())
					{
						System.out.println("success!");
					}
					else
					{
						System.out.println("failed to delete " + programName + "-" + currVer + ".jar");
					}
				}
				currVer = newVer;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					is.close();
					outStream.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		//After getting the newest version now run it.
		//The currVer variable now holds the newest version and allows for failure of downloading the newest version.  So if the user can't access the download page instead of erroring out it will fail to update then run the old version of the software.

		try
		{
			Process p = Runtime.getRuntime().exec("java -cp " + programName + "-" + currVer + ".jar code." + programName);
			p.waitFor();
		}
		catch(Exception err)
		{
			err.printStackTrace();
		}
	}

	/**
	*	Parses the command string given then enables/disables flags based on the input.
	*	@param cmd	The command string to be parsed.
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
			if(temp[0].equals(updateModeStr))
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
					System.err.println("Syntax error: " + cmd);
				}
			}
		}
		else
		{
			System.err.println("Syntax error: " + cmd);
		}
	}
}
