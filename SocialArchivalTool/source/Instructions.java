package code;

/**	@author amurdock	**/

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

/**	The instruction frame class.  This class is used to display an instruction set to help the user, it can be accessed by the menu or by turning on the associated variable in the options.txt file and it will run on startup.	**/

public class Instructions extends JPanel implements ComponentListener
{
	JTextArea textArea;
	JLabel label;
	JScrollPane scrollPane;
	Insets insets;

	/**	Class Constructor	**/
	public Instructions(int width, int height)
	{
		//Panel setup
		setOpaque(true);
		setSize(width, height);
		setVisible(true);
		insets = getInsets();
		setLayout(null);
		addComponentListener(this);
		//End Panel setup

		//Component setup
		label = new JLabel("<HTML><center><strong>Instructions:</strong><br />This is a short how-to on using the SocialArchivalTool<br />The SocialArchivalTool is a program designed to be run by a scheduler on a regular basis to gather Tweets for the registered accounts and save them locally to meet the NARA requirements.<br />It works by authorizing (adding) an account then when the update is run queries Twitter using the Scribe library to gather all direct messages sent, direct messages received and Tweets posted by the account.<br /><br /><strong>Adding an Account:</strong><br /><br /><p>To add an account choose \"Add New Account\" from the \"Accounts\" drop down menu<br />Next enter the account name as it is displayed through Twitter and press \"Confirm\"<br />A dialog will popup with a URL, copy and paste the URL into a browser of your choice then enter your account information and paste the pin Twitter supplies into the text box on the popup and confirm.</p><br /></center><br /></HTML>");
		label.setBounds(insets.left + 50, insets.top + 50, getWidth() - 100, getHeight() - 100);
		add(label);
		/*textArea.setLineWrap(true);
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane);
		scrollPane.setBounds(insets.left + W/2 - 250, insets.top + H/2 - 200, 500, 400);*/
		
		//End Component setup
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
		label.setBounds(insets.left + 50, insets.top + 50, getWidth() - 100, getHeight() - 100);
		validate();
	}
}
