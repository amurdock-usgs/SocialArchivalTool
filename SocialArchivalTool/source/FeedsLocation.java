package code;

/**	@author amurdock	**/

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**	FeedsLocation class.  This class is for including functions that will be implemented by multiple other classes, such as reading a common imput file.	**/

public class FeedsLocation
{
	CurrDir options = new CurrDir();
	Logs logs = new Logs();
        public String feedLocation = "";

	/**
	*	Sets up the default feeds location.
	**/
        public void setupFeedsLocation() throws IOException
        {
		createLocationTXT();
        }

	/**
	*	Checks if the folder at the specified location exists.  If folder == "" then it checks the feedLocation.
	*	@param folder	The folder located in the feedsLocation to be checked for existance
	*	@return		Returns if the folder exists or not.
	**/
	public boolean checkFeedsLocation(String folder)
	{
		getFeedsLocation();
		String[] temp = (feedLocation + "/" + folder).split("/");
		String str = "";
		for(int i = 0; i < temp.length; i++)
		{
			if(temp[i] != "")
			{
				str = str + '/' + temp[i];
				File f = new File(str);
				if(!f.exists())
				{
					if(!f.mkdir())
					{
						logs.updateLogs("Feeds location is invalid, resetting to default location.");
						f = new File(options.GetCurrentDir() + "location.txt");
						f.delete();
						try
						{
							createLocationTXT();
						}
						catch(Exception e)
						{}
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	*	Creates the location.txt file and writes the default location to it.
	**/
	private void createLocationTXT() throws IOException
	{
		File f = new File(options.GetCurrentDir() + "location.txt");
		if(f.exists())
		{
		}
		else
		{
			f.createNewFile();
                        FileOutputStream fop = new FileOutputStream(f);
                        File dir = new File(options.GetCurrentDir());
			byte [] temp = dir.toString().getBytes();
			String str = "";
			for(int i = 0; i < temp.length; i++)
			{
				if((char)(temp[i]) == '\\')
				{
					temp[i] = (byte)('/');
				}
				str = str + (char)(temp[i]);
			}
                        str = str + "/feeds";
                        fop.write(str.getBytes());
                        fop.flush();
                        fop.close();
		}
	}

	/**
	*	Retrieves the location stored in the location.txt file.
	**/
        public void getFeedsLocation()
        {
                File f = new File(options.GetCurrentDir() + "location.txt");
                String str = "";
                if(f.exists() && f.length() > 0)
                {
                        try
                        {
                                FileInputStream finp = new FileInputStream(f);
                                byte b = 0;
                                while(b != -1)
                                {
                                        b = (byte)finp.read();
					if(b != -1)
						str = str + (char)b;
                                }
                                finp.close();
                        }
                        catch(Exception e)
                        {}
                }
                feedLocation = str;
	}

	/**
	*	Checks the feedLocation variable folder structure then writes the location to location.txt.
	*	@return		Returns true if the structure exists and the location was saved, else false.
	**/
	public boolean setFeedsLocation()
	{
		byte [] temp = feedLocation.getBytes();
		feedLocation = "";
		for(int i = 0; i < temp.length; i++)
		{
			if((char)(temp[i]) == '\\')
			{
				temp[i] = (byte)('/');
			}
			feedLocation = feedLocation + (char)(temp[i]);
		}

		String currentLocation[] = feedLocation.split("/");
		if(currentLocation[0] != "")
		{
			feedLocation = (new File(options.GetCurrentDir())).toString() + "/" + feedLocation;
			temp = feedLocation.getBytes();
			feedLocation = "";
			for(int i = 0; i < temp.length; i++)
			{
				if((char)(temp[i]) == '\\');
				{
					temp[i] = (byte)'/';
				}
				feedLocation = feedLocation + (char)temp[i];
			}
			currentLocation = feedLocation.split("/");
		}
		feedLocation = "";
		for(int i = 0; i < currentLocation.length; i++)
		{
			if(!currentLocation[i].equals(""))
			{
				feedLocation = feedLocation + "/" + currentLocation[i];
			}
		}
		currentLocation = feedLocation.split("/");
		File f = new File("");
		for(int i = 0; i < currentLocation.length; i++)
		{
			f = new File(f.toString()+"/"+currentLocation[i]);
			if(f.isDirectory())
			{//Exists, do nothing
			}
			else
			{
				if(f.mkdir())
				{//Success
				}
				else
				{//Failure
					logs.updateLogs("Cannot create directory, aborting.");
					getFeedsLocation();
					return false;
				}
			}
		}

		f = new File(options.GetCurrentDir() + "location.txt");
		if(f.exists() && f.length() > 0)
		{
			try
			{
				FileOutputStream fop = new FileOutputStream(f);
				fop.write(feedLocation.getBytes());
				fop.flush();
				fop.close();
			}
			catch(IOException e)
			{
				logs.updateLogs("Failed to change feeds location.");
				getFeedsLocation();
				return false;
			}
		}
		getFeedsLocation();
		return true;
	}

	/**
	*	Moves all the files located in oldLocation to newLocation then deletes them from oldLocation.
	*	@param oldLocation	The current location of all the files to be moved.
	*	@param newLocation	The new location for all the files to reside.
	*	@return 		True if successful transfer of files, else false.
	**/
	public boolean moveToLocation(String oldLocation, String newLocation)
	{
		File newFile = new File(newLocation);
		File oldFile = new File(oldLocation);
		boolean completeCopy = true;

		if(oldFile.isDirectory())
		{
			if(newFile.isDirectory())
			{
				String files[] = oldFile.list();
				for(int i = 0; i < files.length; i++)
				{
					int counter = 1;
					File f = new File(oldLocation + "/" + files[i]);
					File f2 = new File(newLocation + "/" + files[i]);
					if(f.isDirectory())
					{
						if(!f2.exists())
						{
							f2.mkdir();
						}
						if(moveToLocation(f.toString(), newLocation + "/" + files[i]))
						{
							f.delete();
						}
						else
						{
							completeCopy = false;
						}
					}
					else
					{
						f2 = new File(newLocation + "/" + files[i]);
						while(f2.exists())
						{
							String temp[] = {""};
							temp = files[i].split(".");
							String newFileName = "";
							if(temp.length > 0)
							{
								if(!temp[0].equals(""))
								{
									temp[0] = temp[0] + counter;
									newFileName = temp[0];
								}
								else
								{
									temp[1] = temp[1] + counter;
								}
								for(int j = 1; j < temp.length; j++)
								{
									newFileName = newFileName + "." + temp[j];
								}
							}
							else
							{
								newFileName = files[i] + counter;
							}
							logs.updateLogs(files[i] + " already exists, renaming as " + newFileName);
							f2 = new File(newLocation + "/" + newFileName);
						}
						try
						{
							InputStream in = new FileInputStream(f);
							OutputStream out = new FileOutputStream(f2);
							byte[] buf = new byte[1024];
							int len;
							while((len = in.read(buf)) > 0)
							{
								out.write(buf, 0, len);
							}
							in.close();
							out.close();
							f.delete();
						}
						catch(Exception e)
						{
						}
					}
				}
			}
			else
			{
				completeCopy = false;
				logs.updateLogs(newLocation + " does not exist");
				return completeCopy;
			}
		}
		else
		{
			completeCopy = false;
			logs.updateLogs(oldLocation + " does not exist");
		}

		return completeCopy;
	}
}
