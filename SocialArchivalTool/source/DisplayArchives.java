package code;

/**	@author amurdock	**/

import javax.swing.*;
import javax.swing.event.*;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Container;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**	The DisplayArchives internal frame class.  This class will be used to display the archived information for the account selected in the ViewAccounts frame, then link back to the ViewAccounts frame as a way of closing.	**/

public class DisplayArchives extends JPanel implements ActionListener, ListSelectionListener, ComponentListener, HyperlinkListener
{
	JLabel accountLabel;
	JScrollPane yearScrollingList, monthScrollingList, fileScrollingList, scrollingTextArea;
	JTextArea textField;
	JButton closeButton; 
	JList yearList, monthList, fileList;
	String account;
	String feedLocation = "";
	FeedsLocation location = new FeedsLocation();
	Insets insets;
	Logs logs = new Logs();

	/**
	*	Class constructor
	*	@param width	The width of the new frame.
	*	@param height	The height of the new frame.
	*	@param accountName	The accountName for which to display the archives.
	**/
	public DisplayArchives(int width, int height, String accountName)
	{
		//Setup Panel
		setOpaque(true);
		setSize(width, height);
		setVisible(true);
		Dimension size;
		insets = getInsets();
		setLayout(null);
		account = accountName;
		addComponentListener(this);
		//End Panel setup

		//Initialize feedsLocation
		location.getFeedsLocation();
		feedLocation = location.feedLocation;
		//End feedsLocation initialization

		//Setup components
		yearList = getYearList(account);
		yearList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		yearList.setSelectedIndex(0);
		yearList.addListSelectionListener(this);

		monthList = getMonthList(account, String.valueOf(yearList.getSelectedValue()));
		monthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		monthList.setSelectedIndex(0);
		monthList.addListSelectionListener(this);

		fileList = getFilesList(account, String.valueOf(yearList.getSelectedValue()), String.valueOf(monthList.getSelectedValue()));
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.setSelectedIndex(0);
		fileList.addListSelectionListener(this);

		accountLabel = new JLabel(account + "'s Archives");
		add(accountLabel);
		size = accountLabel.getPreferredSize();
		accountLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + 25, size.width, size.height);

		closeButton = new JButton("Close Archives");
		add(closeButton);
		closeButton.addActionListener(this);
		closeButton.setBounds(insets.left + getWidth()/2 - 100, insets.top + getHeight() - 50, 200, 25);

		scrollingTextArea = new JScrollPane(textField);
		scrollingTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollingTextArea);
		scrollingTextArea.setBounds(insets.left + 100, insets.top + 50, 3*(getWidth()-200)/4, getHeight() - 100);
		updateTextArea();

		yearScrollingList = new JScrollPane(yearList);
		add(yearScrollingList);
		yearScrollingList.setBounds(insets.left + 100 + 3*(getWidth()-200)/4, insets.top + 50, (getWidth()-200)/4, (getHeight() - 100)/3);

		monthScrollingList = new JScrollPane(monthList);
		add(monthScrollingList);
		monthScrollingList.setBounds(insets.left + 100 + 3*(getWidth()-200)/4, insets.top + 50 + ((getHeight() - 100)/3), (getWidth()-200)/4, (getHeight() - 100)/3);

		fileScrollingList = new JScrollPane(fileList);
		add(fileScrollingList);
		fileScrollingList.setBounds(insets.left + 100 + 3*(getWidth()-200)/4, insets.top + 50 + 2*((getHeight() - 100)/3), (getWidth()-200)/4, (getHeight() - 100)/3);
		//End components setup
	}

	/**
	*	Overload of actionPerformed.
	**/
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == closeButton)
		{
			Container parent = getParent();
			parent.add(new ViewAccounts(getWidth(), getHeight()));
			parent.remove(this);
		}
	}

	/**
	*	Gets a list of all the year folders for the given account.
	*	@param	accountName	The account for which to gather the list of folders.
	*	@return			JList of all year folders for the account.
	**/
	public JList getYearList(String accountName)
	{
		JList list;
		File feedsDir = new File(feedLocation + "/" + account);
		String[] chld = feedsDir.list();
		int[] temp;
		if(chld == null)
		{
			chld = new String[1];
			chld[0] = "";
		}
		else
		{
			temp = new int[chld.length];
			for(int i = 0; i < temp.length; i++)
			{
				int loc = -1;
				temp[i] = -1;
				for(int j = 0; j < chld.length; j++)
				{
					if(Integer.valueOf(chld[j]) != -1 && Integer.valueOf(chld[j]) > temp[i])
					{
						temp[i] = Integer.valueOf(chld[j]);
						loc = j;
					}
				}
				if(loc != -1)
				{
					chld[loc] = "-1";
				}
			}
			chld = new String[temp.length];
			for(int i = 0; i < temp.length; i++)
			{
				chld[i] = String.valueOf(temp[i]);
			}
		}
		list = new JList(chld);
		return list;
	}

	/**
	*	Gets a list of all the month folders for the year folder under the given account.
	*	@param	accountName	The account for which to gather the list of folders.
	*	@param	year		The year underwhich you are gathering the list of month folders.
	*	@return			JList of all month folders for the given year and account.
	**/
	public JList getMonthList(String accountName, String year)
	{
		JList list;
		File feedsDir = new File(feedLocation + "/" + account + "/" + year);
		String[] chld = feedsDir.list();
		String[] temp;
		if(chld == null)
		{
			chld = new String[1];
			chld[0] = "";
		}
		else
		{
			temp = new String[chld.length];
			for(int i = 0; i < chld.length; i++)
			{
				temp[i] = "";
				for(int j = 0; j < OAuth.MONTHS.length; j++)
				{
					for(int k = 0; k < chld.length; k++)
					{
						if(chld[k].equals(OAuth.MONTHS[j]))
						{
							temp[i] = chld[k];
							chld[k] = "";
							break;
						}
					}
					if(!temp[i].equals(""))
					{
						break;
					}
				}
			}
			chld = new String[temp.length];
			chld = temp;
		}
		list = new JList(chld);
		return list;
	}

	/**
	*	Gets a list of all the files in the folder for the given account then returns it in a JList.
	*	@param accountName	The account for which to gather the list of files.
	*	@param	year		The year underwhich you are gathering the list of files.
	*	@param	month		The month underwhich you are gathering the list of files.
	*	@return			JList of all the files in the selected month folder for the selected year.
	**/
	public JList getFilesList(String accountName, String year, String month)
	{
		JList list;
		File feedsDir = new File(feedLocation + "/" + account + "/" + year + "/" + month);
		String[] chld = feedsDir.list();
		if(chld == null)
		{
			chld = new String[1];
			chld[0] = "";
		}
		list = new JList(chld);
		return list;
	}

	/**
	*	Overload of the valueChanged function.
	**/
	public void valueChanged(ListSelectionEvent e)
	{
		if(e.getSource() == yearList)
		{
			monthList = getMonthList(account, String.valueOf(yearList.getSelectedValue()));
			monthList.setSelectedIndex(0);
			monthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			monthList.addListSelectionListener(this);

			fileList = getFilesList(account, String.valueOf(yearList.getSelectedValue()), String.valueOf(monthList.getSelectedValue()));
			fileList.setSelectedIndex(0);
			fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			fileList.addListSelectionListener(this);

			remove(monthScrollingList);
			monthScrollingList = new JScrollPane(monthList);
			add(monthScrollingList);
			monthScrollingList.setBounds(insets.left + 100 + 3*(getWidth()-200)/4, insets.top + 50 + ((getHeight() - 100)/3), (getWidth()-200)/4, (getHeight() - 100)/3);

			remove(fileScrollingList);
			fileScrollingList = new JScrollPane(fileList);
			add(fileScrollingList);
			fileScrollingList.setBounds(insets.left + 100 + 3*(getWidth()-200)/4, insets.top + 50 + 2*((getHeight() - 100)/3), (getWidth()-200)/4, (getHeight() - 100)/3);
		}
		else if(e.getSource() == monthList)
		{
			fileList = getFilesList(account, String.valueOf(yearList.getSelectedValue()), String.valueOf(monthList.getSelectedValue()));
			fileList.setSelectedIndex(0);
			fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			fileList.addListSelectionListener(this);

			remove(fileScrollingList);
			fileScrollingList = new JScrollPane(fileList);
			add(fileScrollingList);
			fileScrollingList.setBounds(insets.left + 100 + 3*(getWidth()-200)/4, insets.top + 50 + 2*((getHeight() - 100)/3), (getWidth()-200)/4, (getHeight() - 100)/3);
		}
		updateTextArea();
		validate();
	}

	/**
	*	Updates the text area based on information saved in the file selected in the jlist variable.
	*	@param jlist	List used to update the text area
	**/
	public void updateTextArea()
	{
		String tempStr = "";
		int numberOfMessages = 0;
		String [] messages = new String[0];
		String [] tempStrArray = new String[0];
		String [] tempStrArray2 = new String[0];
		if(yearList.getSelectedValue() == null || monthList.getSelectedValue() == null || fileList.getSelectedValue() == null)
		{
		}
		else
		{
			File f = new File(feedLocation + "/" +  account + "/" + yearList.getSelectedValue().toString() + "/" + monthList.getSelectedValue().toString() + "/" + fileList.getSelectedValue().toString());
			byte b;

			try
			{
				FileInputStream finp = new FileInputStream(f);
				while((b = (byte)finp.read()) > -1)
				{
					tempStr = tempStr + (char)b;
				}
				finp.close();
			}
			catch(IOException exception)
			{
				logs.updateLogs("IOException: " + exception.getMessage());
			}
		}

		//Parse tempStr into messages
		if(tempStr.equals(""))
		{//No file or nothing in the file.
			numberOfMessages = 0;
		}
		else
		{//Start parsing
			messages = tempStr.split("<message>");//Each of these is a message with </message> as the last portion of it
			for(int i = 0; i < messages.length; i++)
			{
				if(!messages[i].equals(""))
				{
					tempStrArray2 = new String[tempStrArray.length];
					for(int j = 0; j < tempStrArray.length; j++)
						tempStrArray2[j] = tempStrArray[j];
					tempStrArray = new String[tempStrArray.length + 1];
					for(int j = 0; j < tempStrArray2.length; j++)
						tempStrArray[j] = tempStrArray2[j];
					tempStrArray[tempStrArray.length - 1] = messages[i];
				}
			}
			messages = new String[tempStrArray.length];
			for(int i = 0; i < tempStrArray.length; i++)
			{
				messages[i] = tempStrArray[i];
			}
			numberOfMessages = messages.length;
		}

		JEditorPane [] display = new JEditorPane[numberOfMessages];
		JPanel desktop = new JPanel();
		desktop.setOpaque(true);
		desktop.setVisible(true);
		desktop.setLayout(new GridLayout(messages.length, 1));

		for(int i = 0; i < numberOfMessages; i++)
		{
			String to = "";
			String from = "";
			String date = "";
			String text = "";
			String id = "";
			String[] user_mentions = new String[0];
			String[] urls = new String[0];
			String[] hashtags = new String[0];
			int [] startOfMention = new int[0], endOfMention = new int[0], startOfUrl = new int[0], endOfUrl = new int[0], startOfHashtag = new int[0], endOfHashtag = new int[0];
			String[] expandedUrl = new String[0];
			String[] displayUrls = new String[0];
			//for(int j = 0; j < tempStrArray.length; j++)
			{
				tempStrArray2 = messages[i].split(OAuth.TO_STR);
				if(tempStrArray2.length > 1)
				{
					tempStrArray2 = tempStrArray2[1].split("\n");
					String temp2 = tempStrArray2[0];
					if(!temp2.equals(""))
						to = "@"+"<a href=\"http://twitter.com/" + temp2 + "\">" + temp2 + "</a>";
					else
						to = temp2;
				}
				tempStrArray2 = messages[i].split(OAuth.FROM_STR);
				if(tempStrArray2.length > 1)
				{
					tempStrArray2 = tempStrArray2[1].split("\n");
					String temp2 = tempStrArray2[0];
					from = "<font color=\"blue\" size=\"6\"><a href=\"http://twitter.com/" + temp2 + "\">" + temp2 + "</a></font>";
				}
				tempStrArray2 = messages[i].split(OAuth.DATE_STR);
				if(tempStrArray2.length > 1)
				{
					tempStrArray2 = tempStrArray2[1].split("\n");
					date = tempStrArray2[0];
//					boolean offset = false;
//					char[] tempChars = tempStrArray2.toCharArray();
/*					for(int j = 0; j < tempChars.length; j++)
					{
						if(tempChars[j] == '+' || tempChars[j] == '-')
						{
							offset = true;
						}
						if(!offset)
						{
							date = date + tempChars[j];
						}
						else
						{
							if(tempChars[j] == ' ')
								offset = false;
						}
					}
*/
				}
				tempStrArray2 = messages[i].split(OAuth.TEXT_STR);
				if(tempStrArray2.length > 1)
				{
					tempStrArray2 = tempStrArray2[1].split("\n");
					String temp2 = tempStrArray2[0];
					text = temp2;
				}
				tempStrArray2 = messages[i].split(OAuth.ID_STR);
				if(tempStrArray2.length > 1)
				{
					tempStrArray2 = tempStrArray2[1].split("\n");
					String temp2 = tempStrArray2[0];
					id = temp2;
				}
				tempStrArray2 = messages[i].split(OAuth.ENTITIES_STR);
				if(tempStrArray2 != null)
				{//pull out the entity information and alter the text as needed.  This will always be gathered after the text of the message and there will never be information here that isn't covered in the text of the message.
					tempStrArray2 = tempStrArray2[1].split("</message>");
					String temp2 = "";
					String [] parseArray = tempStrArray2[0].split("<user_mentions>");
					if(parseArray.length > 1)
					{//At least 1 user mention
						parseArray = parseArray[1].split("</user_mentions>");
						temp2 = parseArray[0];
						parseArray = temp2.split("<user_mention end=\"");
						startOfMention = new int[parseArray.length - 1];
						endOfMention = new int[parseArray.length - 1];
						user_mentions = new String[parseArray.length - 1];
						for(int k = 0; k < endOfMention.length; k++)
						{
							String[] parseArray2 = parseArray[k+1].split("\"");
							endOfMention[k] = Integer.valueOf(parseArray2[0]);
							startOfMention[k] = Integer.valueOf(parseArray2[2]);
							parseArray2 = parseArray[k+1].split("<screen_name>");
							parseArray2 = parseArray2[1].split("</screen_name>");
							user_mentions[k] = parseArray2[0];
						}
					}
					parseArray = tempStrArray2[0].split("<urls>");
					if(parseArray.length > 1)
					{//At least 1 url
						parseArray = parseArray[1].split("</urls>");
						temp2 = parseArray[0];
						parseArray = temp2.split("<url end=\"");
						startOfUrl = new int[parseArray.length - 1];
						endOfUrl = new int[parseArray.length - 1];
						urls = new String[parseArray.length - 1];
						displayUrls = new String[parseArray.length - 1];
						expandedUrl = new String[parseArray.length - 1];
						for(int k = 0; k < endOfUrl.length; k++)
						{
							String[] parseArray2 = parseArray[k+1].split("\"");
							endOfUrl[k] = Integer.valueOf(parseArray2[0]);
							startOfUrl[k] = Integer.valueOf(parseArray2[2]);
							parseArray2 = parseArray[k+1].split("<display_url>");
							if(parseArray2.length <= 1)
							{//no display url
								displayUrls[k] = "";
							}
							else
							{
								parseArray2 = parseArray2[1].split("</display_url>");
								displayUrls[k] = parseArray2[0];
							}

							parseArray2 = parseArray[k+1].split("<expanded_url>");
							if(parseArray2.length <= 1)
							{//no expanded url
								expandedUrl[k] = "";
							}
							else
							{
								parseArray2 = parseArray2[1].split("</expanded_url>");
								expandedUrl[k] = parseArray2[0];
							}

							parseArray2 = parseArray[k+1].split("<url>");
							if(parseArray2.length <= 1)
							{//no display url
								urls[k] = "";
							}
							else
							{
								parseArray2 = parseArray2[1].split("</url>");
								urls[k] = parseArray2[0];
							}
						}
					}
					parseArray = tempStrArray2[0].split("<hashtags>");
					if(parseArray.length > 1)
					{//At least 1 hashtag
						parseArray = parseArray[1].split("</hashtags>");
						temp2 = parseArray[0];
						parseArray = temp2.split("<hashtag end=\"");
						startOfHashtag = new int[parseArray.length - 1];
						endOfHashtag = new int[parseArray.length - 1];
						hashtags = new String[parseArray.length - 1];
						for(int k = 0; k < endOfHashtag.length; k++)
						{
							String[] parseArray2 = parseArray[k+1].split("\"");
							endOfHashtag[k] = Integer.valueOf(parseArray2[0]);
							startOfHashtag[k] = Integer.valueOf(parseArray2[2]);
							parseArray2 = parseArray[k+1].split("<text>");
							parseArray2 = parseArray2[1].split("</text>");
							hashtags[k] = parseArray2[0];
						}
					}
					
				}
			}
			int[] tempStart = new int[startOfMention.length], tempEnd = new int[endOfMention.length];
			int tempInt = 0, loc = 0;
			for(int j = 0; j < endOfMention.length; j++)
			{//Bubble sort, largest to smallest
				loc = 0;
				tempInt = 0;
				for(int k = 0; k < endOfMention.length; k++)
				{
					if(endOfMention[k] > tempInt)
					{
						tempInt = endOfMention[k];
						loc = k;
					}
				}
				tempStart[j] = startOfMention[loc];
				tempEnd[j] = endOfMention[loc];
				startOfMention[loc] = -1;
				endOfMention[loc] = -1;
			}
			for(int j = 0; j < tempStart.length; j++)
			{
				startOfMention[tempStart.length - j - 1] = tempStart[j];
				endOfMention[tempStart.length - j - 1] = tempEnd[j];
			}

			tempStart = new int[startOfUrl.length];
			tempEnd = new int[endOfUrl.length];
			tempInt = 0;
			loc = 0;
			for(int j = 0; j < endOfUrl.length; j++)
			{//Bubble sort, largest to smallest
				loc = 0;
				tempInt = 0;
				for(int k = 0; k < endOfUrl.length; k++)
				{
					if(endOfUrl[k] > tempInt)
					{
						tempInt = endOfUrl[k];
						loc = k;
					}
				}
				tempStart[j] = startOfUrl[loc];
				tempEnd[j] = endOfUrl[loc];
				startOfUrl[loc] = -1;
				endOfUrl[loc] = -1;
			}
			for(int j = 0; j < tempStart.length; j++)
			{
				startOfUrl[tempStart.length - j - 1] = tempStart[j];
				endOfUrl[tempStart.length - j - 1] = tempEnd[j];
			}

			tempStart = new int[startOfHashtag.length];
			tempEnd = new int[endOfHashtag.length];
			tempInt = 0;
			loc = 0;
			for(int j = 0; j < endOfHashtag.length; j++)
			{//Bubble sort, largest to smallest
				loc = 0;
				tempInt = 0;
				for(int k = 0; k < endOfHashtag.length; k++)
				{
					if(endOfHashtag[k] > tempInt)
					{
						tempInt = endOfHashtag[k];
						loc = k;
					}
				}
				tempStart[j] = startOfHashtag[loc];
				tempEnd[j] = endOfHashtag[loc];
				startOfHashtag[loc] = -1;
				endOfHashtag[loc] = -1;
			}
			for(int j = 0; j < tempStart.length; j++)
			{
				startOfHashtag[tempStart.length - j - 1] = tempStart[j];
				endOfHashtag[tempStart.length - j - 1] = tempEnd[j];
			}
			//Now all three are sorted smallest to largest, start with the largest and work backwards in the text inserting information as needed.  This is done so that the start and end information from <entities> doesn't have to be altered.
			int hashtagLoc = endOfHashtag.length - 1;
			int urlLoc = endOfUrl.length - 1;
			int mentionLoc = endOfMention.length - 1;
			boolean mention = false, url = false, hashtag = false;
			char[] parsingChars = new char[0];
			while(hashtagLoc >= 0 || urlLoc >= 0 || mentionLoc >= 0)
			{
				mention = false;
				url = false;
				hashtag = false;
				if(hashtagLoc >= 0)
				{
					if(urlLoc >= 0)
					{
						if(endOfHashtag[hashtagLoc] > endOfUrl[urlLoc])
						{
							if(mentionLoc >= 0)
							{
								if(endOfHashtag[hashtagLoc] > endOfMention[mentionLoc])
								{//use endOfHashtag
									hashtag = true;
								}
								else
								{//use endOfMention
									mention = true;
								}
							}
							else
							{//use endOfHashtag
								hashtag = true;
							}
						}
						else
						{
							if(mentionLoc >= 0)
							{
								if(endOfUrl[urlLoc] > endOfMention[mentionLoc])
								{//use endOfUrl
									url = true;
								}
								else
								{//use endOfMention
									mention = true;
								}
							}
							else
							{//use endOfHashtag
								hashtag = true;
							}
						}
					}
					else
					{
						if(mentionLoc >= 0)
						{
							if(endOfHashtag[hashtagLoc] > endOfMention[mentionLoc])
							{//use endOfHashtag
								hashtag = true;
							}
							else
							{//use endOfMention
								mention = true;
							}
						}
						else
						{//use endOfHashtag
							hashtag = true;
						}
					}
				}
				else
				{
					if(urlLoc >= 0)
					{
						if(mentionLoc >= 0)
						{
							if(endOfUrl[urlLoc] > endOfMention[mentionLoc])
							{//use endOfUrl
								url = true;
							}
							else
							{//use endOfMention
								mention = true;
							}
						}
						else
						{//use endOfUrl
							url = true;
						}
					}
					else
					{
						if(mentionLoc >= 0)
						{//use endOfMention
							mention = true;
						}
						else
						{//can't use any of them, it should never hit this case so break loop.
							break;
						}
					}
				}
				if(mention)
				{//covers start+1 through end-1 inclusively
/*					parsingChars = text.toCharArray();
					tempStr = "";
					for(int j = endOfMention[mentionLoc]; j < parsingChars.length; j++)
					{
						tempStr = tempStr + parsingChars[j];
					}
					text = "";
					for(int j = 0; j < endOfMention[mentionLoc]; j++)
					{
						text = text + parsingChars[j];
					}
					text = text + "</a>" + tempStr;
					parsingChars = text.toCharArray();
					tempStr = "";
					for(int j = startOfMention[mentionLoc] + 1; j < parsingChars.length; j++)
					{
						tempStr = tempStr + parsingChars[j];
					}
					text = "";
					for(int j = 0; j < startOfMention[mentionLoc] + 1; j++)
					{
						text = text + parsingChars[j];
					}
					text = text + "<a href=\"http://twitter.com/" + user_mentions[mentionLoc] + "\">" + tempStr;*/
					mentionLoc--;
					mention = false;
				}
				if(url)
				{//covers start+1 through end-1 inclusively
					parsingChars = text.toCharArray();
					tempStr = "";
					for(int j = endOfUrl[urlLoc]; j < parsingChars.length; j++)
					{
						tempStr = tempStr + parsingChars[j];
					}
					tempStr = "</a>" + tempStr;
					text = "";
					for(int j = 0; j < startOfUrl[urlLoc]; j++)
					{
						text = text + parsingChars[j];
					}
					text = text + "<a href=\"";
					if(expandedUrl[urlLoc].equals(""))
					{
						 text = text + urls[urlLoc];
					}
					else
					{
						text = text + expandedUrl[urlLoc];
					}
					text = text + "\">";
					if(displayUrls[urlLoc].equals(""))
					{
						text = text + urls[urlLoc];
					}
					else
					{
						text = text + displayUrls[urlLoc];
					}
					text = text + tempStr;
					urlLoc--;
					url = false;
				}
				if(hashtag)
				{//covers start+2 through end-1 inclusively
/*					parsingChars = text.toCharArray();
					tempStr = "";
					for(int j = endOfHashtag[hashtagLoc]; j < parsingChars.length; j++)
					{
						tempStr = tempStr + parsingChars[j];
					}
					tempStr = "</a>" + tempStr;
					text = "";
					for(int j = 0; j < startOfHashtag[hashtagLoc] + 1; j++)
					{
						text = text + parsingChars[j];
					}
					text = text + "<a href=\"http://twitter.com/search?q=" + hashtags[hashtagLoc] + "\">" + hashtags[hashtagLoc] + tempStr;*/
					hashtagLoc--;
					hashtag = false;
				}
			}
			messages[i] = from + "<br />" + to + "<br />" + text + "<br /><img width=\"16\" height=\"16\" src=\"https://si0.twimg.com/images/dev/cms/intents/bird/bird_blue/bird_16_blue.png\"></img>" + date;

			display[i] = new JEditorPane("text/html", messages[i]);
			display[i].setEditable(false);
			display[i].setOpaque(false);
			display[i].addHyperlinkListener(this);
			Dimension size = display[i].getPreferredSize();
			display[i].setPreferredSize(new Dimension(scrollingTextArea.getWidth() - 20, 150));
			desktop.add(display[i]);
		}
		remove(scrollingTextArea);
		scrollingTextArea = new JScrollPane(desktop);
		scrollingTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollingTextArea.setBounds(insets.left + 100, insets.top + 50, 3*(getWidth()-200)/4, getHeight() - 100);
		scrollingTextArea.setWheelScrollingEnabled(true);
		JScrollBar vBar = scrollingTextArea.getVerticalScrollBar();
		vBar.setMinimum(0);
		vBar.setMaximum(100);
		vBar.setValue(0);
		vBar.setUnitIncrement(10);
		scrollingTextArea.setVerticalScrollBar(vBar);

		add(scrollingTextArea);
	}

	public void hyperlinkUpdate(HyperlinkEvent hle)
	{
		if(HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType()))
		{
			logs.updateLogs(hle.getURL().toString());
			try
			{
				java.awt.Desktop.getDesktop().browse(java.net.URI.create(hle.getURL().toString()));
			}
			catch(IOException e)
			{
				logs.updateLogs("IOError: " + e.getMessage());
			}
		}
	}

	/**
	*	Overload of the componentHidden function.  This is required to have the resize listener.
	**/
	public void componentHidden(ComponentEvent e)
	{
	}

	/**
	*	Overload of the componentShown function.  This is required to have the resize listener.
	**/
	public void componentShown(ComponentEvent e)
	{
	}

	/**
	*	Overload of the componentMoved function.  This is required to have the resize listener.
	**/
	public void componentMoved(ComponentEvent e)
	{
	}

	/**
	*	Overload of the componentResize function.  Overloaded to allow for resizeing the application and displaying the frames correctly.
	**/
	public void componentResized(ComponentEvent e)
	{
		Dimension size = accountLabel.getPreferredSize();
		accountLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + 25, size.width, size.height);

		closeButton.setBounds(insets.left + getWidth()/2 - 100, insets.top + getHeight() - 50, 200, 25);

		scrollingTextArea.setBounds(insets.left + 100, insets.top + 50, 3*(getWidth()-200)/4, getHeight() - 100);

		yearScrollingList.setBounds(insets.left + 100 + 3*(getWidth()-200)/4, insets.top + 50, (getWidth()-200)/4, (getHeight() - 100)/3);

		monthScrollingList.setBounds(insets.left + 100 + 3*(getWidth()-200)/4, insets.top + 50 + ((getHeight() - 100)/3), (getWidth()-200)/4, (getHeight() - 100)/3);

		fileScrollingList.setBounds(insets.left + 100 + 3*(getWidth()-200)/4, insets.top + 50 + 2*((getHeight() - 100)/3), (getWidth()-200)/4, (getHeight() - 100)/3);

		validate();
	}
}
