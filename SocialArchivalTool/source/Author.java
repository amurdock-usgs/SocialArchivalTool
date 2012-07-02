package code;

/**	@author amurdock	**/

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

/**	The Author internal frame class.  This class displays information about the author of this program and is executed from the menu in the driver class.	**/

public class Author extends JPanel implements ComponentListener
{
	JLabel versionLabel, authorLabel, agencyLabel, ownedLabel, emailLabel;
	Insets insets;
	Logs logs = new Logs();

	/**	Class constructor.	**/
	public Author(int width, int height)
	{
		//Panel setup
		setOpaque(true);
		setSize(width, height);
		setVisible(true);
		Dimension size;
		insets = getInsets();
		setLayout(null);
		addComponentListener(this);
		//End Panel setup

		//Component setup
		versionLabel = new JLabel("Version: " + GetVersion());
		add(versionLabel);
		size = versionLabel.getPreferredSize();
		size.width = 220;
		size.height = 25;
		versionLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		authorLabel = new JLabel("Author: Austin Murdock");
		add(authorLabel);
		authorLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - size.height/2 - 25, size.width, size.height);

		ownedLabel = new JLabel("Maintained by: NGTOC Web Team");
		add(ownedLabel);
                ownedLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - size.height/2, size.width, size.height);

		emailLabel = new JLabel("Email: ngtocweb@usgs.gov");
		add(emailLabel);
                emailLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 + 25 - size.height/2, size.width, size.height);
		//End component setup
	}

	/**
	*	This function just reads the version number out of the jar file name.
	*	@return Returns the version number in the string format or an error message.
	**/
	public String GetVersion()
	{
		//Get the current directory
		File currDir = new File(System.getProperty("user.dir"));
		String [] chld = currDir.list();
		if(chld != null)
		{
			for(int i = 0; i < chld.length; i++)
			{
				String [] temp = chld[i].split("-");
				if(temp.length > 1)
				{
					if(temp[0].equals("SocialArchivalTool"))
					{
						temp = temp[1].split(".jar");
						return temp[0];
					}
				}
			}
		}
		logs.updateLogs("Unable to verify current version.");
		return "Unable to verify current version.";
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
		Dimension size = versionLabel.getPreferredSize();
		size.width = 220;
		size.height = 25;
		authorLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - size.height/2 - 25, size.width, size.height);

		ownedLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 + 25 - size.height/2, size.width, size.height);

		agencyLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - size.height/2, size.width, size.height);

		versionLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		emailLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 + 50 - size.height/2, size.width, size.height);

		validate();
	}
}
