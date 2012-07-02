package code;

/**	@author amurdock	**/

//import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.event.*;

/**	The ViewAccounts internal frame class.  This class will display all of the accounts that this program follows as well as have buttons at the bottom to link to the AddAccounts and RemoveAccounts pages.	**/

public class ViewAccounts extends JPanel implements ActionListener, ComponentListener
{
	JLabel accountsLabel, archivesLabel;
	JScrollPane accountsPane, archivesPane;
	JButton viewAccountButton, deleteAccountButton, viewArchivesButton, deleteArchivesButton;
	JList accountsList, archivesList;
 	JPanel frame;
	String feedLocation = "";
	FeedsLocation location = new FeedsLocation();
	Insets insets;
	Logs logs = new Logs();

	/**
	*	Class constructor.
	*	@param width	The width of the new frame.
	*	@param height	The height of the new frame.
	**/
	public ViewAccounts(int width, int height)
	{
		//Panel setup
		setOpaque(true);
		setSize(width, height);
		setVisible(true);
		Dimension size;
		insets = getInsets();
		setLayout(null);
		addComponentListener(this);
		//End panel setup

		//FeedsLocation setup
		location.getFeedsLocation();
		feedLocation = location.feedLocation;
		//End feedsLocation setup

		//Component setup
		try
		{
			accountsList = ReadAccounts();
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage());
		}

		accountsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		accountsList.setSelectedIndex(0);

		ReadArchivesList();
		archivesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		archivesList.setSelectedIndex(0);

		accountsLabel = new JLabel("Twitter Accounts");
		add(accountsLabel);
		size = accountsLabel.getPreferredSize();
		accountsLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - 280, size.width, 25);

		viewAccountButton = new JButton("View Account");
                add(viewAccountButton);
                viewAccountButton.addActionListener(this);
                viewAccountButton.setBounds(insets.left + getWidth()/2 + 25, insets.top + getHeight()/2 - 30, 150, 25);

                deleteAccountButton = new JButton("Delete Account");
                add(deleteAccountButton);
                deleteAccountButton.addActionListener(this);
                deleteAccountButton.setBounds(insets.left + getWidth()/2 - 175, insets.top + getHeight()/2 - 30, 150, 25);

		archivesLabel = new JLabel("Twitter Archives");
		add(archivesLabel);
		size = archivesLabel.getPreferredSize();
		archivesLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 + 5, size.width, 25);

		viewArchivesButton = new JButton("View Archives");
		add(viewArchivesButton);
		viewArchivesButton.addActionListener(this);
		viewArchivesButton.setBounds(insets.left + getWidth()/2 + 25, insets.top + getHeight()/2 + 255, 150, 25);

		deleteArchivesButton = new JButton("Delete Archives");
		add(deleteArchivesButton);
		deleteArchivesButton.addActionListener(this);
		deleteArchivesButton.setBounds(insets.left + getWidth()/2 - 175, insets.top + getHeight()/2 + 255, 150, 25);

		if(accountsList.getSelectedValue().equals(""))
		{
			viewAccountButton.setEnabled(false);
			deleteAccountButton.setEnabled(false);
		}
		else
		{
			accountsPane = new JScrollPane(accountsList);
			add(accountsPane);
			accountsPane.setBounds(insets.left + getWidth()/2 - 200, insets.top + getHeight()/2 - 250, 400, 220);
		}
		if(archivesList.getSelectedValue().equals(""))
		{
			viewArchivesButton.setEnabled(false);
			deleteArchivesButton.setEnabled(false);
		}
		else
		{
			archivesPane = new JScrollPane(archivesList);
			add(archivesPane);
			archivesPane.setBounds(insets.left + getWidth()/2 - 200, insets.top + getHeight()/2 + 35, 400, 220);
		}
		//End componenet setup
	}

	/**
	*	Reads in a list of all the accounts which current have archives then places it in the archivesList variable.
	**/
	public void ReadArchivesList()
	{
		String[] s = {""};
		File f = new File(feedLocation);
		if(f.exists())
		{
			s = f.list();
			if(s.length <= 0)
			{
				s = new String[1];
				s[0] = "";
			}
		}
		else
		{
			s = new String[1];
			s[0] = "";
		}
		archivesList = new JList(s);
	}

	/**
	*	Reads in the accounts.txt file into a list.
	*	@return		JList of the accounts in the accounts.txt file.
	**/
	public JList ReadAccounts() throws IOException
	{
		JList list;
		String[] s = {""};
		File f = new File("accounts.txt");
		byte b;
		if(!f.exists() || f.length() < 0)
		{
			logs.updateLogs("accounts.txt does not exist, creating now.");
			f.createNewFile();
		}
		else
		{
			FileInputStream finp = new FileInputStream(f);
			int i = 0;
			String tempStr = "";
			while((b = (byte)finp.read()) > -1)
			{
				if((char)b == '\n')
				{
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
                                	s[i] = tempStr;
                                	i++;
					tempStr = "";
                                }
				else
					tempStr = tempStr + (char)b;
			}
			if(s.length <= i)
                        {
                        	String[] temp = new String[s.length + 1];
                        	for(int j = 0; j < s.length; j++)
                               	        temp[j] = s[j];
                               	s = new String[temp.length];
                               	for(int j = 0; j < temp.length; j++)
                          		s[j] = temp[j];
                        }
                        s[i] = tempStr;
                        i++;
			tempStr = "";
			finp.close();
		}
		String [] finalStr = new String[s.length];
		int j = 0;
		for(int i = 0; i < s.length; i++)
		{
			finalStr[i] = "";
			if(!s[i].equals(""))
			{
				finalStr[j] = s[i];
				j++;
			}
		}
		if(finalStr.length <= 0)
		{
			s = new String[1];
			s[0] = "";
		}
		else
		{
			s = new String[j];
			for(int i = 0; i < j; i++)
			{
				s[i] = finalStr[i];
			}
		}
		list = new JList(s);
		return list;
	}

	/**
	*	Overload of the actionPerformed function.
	**/
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == deleteAccountButton)
		{
			int selection = JOptionPane.showInternalConfirmDialog(this, "Are you sure you want to remove this account?\n"+ accountsList.getSelectedValue(), "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			//Yes == 0, no == 1
			if(selection == 0)//Remove selected account
			{
				JOptionPane.showMessageDialog(this, accountsList.getSelectedValue() + " was deleted from the update list, the\narchives for " + accountsList.getSelectedValue() + " will no longer be updated.  The account token was also removed.");
				logs.updateLogs(accountsList.getSelectedValue() + " was deleted from the update list, the\narchives for " + accountsList.getSelectedValue() + " will no longer be updated.  The account token was also removed.");
				removeAccount(accountsList.getSelectedValue().toString());
			}
			else//Leave account (do nothing)
			{
				JOptionPane.showMessageDialog(this, accountsList.getSelectedValue() + " was not deleted.");
				logs.updateLogs(accountsList.getSelectedValue() + " was not deleted.");
			}
		}
		else if(e.getSource() == viewAccountButton)
		{//Display the archives for the selected account.'
			frame = new DisplayArchives(getWidth(), getHeight(), accountsList.getSelectedValue().toString());
			Container parent = getParent();
			parent.add(frame);
			parent.remove(this);
			
		}
		if(e.getSource() == deleteArchivesButton)
                {
                        int selection = JOptionPane.showInternalConfirmDialog(this, "Are you sure you want to remove the archives for" + archivesList.getSelectedValue() + "?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        //Yes == 0, no == 1
                        if(selection == 0)//Remove selected account
                        {
                                JOptionPane.showMessageDialog(this, archivesList.getSelectedValue() + " was deleted from the archives and\naccounts list.");
				logs.updateLogs(archivesList.getSelectedValue() + " was deleted from the archives and\naccounts list.");
				removeArchives(archivesList.getSelectedValue().toString());
                        }
                        else//Leave account (do nothing)
                        {
                                JOptionPane.showMessageDialog(this, archivesList.getSelectedValue() + " was not deleted.");
				logs.updateLogs(archivesList.getSelectedValue() + " was not deleted.");
                        }
                }
                else if(e.getSource() == viewArchivesButton)
                {//Display the archives for the selected archive.
                        frame = new DisplayArchives(getWidth(), getHeight(), archivesList.getSelectedValue().toString());
			Container parent = getParent();
			parent.add(frame);
			parent.remove(this);
                }
	}

	/**
	*	Removes the archives for the specified account.
	*	@param archiveName	The archives to remove.
	**/
	public void removeArchives(String archiveName)
	{
		File f = new File(feedLocation + "/" + archiveName);
		String[] s = {""};
		if(f.isDirectory())
		{
			s = f.list();
			for(int i = 0; i < s.length; i++)
			{
				removeArchives(archiveName + "/" + s[i]);
			}
		}
		f.delete();
		removeAccount(archiveName);
		return;
	}

	/**
	*	Removes the specified account from the accounts.txt file and deletes the associated token.
	*	@param accountName	The account to remove.
	**/
	public void removeAccount(String accountName)
	{
		File accounts_txt = new File("accounts.txt");
		JList list;
		String[] s = {""};
		byte b;

                if(!accounts_txt.exists() && accounts_txt.length() < 0)
                {
                        logs.updateLogs("accounts.txt does not exist");
                }
                else
                {
			try{
                        FileInputStream finp = new FileInputStream(accounts_txt);
                        int i = 0;
                        String tempStr = "";
                        while((b = (byte)finp.read()) > -1)
                        {
                                if((char)b == '\n')
                                {
					if(tempStr.equals(accountName))
					{
					}
					else
					{
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
                                        	s[i] = tempStr;
                                        	i++;
					}
                                        tempStr = "";
                                }
                                else
                                        tempStr = tempStr + (char)b;
                        }
                        finp.close();

			try{
			FileOutputStream fop = new FileOutputStream(accounts_txt);
			String str = "";
			for(int j = 0; j < s.length; j++)
			{
				str = str + s[j] + "\n";
			}
			fop.write(str.getBytes());
			fop.flush();
			fop.close();
			}
			catch(Exception e)
			{
			}
			//Remove the token file for the account
			File tokenFile = new File("tokens/" + accountName + ".usgs");
			if(tokenFile.exists())
			{
				tokenFile.delete();
			}
			}
			catch(Exception exception)
			{
			}
			try
			{
				FileInputStream finp = new FileInputStream(accounts_txt);
				String tempStr = "";
				while((b = (byte)finp.read()) > -1)
				{
					if((char)b == '\n')
					{
					}
					else
					{
						tempStr = tempStr + (char)b;
					}
				}
				if(tempStr.equals(""))
				{
					accounts_txt.delete();
				}
			}
			catch(Exception e)
			{
			}
                }
                return;
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
		Dimension size = accountsLabel.getPreferredSize();
		accountsLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - 280, size.width, 25);

		accountsPane.setBounds(insets.left + getWidth()/2 - 200, insets.top + getHeight()/2 - 250, 400, 220);

                viewAccountButton.setBounds(insets.left + getWidth()/2 + 25, insets.top + getHeight()/2 - 30, 150, 25);

                deleteAccountButton.setBounds(insets.left + getWidth()/2 - 175, insets.top + getHeight()/2 - 30, 150, 25);

		size = archivesLabel.getPreferredSize();
		archivesLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 + 5, size.width, 25);

		archivesPane.setBounds(insets.left + getWidth()/2 - 200, insets.top + getHeight()/2 + 35, 400, 220);

		viewArchivesButton.setBounds(insets.left + getWidth()/2 + 25, insets.top + getHeight()/2 + 255, 150, 25);

		deleteArchivesButton.setBounds(insets.left + getWidth()/2 - 175, insets.top + getHeight()/2 + 255, 150, 25);

		validate();
	}
}
