package code;

/**	@author amurdock	**/

import javax.swing.*;
import java.util.Scanner;
import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*; 
import java.io.*;

/**	The OAuth class, used for all of the OAuth communication.  This class will have all the structures and function for communication through OAuth.	**/

public class OAuth
{
	private static final String PROTECTED_RESOURCE_URL = "http://api.twitter.com/l/account/verify_credentials.xml";
	private static final String DIRECT_MESSAGES_SENT_URL = "http://api.twitter.com/1/direct_messages/sent.xml";
	private static final String DIRECT_MESSAGES_RECEIVED_URL = "http://api.twitter.com/1/direct_messages.xml";
	private static final String STATUSES_URL = "http://api.twitter.com/1/statuses/user_timeline.xml";
	static final String [] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	static final String TO_STR = "<To>";
	static final String FROM_STR = "<From>";
	static final String DATE_STR = "<Date>";
	static final String ID_STR = "<ID>";
	static final String TEXT_STR = "<Text>";
	static final String ENTITIES_STR = "<entities>";
	Logs logs = new Logs();

	OAuthService service = new ServiceBuilder()
		.provider(TwitterApi.class)
		.apiKey("Yy3ZDy0xGN5kAN9z6jUag")
		.apiSecret("GqXC0RBeysrdfHd3fYT0xgppAE10qVPlGZzXzzz35E")
		.build();
	Scanner in = new Scanner(System.in);
	FeedsLocation location = new FeedsLocation();

	/**
	*	Class constructor.
	**/
	public OAuth()
	{
		location.getFeedsLocation();
	}

	/**
	*	Get access token for the user, requires the user to login to Twitter at the URL given.
	*	@return		Account name corresponding to the new token.
	**/
	public String GetToken()
	{
		//Obtain request token
		String accountName = "";
		Verifier verifier;
		Token requestToken = service.getRequestToken();
		String temp = "";
		temp = JOptionPane.showInputDialog("Authorize at the URL below then replace the text in the text box with the PIN Twitter provides.\n", service.getAuthorizationUrl(requestToken));
		if(temp == null)
		{
			return null;
		}
		else
		{
			verifier = new Verifier(temp);
			Token accessToken = service.getAccessToken(requestToken, verifier);
			accountName = getAccountName(accessToken);
			if(StoreAccessToken(accountName, accessToken.toString()))
				return accountName;
			else
				return null;
		}
	}

	/**
	*	Stores the access token passed in for the account name specified.
	*	@param	accountName	The account to which the accessToken belongs, used for the name of the file that token is saved in.
	*	@param	accessToken	The access token for the account, stored for later use.
	*	@return			Success or failure of storing the token.
	**/
	public boolean StoreAccessToken(String accountName, String accessToken)
	{
		File f = new File("tokens");
		if(!f.exists())
		{
			f.mkdir();
		}
		if(f.isDirectory())
		{
			try
			{
				f = new File("tokens/" + accountName + ".usgs");
				DesEncrypter encrypter = new DesEncrypter(accountName);
				String encrypted = encrypter.encrypt(accessToken);
				FileOutputStream fop = new FileOutputStream(f);
				fop.write(encrypted.getBytes());	
				fop.flush();
				fop.close();
				return true;
			}
			catch(Exception e)
			{
				return false;
			}
		}
		return false;
	}

	/**
	*	Retrieves the access token for the account specified.
	*	@param accountName	Account for which to get the access token.
	*	@return			Returns the token linked to the account.
	**/
	public Token GetAccessToken(String accountName)
	{
		File tokenFile = new File("tokens/" + accountName + ".usgs");
		String strLine = "";
                if(tokenFile.exists())
                {
                        try
                        {
                                DataInputStream in = new DataInputStream(new FileInputStream(tokenFile));
                                BufferedReader br = new BufferedReader(new InputStreamReader(in));
				strLine = "";
				String str = "";
				while((str = br.readLine()) != null)
				{
					strLine = strLine + str;
				}
                                in.close();
				DesEncrypter encrypter = new DesEncrypter(accountName);
				strLine = encrypter.decrypt(strLine);
                        }
                        catch(Exception e)
                        {
                                logs.updateLogs("Error reading file: " + e.getMessage());
                        }
                }
                else
                {
                        logs.updateLogs("No token for " + accountName + " account");
                }
		//Parse strLine for token and secret
		//In the format of "Token[token , secret] possible stray \n as well.  Needs to ignore "Token", '[', ']', ',', ' ', '\n'
		byte b[] = strLine.getBytes();
		String tokenStr = "";
		String secretStr = "";
		boolean token = false;
		boolean secret = false;
		for(int i = 0; i < b.length; i++)
		{
			if((char)b[i] == '[')
			{
				token = true;
				secret = false;
			}
			else if((char)b[i] == ',')
			{
				token = false;
				secret = true;
			}
			else if((char)b[i] == ']')
			{
				token = false;
				secret = false;
			}
			else if((char)b[i] != ' ' && (char)b[i] != '\n')
			{
				if(token == true)
				{
					tokenStr = tokenStr + (char)b[i];
				}
				else if(secret == true)
				{
					secretStr = secretStr + (char)b[i];
				}
			}
		}
		Token accessToken = new Token(tokenStr,secretStr);
                return accessToken;
	}

	/**
	*	Queries Twitter to get information for the specified account.
	*	@param accountName	The account for which to query twitter.
	*	@return 		Success or failure of the query, considered a failure if it doesn't succeed in all three queries.
	**/
	public boolean QueryTwitter(String accountName)
	{
		Options options = new Options();
		options.ReadOptions();

		Token accessToken = GetAccessToken(accountName);

		//Direct Messages sent
		String str = "";
		String tempStr = null;
		int pageNumber = 1;
		do
		{
			tempStr = GetResponseString(new OAuthRequest(Verb.GET, DIRECT_MESSAGES_SENT_URL+"?since_id="+GetLastDMSentID(accountName)+"&count="+options.GetNumberOfTweets()+"&include_entities=t&page="+pageNumber), accessToken);
			if(tempStr == null)
				break;
			else
				str = str + tempStr;
			pageNumber++;
		}while(tempStr != null);
		if(!str.equals(""))
		{
			logs.updateLogs(ParseAndSaveSentDirectMessages(str, accountName, false) + " Direct Messages sent by " + accountName + " since last update.");
		}

		//Direct messages received
		str = "";
		tempStr = null;
		pageNumber = 1;
		do
		{
			tempStr = GetResponseString(new OAuthRequest(Verb.GET, DIRECT_MESSAGES_RECEIVED_URL+"?since_id="+GetLastDMReceivedID(accountName)+"&count="+options.GetNumberOfTweets()+"&include_entities=t&page="+pageNumber), accessToken);
			if(tempStr == null)
				break;
			else
				str = str + tempStr;
			pageNumber++;
		}while(tempStr != null);
		if(!str.equals(""))
		{
			logs.updateLogs(ParseAndSaveReceivedDirectMessages(str, accountName, false) + " Direct Messages received by " + accountName + " since last update.");
		}

		//Status updates
		str = "";
		tempStr = null;
		pageNumber = 1;
		do
		{
			if(options.GetIncludeReTweets())
				tempStr = GetResponseString(new OAuthRequest(Verb.GET, STATUSES_URL+"?since_id="+GetLastTweetID(accountName)+"&screen_name="+accountName+"&count="+options.GetNumberOfTweets()+"&include_entities=t&include_rts=t&page="+pageNumber), accessToken);
			else
				tempStr = GetResponseString(new OAuthRequest(Verb.GET, STATUSES_URL+"?since_id="+GetLastTweetID(accountName)+"&screen_name="+accountName+"&count="+options.GetNumberOfTweets()+"&include_entities=t&page="+pageNumber), accessToken);
			if(tempStr == null)
				break;
			else
				str = str + tempStr;
			pageNumber++;
		}while(tempStr != null);
		if(!str.equals(""))
		{
			logs.updateLogs(ParseAndSaveStatuses(str, accountName, false) + " Tweets by " + accountName + " since last update.");
		}
		return true;
	}

	/**
	*	Parses the information retrieved from a direct message sent request then saves it into a text file.  The save order is newest to oldest.
	*	@param r		The response received for the query, in xml format.
	*	@param accountName	The account for which the query belongs.
	*	@param older		Designates the function to append the file instead.
	*	@return			The number of messages gathered from Twitter, or -1 to signify that there was an error.
	**/
	private int ParseAndSaveSentDirectMessages(String r, String accountName, boolean older)
	{
		String[] messages = r.split("<direct_message>");
		int numberOfMessages = messages.length - 1;
		String[] sender = new String[numberOfMessages], recipient = new String[numberOfMessages], body = new String[numberOfMessages], id = new String[numberOfMessages], date = new String[numberOfMessages], entities = new String[numberOfMessages];
		File [] file_tmps = new File[0];
		File [] files = new File[0];

		for(int i = 0; i < numberOfMessages; i++)
		{
			sender[i] = "";
			recipient[i] = "";
			body[i] = "";
			id[i] = "";
			date[i] = "";
		}
		for(int i = 1; i < numberOfMessages + 1; i++)
		{
			String[] tokens = messages[i].split("<sender_screen_name>");
			tokens = tokens[1].split("</sender_screen_name>");
			sender[i-1] = tokens[0];
			tokens = messages[i].split("<recipient_screen_name>");
			tokens = tokens[1].split("</recipient_screen_name>");
			recipient[i-1] = tokens[0];
			tokens = messages[i].split("<text>");
			tokens = tokens[1].split("</text>");
			body[i-1] = tokens[0];
			tokens = messages[i].split("<id>");
			tokens = tokens[1].split("</id>");
			id[i-1] = tokens[0];
			tokens = messages[i].split("<created_at>");
			tokens = tokens[1].split("</created_at>");
			date[i-1] = tokens[0];
			tokens = messages[i].split("<entities>");
			tokens = tokens[1].split("</entities>");
			entities[i-1] = tokens[0];
		}

		File file_tmp;
		for(int i = 0; i < numberOfMessages; i++)
		{
			String[] tmpDate = date[i].split(" ");
			String msgMonth = tmpDate[1];
			String msgYear = tmpDate[5];
			File file = new File(location.feedLocation + "/" + accountName + "/" + msgYear + "/" + msgMonth + "/SentDirectMessages.txt");
			file_tmp = new File(location.feedLocation + "/" + accountName + "/" + msgYear + "/" + msgMonth + "/SentDirectMessages_tmp.txt");
			boolean check = false;
			for(int j = 0; j < files.length; j++)
				if(file == files[j])
					check = true;
			if(!check)
			{
				File[] tmps = new File[files.length + 1];
				File[] tmps2 = new File[file_tmps.length + 1];
				for(int j = 0; j < files.length; j++)
				{
					tmps[j] = files[j];
					tmps2[j] = file_tmps[j];
				}
				tmps[tmps.length - 1] = file;
				tmps2[tmps2.length - 1] = file_tmp;
				files = new File[tmps.length];
				file_tmps = new File[tmps2.length];
				for(int j = 0; j < tmps.length; j++)
				{
					files[j] = tmps[j];
					file_tmps[j] = tmps2[j];
				}
			}
                try
		{
			boolean success = false;
			if(location.checkFeedsLocation(""))
				if(location.checkFeedsLocation("/" + accountName))
					if(location.checkFeedsLocation("/" + accountName + "/" + msgYear))
						if(location.checkFeedsLocation("/" + accountName + "/" + msgYear + "/" + msgMonth))
							success = true;
			if(!success)
			{
				logs.updateLogs("File structure is broken");
				return -1;
			}
			if(!file.exists())
                	{
                	        file.createNewFile();
                	}
			else
                        {
				if(!file_tmp.exists())
				{
                                	FileInputStream fstream = new FileInputStream(file);
                                	DataInputStream in = new DataInputStream(fstream);
                                	BufferedReader br = new BufferedReader(new InputStreamReader(in));
                                	String strLine = "";
                                	BufferedWriter out = new BufferedWriter(new FileWriter(file_tmp));
                                	while((strLine = br.readLine()) != null)
                                	{
                                	        out.write(strLine+"\n");
                                	}
                                	in.close();
                                	out.close();
					file.delete();
					file.createNewFile();
				}
                        }
                        BufferedWriter out = new BufferedWriter(new FileWriter(file,true));
			if(older)
			{
				if(file_tmp.exists())
                        	{
                        	        FileInputStream fstream = new FileInputStream(file_tmp);
                        	        DataInputStream in = new DataInputStream(fstream);
                        	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        	        String strLine = "";
                        	        while((strLine = br.readLine()) != null)
                        	        {
                        	                out.write(strLine + "\n");
                        	        }
                        	        in.close();
                        	        file_tmp.delete();
                        	}
			}
			out.write("<message>\n");
			out.write(TO_STR + recipient[i] + "\n");
			out.write(FROM_STR + sender[i] + "\n");
			out.write(DATE_STR + date[i] + "\n");
			out.write(ID_STR + id[i] + "\n");
			out.write(TEXT_STR + body[i] + "\n");
			out.write(ENTITIES_STR + entities[i] + "\n");
			out.write("</message>\n");
			out.close();
			}
			catch(Exception e)
			{
				logs.updateLogs("Exception: " + e.getMessage());
			}
		}
		for(int i = 0; i < files.length; i++)
		{
			if(file_tmps[i].exists())
			{
				try
				{
					BufferedWriter out = new BufferedWriter(new FileWriter(files[i], true));
					BufferedReader in = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file_tmps[i]))));
					String strLine = "";
					while((strLine = in.readLine()) != null)
					{
						out.write(strLine + "\n");
					}
					in.close();
					out.close();
					file_tmps[i].delete();
				}
				catch(IOException e)
				{
					logs.updateLogs("Error cleaning up temp files: " + e.getMessage());
				}
			}
		}
		return numberOfMessages;
	}

	/**
	*	Parses the information retrieved from a direct message received request then saves it into a text file.  The save order is newest to oldest.
	*	@param r		The response received for the query, in xml format.
	*	@param accountName	The account for which the query belongs.
	*	@param older		Designates the function to append the file instead.
	*	@return			The number of messages gathered from Twitter or -1 to signify an error.
	**/
	private int ParseAndSaveReceivedDirectMessages(String r, String accountName, boolean older)
        {
                String[] messages = r.split("<direct_message>");
                int numberOfMessages = messages.length - 1;
                String[] sender = new String[numberOfMessages], recipient = new String[numberOfMessages], body = new String[numberOfMessages], id = new String[numberOfMessages], date = new String[numberOfMessages], entities = new String[numberOfMessages];
		File [] file_tmps = new File[0];
		File [] files = new File[0];

                for(int i = 0; i < numberOfMessages; i++)
                {
                        sender[i] = "";
                        recipient[i] = "";
                        body[i] = "";
                        id[i] = "";
                        date[i] = "";
                }
                for(int i = 1; i < numberOfMessages + 1; i++)
                {
                        String[] tokens = messages[i].split("<sender_screen_name>");
                        tokens = tokens[1].split("</sender_screen_name>");
                        sender[i-1] = tokens[0];
                        tokens = messages[i].split("<recipient_screen_name>");
                        tokens = tokens[1].split("</recipient_screen_name>");
                        recipient[i-1] = tokens[0];
                        tokens = messages[i].split("<text>");
                        tokens = tokens[1].split("</text>");
                        body[i-1] = tokens[0];
                        tokens = messages[i].split("<id>");
                        tokens = tokens[1].split("</id>");
                        id[i-1] = tokens[0];
                        tokens = messages[i].split("<created_at>");
                        tokens = tokens[1].split("</created_at>");
                        date[i-1] = tokens[0];
			tokens = messages[i].split("<entities>");
			tokens = tokens[1].split("</entities>");
			entities[i-1] = tokens[0];
                }

		File file_tmp;
		for(int i = 0; i < numberOfMessages; i++)
		{
			String[] tmpDate = date[i].split(" ");
			String msgMonth = tmpDate[1];
			String msgYear = tmpDate[5];
			File file = new File(location.feedLocation + "/" + accountName + "/" + msgYear + "/" + msgMonth + "/ReceivedDirectMessages.txt");
			file_tmp = new File(location.feedLocation + "/" + accountName + "/" + msgYear + "/" + msgMonth + "/ReceivedDirectMessages_tmp.txt");
			boolean check = false;
			for(int j = 0; j < files.length; j++)
				if(file == files[j])
					check = true;
			if(!check)
			{
				File[] tmps = new File[files.length + 1];
				File[] tmps2 = new File[file_tmps.length + 1];
				for(int j = 0; j < files.length; j++)
				{
					tmps[j] = files[j];
					tmps2[j] = file_tmps[j];
				}
				tmps[tmps.length - 1] = file;
				tmps2[tmps2.length - 1] = file_tmp;
				files = new File[tmps.length];
				file_tmps = new File[tmps2.length];
				for(int j = 0; j < tmps.length; j++)
				{
					files[j] = tmps[j];
					file_tmps[j] = tmps2[j];
				}
			}
                try
		{
			boolean success = false;
			if(location.checkFeedsLocation(""))
				if(location.checkFeedsLocation("/" + accountName))
					if(location.checkFeedsLocation("/" + accountName + "/" + msgYear))
						if(location.checkFeedsLocation("/" + accountName + "/" + msgYear + "/" + msgMonth))
							success = true;
			if(!success)
			{
				logs.updateLogs("File structure is broken");
				return -1;
			}
			if(!file.exists())
                	{
                	        file.createNewFile();
                	}
			else
                        {
				if(!file_tmp.exists())
				{
                                	FileInputStream fstream = new FileInputStream(file);
                                	DataInputStream in = new DataInputStream(fstream);
                                	BufferedReader br = new BufferedReader(new InputStreamReader(in));
                                	String strLine = "";
                                	BufferedWriter out = new BufferedWriter(new FileWriter(file_tmp));
                                	while((strLine = br.readLine()) != null)
                                	{
                                	        out.write(strLine+"\n");
                                	}
                                	in.close();
                                	out.close();
					file.delete();
					file.createNewFile();
				}
                        }
                        BufferedWriter out = new BufferedWriter(new FileWriter(file,true));
			if(older)
			{
				if(file_tmp.exists())
                        	{
                        	        FileInputStream fstream = new FileInputStream(file_tmp);
                        	        DataInputStream in = new DataInputStream(fstream);
                        	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        	        String strLine = "";
                        	        while((strLine = br.readLine()) != null)
                        	        {
                        	                out.write(strLine + "\n");
                        	        }
                        	        in.close();
                        	        file_tmp.delete();
                        	}
			}
			out.write("<message>\n");
			out.write(TO_STR + recipient[i] + "\n");
			out.write(FROM_STR + sender[i] + "\n");
			out.write(DATE_STR + date[i] + "\n");
			out.write(ID_STR + id[i] + "\n");
			out.write(TEXT_STR + body[i] + "\n");
			out.write(ENTITIES_STR + entities[i] + "\n");
			out.write("</message>\n");
			out.close();
                }
                catch(Exception e)
                {}
		}
		for(int i = 0; i < files.length; i++)
		{
			if(file_tmps[i].exists())
			{
				try
				{
					BufferedWriter out = new BufferedWriter(new FileWriter(files[i], true));
					BufferedReader in = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file_tmps[i]))));
					String strLine = "";
					while((strLine = in.readLine()) != null)
					{
						out.write(strLine + "\n");
					}
					in.close();
					out.close();
					file_tmps[i].delete();
				}
				catch(IOException e)
				{
					logs.updateLogs("Error cleaning up temp files: " + e.getMessage());
				}
			}
		}
		return numberOfMessages;
        }

	/**
	*	Parses the information retrieved from a statuses request then saves it into a text file.  The save order is newest to oldest.
	*	@param r		The response received for the query, in xml format.
	*	@param accountName	The account for which the query belongs.
	*	@param older		Designates the function to append the file instead.
	*	@return			The number of messages gathered from Twitter or -1 to signify an error.
	**/
	private int ParseAndSaveStatuses(String r, String accountName, boolean older)
	{
		String[] statuses = r.split("<status>");
		int numberOfStatuses = statuses.length - 1;
		String[] date = new String[numberOfStatuses], id = new String[numberOfStatuses], body = new String[numberOfStatuses], inReplyTo = new String[numberOfStatuses], user = new String[numberOfStatuses], entities = new String[numberOfStatuses];
		File [] file_tmps = new File[0];
		File [] files = new File[0];

		for(int i = 0; i < numberOfStatuses; i++)
		{
			date[i] = "";
			id[i] = ""; 
			body[i] = "";
			inReplyTo[i] = "";
			user[i] = "";
			entities[i] = "";
		}
		for(int i = 1; i < numberOfStatuses + 1; i++)
		{
			String[] tokens = statuses[i].split("<created_at>");
			tokens = tokens[1].split("</created_at>");
			date[i-1] = tokens[0];
			tokens = statuses[i].split("<id>");
                        tokens = tokens[1].split("</id>");
                        id[i-1] = tokens[0];
			tokens = statuses[i].split("<text>");
                        tokens = tokens[1].split("</text>");
                        body[i-1] = tokens[0];
			tokens = statuses[i].split("<in_reply_to_screen_name>");
                        tokens = tokens[1].split("</in_reply_to_screen_name>");
                        inReplyTo[i-1] = tokens[0];
			tokens = statuses[i].split("<screen_name>");
                        tokens = tokens[1].split("</screen_name>");
                        user[i-1] = tokens[0];
			tokens = statuses[i].split("<entities>");
			tokens = tokens[1].split("</entities>");
			entities[i-1] = tokens[0];
		}

		File file_tmp;
		for(int i = 0; i < numberOfStatuses; i++)
		{
			String[] tmpDate = date[i].split(" ");
			String msgMonth = tmpDate[1];
			String msgYear = tmpDate[5];
			File file = new File(location.feedLocation + "/" + accountName + "/" + msgYear + "/" + msgMonth + "/Tweets.txt");
			file_tmp = new File(location.feedLocation + "/" + accountName + "/" + msgYear + "/" + msgMonth + "/Tweets_tmp.txt");
			boolean check = false;
			for(int j = 0; j < files.length; j++)
				if(file == files[j])
					check = true;
			if(!check)
			{
				File[] tmps = new File[files.length + 1];
				File[] tmps2 = new File[file_tmps.length + 1];
				for(int j = 0; j < files.length; j++)
				{
					tmps[j] = files[j];
					tmps2[j] = file_tmps[j];
				}
				tmps[tmps.length - 1] = file;
				tmps2[tmps2.length - 1] = file_tmp;
				files = new File[tmps.length];
				file_tmps = new File[tmps2.length];
				for(int j = 0; j < tmps.length; j++)
				{
					files[j] = tmps[j];
					file_tmps[j] = tmps2[j];
				}
			}
                try
		{
			boolean success = false;
			if(location.checkFeedsLocation(""))
				if(location.checkFeedsLocation("/" + accountName))
					if(location.checkFeedsLocation("/" + accountName + "/" + msgYear))
						if(location.checkFeedsLocation("/" + accountName + "/" + msgYear + "/" + msgMonth))
							success = true;
			if(!success)
			{
				logs.updateLogs("File structure is broken");
				return -1;
			}
			if(!file.exists())
                	{
                	        file.createNewFile();
                	}
			else
                        {
				if(!file_tmp.exists())
				{
                                	FileInputStream fstream = new FileInputStream(file);
                                	DataInputStream in = new DataInputStream(fstream);
                                	BufferedReader br = new BufferedReader(new InputStreamReader(in));
                                	String strLine = "";
                                	BufferedWriter out = new BufferedWriter(new FileWriter(file_tmp));
                                	while((strLine = br.readLine()) != null)
                                	{
                                	        out.write(strLine+"\n");
                                	}
                                	in.close();
                                	out.close();
					file.delete();
					file.createNewFile();
				}
                        }
                        BufferedWriter out = new BufferedWriter(new FileWriter(file,true));
			if(older)
			{
				if(file_tmp.exists())
                        	{
                        	        FileInputStream fstream = new FileInputStream(file_tmp);
                        	        DataInputStream in = new DataInputStream(fstream);
                        	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        	        String strLine = "";
                        	        while((strLine = br.readLine()) != null)
                        	        {
                        	                out.write(strLine + "\n");
                        	        }
                        	        in.close();
                        	        file_tmp.delete();
                        	}
			}
			out.write("<message>\n");
			out.write(TO_STR + inReplyTo[i] + "\n");
			out.write(FROM_STR + user[i] + "\n");
			out.write(DATE_STR + date[i] + "\n");
			out.write(ID_STR + id[i] + "\n");
			out.write(TEXT_STR + body[i] + "\n");
			out.write(ENTITIES_STR + entities[i] + "\n");
			out.write("</message>\n");
			out.close();
		}
		catch(Exception e)
		{}
		}
		for(int i = 0; i < files.length; i++)
		{
			if(file_tmps[i].exists())
			{
				try
				{
					BufferedWriter out = new BufferedWriter(new FileWriter(files[i],true));
					BufferedReader in = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file_tmps[i]))));
					String strLine = "";
					while((strLine = in.readLine()) != null)
					{
						out.write(strLine + "\n");
					}
					in.close();
					out.close();
					file_tmps[i].delete();
				}
				catch(IOException e)
				{
					logs.updateLogs("Error cleaning up temp files: " + e.getMessage());
				}
			}
		}
		return numberOfStatuses;
	}

	/**
	*	Retrieves the ID of the last status update saved for the account specified.
	*	@param	accountName	The account for which to retreive the information from.
	*	@param	older		Designated to retreived the oldest ID instead of newest.
	*	@return			Returns a string containing the last tweet ID.
	**/
	public String GetLastTweetID(String accountName)
	{
		String fileName = getNewestFileName(accountName, "Tweets.txt");
		File file;
		if(fileName == null)
			return "1";
		else
			file = new File(fileName);
		//The most recent file should be selected, parse for ID
		long id = 1;
		try
		{
			String [] storage = new String[0];
			String [] tempArray = new String[0];
			long [] ids = new long[0];
                        FileInputStream fstream = new FileInputStream(file);
                	DataInputStream in = new DataInputStream(fstream);
                	BufferedReader br = new BufferedReader(new InputStreamReader(in));
                       	String strLine = "";
                       	String str = "";
                       	while((strLine = br.readLine()) != null)
			{
				str = str + strLine + "\n";
			}
			storage = str.split("<ID>");
			ids = new long[storage.length - 1];
			int loc = 0;
			for(int i = 0; i < ids.length; i++)
			{
				loc = 0;
				for(int j = 1; j < storage.length; j++)
				{
					tempArray = storage[j].split("\n");
					if(id < Long.valueOf(tempArray[0]))
					{
						id = Long.valueOf(tempArray[0]);
						loc = j;
					}
				}
				ids[i] = id;
				storage[loc] = "0\n";
				id = 1;
			}
			id = ids[0];
			in.close();
                }
                catch(Exception e)
                {
			logs.updateLogs("Error: " + e.getMessage());
		}
		return String.valueOf(id);
	}

	/**
	*	Retrieves the ID of the last direct message sent saved for the account specified.
	*	@param	accountName	The account for which to retreive the information from.
	*	@param	older		Designated to retreived the oldest id instead of the newest.
	*	@return			Returns a string containing the last direct message sent ID.
	**/
	public String GetLastDMSentID(String accountName)
	{
		String fileName = getNewestFileName(accountName, "SentDirectMessages.txt");
		File file;
		if(fileName == null)
			return "1";
		else
			file = new File(fileName);
		//The most recent file should be selected, parse for ID
		long id = 1;
		try
		{
			String [] storage = new String[0];
			String [] tempArray = new String[0];
			long [] ids = new long[0];
                        FileInputStream fstream = new FileInputStream(file);
                	DataInputStream in = new DataInputStream(fstream);
                	BufferedReader br = new BufferedReader(new InputStreamReader(in));
                       	String strLine = "";
                       	String str = "";
                       	while((strLine = br.readLine()) != null)
			{
				str = str + strLine + "\n";
			}
			storage = str.split("<ID>");
			ids = new long[storage.length - 1];
			int loc = 0;
			for(int i = 0; i < ids.length; i++)
			{
				loc = 0;
				for(int j = 1; j < storage.length; j++)
				{
					tempArray = storage[j].split("\n");
					if(id < Long.valueOf(tempArray[0]))
					{
						id = Long.valueOf(tempArray[0]);
						loc = j;
					}
				}
				ids[i] = id;
				storage[loc] = "0\n";
				id = 1;
			}
			id = ids[0];
			in.close();
                }
                catch(Exception e)
                {
			logs.updateLogs("Error: " + e.getMessage());
		}
		return String.valueOf(id);
	}

	/**
	*	Retrieves the ID of the last direct message received saved for the account specified.
	*	@param	accountName	The account for which to retreive the information from.
	
	*	@param	older		Designated to retreived the oldest id instead of the newest.
	*	@return			Returns a string containing the last direct message received ID.
	**/
	public String GetLastDMReceivedID(String accountName)
	{
		String fileName = getNewestFileName(accountName, "ReceivedDirectMessages.txt");
		File file;
		if(fileName == null)
			return "1";
		else
		{
			file = new File(fileName);
		}
		//The most recent file should be selected, parse for ID
		long id = 1;
		try
		{
			String [] storage = new String[0];
			String [] tempArray = new String[0];
			long [] ids = new long[0];
                        FileInputStream fstream = new FileInputStream(file);
                	DataInputStream in = new DataInputStream(fstream);
                	BufferedReader br = new BufferedReader(new InputStreamReader(in));
                       	String strLine = "";
                       	String str = "";
                       	while((strLine = br.readLine()) != null)
			{
				str = str + strLine + "\n";
			}
			storage = str.split("<ID>");
			ids = new long[storage.length - 1];
			int loc = 0;
			for(int i = 0; i < ids.length; i++)
			{
				loc = 0;
				for(int j = 1; j < storage.length; j++)
				{
					tempArray = storage[j].split("\n");
					if(id < Long.valueOf(tempArray[0]))
					{
						id = Long.valueOf(tempArray[0]);
						loc = j;
					}
				}
				ids[i] = id;
				storage[loc] = "0\n";
				id = 1;
			}
			id = ids[0];
			in.close();
                }
                catch(Exception e)
                {
			logs.updateLogs("Error: " + e.getMessage());
		}
		return String.valueOf(id);
	}

	/**
	*	getNewestFileName function.  This function is designed to return the file name for the newest instance of fileName.
	*	@param	accountName	The account underwhich to look for the fileName
	*	@param	fileName	The file name to search for.
	*	@return			Returns a string corresponding to the location of the most recent instance of fileName for the accountName.
	**/
	private String getNewestFileName(String accountName, String fileName)
	{
		File file = new File(location.feedLocation + "/" + accountName);
		String[] years = (new File(location.feedLocation + "/" + accountName)).list();
		int year = 0;
		boolean exists = true;
		int month = -1;
		int loc = -1;
		String[] months = new String[0];

		if(years.length <= 0)
			return null;
		if(years == null)
			return null;
		do
		{
			loc = -1;
			year = -1;
			for(int i = 0; i < years.length; i++)
			{
				if(Integer.valueOf(years[i]) > year)
				{
					year = Integer.valueOf(years[i]);
					loc = i;
				}
			}
			if(loc >= 0 && loc < years.length)
				years[loc] = "-1";
			if(year == -1)
			{
				exists = false;
				break;
			}
			months = (new File(location.feedLocation + "/" + accountName + "/" + String.valueOf(year))).list();
			int[] temp = new int[months.length];
			for(int i = 0; i < temp.length; i++)
			{
				temp[i] = -1;
				for(int j = 0; j < MONTHS.length; j++)
				{
					if(months[i].equals(MONTHS[j]))
					{
						temp[i] = j;
						break;
					}
				}
			}
			do
			{
				loc = -1;
				month = -1;
				for(int i = 0; i < temp.length; i++)
				{
					if(temp[i] > month)
					{
						month = temp[i];
						loc = i;
					}
				}
				if(loc >= 0 && loc < temp.length)
					temp[loc] = -1;
				if(month == -1)
				{
					exists = false;
					break;
				}
				file = new File(location.feedLocation + "/" + accountName + "/" + String.valueOf(year) + "/" + MONTHS[month] + "/" + fileName);
				exists = file.exists();
			}while(!exists);
		}while(!exists);
		if(!exists)
		{//There is no record of any received DMs
			return null;
		}
		else
			return file.toString();
	}

	/**
	*	getAccountName function.  This function is designed to get the account new for a corresponding accessToken.
	*	@param	accessToken	The access token for which to get the account name.
	*	@return			Returns the account name corresponding to the access token.
	**/
	private String getAccountName(Token accessToken)
	{
		OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		Response response = request.send();
		if(response.getCode() < 200 || response.getCode() >= 400)
		{
			logs.updateLogs("Returned status code " + response.getCode() + " while verifying credentials.");
			return null;
		}
		else
		{
			String[] temp = response.getBody().split("<span id=\"screen-name\">");
			temp = temp[1].split("</span>");
			if(temp == null)
				return null;
			else
				return temp[0].trim();
		}
	}

	/**
	*	FirstTimeRun function.  This function should be run the first time an account is added to the following list, it verifies credentials then gathers all old posts.
	*	@param	accountName	The account being added.
	*	@return			Returns success (true) or failure (false).
	**/
	public boolean FirstTimeRun(String accountName)
	{
		Token accessToken = GetAccessToken(accountName);
		OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		Response response = request.send();
		if(response.getCode() < 200 || response.getCode() >= 400)
		{
			logs.updateLogs(accountName + " returned status code " + response.getCode() + " on account verification.");
			return false;
		}

		Options options = new Options();
		options.ReadOptions();
		boolean loop = false;
		int pageNumber = 1;
		int trys = 0;
		int type = 0;
		boolean status = true;
		for(type = 0; type < 3; type++)
		{
			pageNumber = 1;
			do
			{
				if(type == 0)
					request = new OAuthRequest(Verb.GET, DIRECT_MESSAGES_SENT_URL + "?since_id=1&count=200&include_entities=t&page=" + pageNumber);
				else if(type == 1)
					request = new OAuthRequest(Verb.GET, DIRECT_MESSAGES_RECEIVED_URL + "?since_id=1&count=200&include_entities=t&page=" + pageNumber);
				else if(type == 2)
				{
					String str = STATUSES_URL + "?since_id=1&screen_name=" + accountName + "&count=200&include_entities=t&page=" + pageNumber;
					if(options.GetIncludeReTweets())
						str = str + "&include_rts=t";
					request = new OAuthRequest(Verb.GET, str);
				}
				else
				{
					logs.updateLogs("An unknown type was entered into the FirstTimeRun function.");
					status = false;
					break;
				}
				service.signRequest(accessToken, request);
				response = request.send();
				if(response.getCode() >= 200 && response.getCode() < 400)
				{
					int temp = 0;
					if(type == 0)
						temp = ParseAndSaveSentDirectMessages(response.getBody(), accountName, true);
					else if(type == 1)
						temp = ParseAndSaveReceivedDirectMessages(response.getBody(), accountName, true);
					else if(type == 2)
						temp = ParseAndSaveStatuses(response.getBody(), accountName, true);
					
					if(temp <= 0)//no new messages or error
						loop = false;
					else
						loop = true;
					logs.updateLogs("FirstTimeRunOutput: Type:" + type + " Page:" + pageNumber + " NumberOfFeeds:" + temp);
					pageNumber++;
					trys = 0;
				}
				else if(response.getCode() == 502 || response.getCode() == 503 || response.getCode() == 420)
				{
					if(trys > 5)
					{
						status = false;
						break;
					}
					logs.updateLogs("Encountered response code " + response.getCode() + " waiting a 1/2 second before continuing.");
					try
					{
						Thread.sleep(500);
					}
					catch(InterruptedException ie)
					{
						logs.updateLogs(ie.getMessage());
					}
					loop = true;
					trys++;
				}
				else
				{
					logs.updateLogs(accountName + " returned status code " + response.getCode() + " FirstTimeRun type " + type + " request.\nWith error message: " + response.getBody());
					status = false;
					break;
				}
			}while(loop);
		}
		return status;
	}

	/**
	*	GetResponseString function.  This function is designed to take a request and accessToken and return the response in String form.  If there were no messages in the response then null is returned;
	*	@param	request		The request to be sent to Twitter.
	*	@param	accessToken	The accessToken for the account the request should link to.
	*	@return			Returns the body of the response from Twitter as long as there is one or more items to retrieve.
	**/
	public String GetResponseString(OAuthRequest request, Token accessToken)
	{
		String str = "";
		String [] temp;
		boolean loop = false;
		boolean empty = false;
		int trys = 0;
		do
		{
			service.signRequest(accessToken, request);
			Response response = request.send();
			if(response.getCode() >= 200 && response.getCode() < 400)
			{
				str = response.getBody();
				//<status>
				temp = str.split("<status>");
				if(temp.length > 1)
					empty = false;
				else
					empty = true;
				//<direct_message>
				temp = str.split("<direct_message>");
				if(temp.length > 1)
					empty = false;
				if(empty)
					return null;
				loop = false;
				trys = 0;
			}
			else if(response.getCode() == 502 || response.getCode() == 503 || response.getCode() == 420)
			{
				if(trys >= 3)
				{
					str = null;
					break;
				}
				logs.updateLogs("Encountered response code " + response.getCode() + " waiting a 1/2 second before continuing.");
				try
				{
					Thread.sleep(500);
				}
				catch(InterruptedException ie)
				{
					logs.updateLogs(ie.getMessage());
				}
				loop = true;
				trys++;
			}
			else
			{
				logs.updateLogs("Returned status code " + response.getCode() + " on update.\nWith error message: " + response.getBody());
				return null;
			}
		}while(loop);
		return str;
	}
}
