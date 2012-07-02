package code;

/**	@author amurdock	**/

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.event.*;
import java.io.*;

/**	SocialArchivalTool driver class.  Driver for the GUI built into the program, allowing for entry of new Twitter accounts to follow as well as offers help options.  On program start the default window to load is the UpdateArchives internal frame.  The reason for this is that the UpdateArchives frame will, well do as its name says.  You can read more on each of the internal frames and what they do in their respective comments.	**/

public class SocialArchivalTool extends JFrame implements ActionListener, ComponentListener
{
	JMenuItem authorItem, updateArchivesItem, newAccountItem, viewAccountsItem, changeArchivesItem, instructionsItem, changelogItem, searchItem;

	JPanel iFrame;

	JDesktopPane desktop;

	Options options = new Options();

	int xOff = 0, yOff = 0;

	int W = 610;
	int H = 650;
	FeedsLocation location = new FeedsLocation();

	/**
	*	Class constructor.
	**/
	public SocialArchivalTool()
	{
		//Frame setup
		super("SocialArchivalTool");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(screenSize.width/2 - W/2, screenSize.height/2 - H/2, W, H);
		desktop = new JDesktopPane();
		setContentPane(desktop);
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		setResizable(true);
		//End frame setup

		desktop.addComponentListener(this);

		//Menu setup
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);

		JMenu accountsMenu = new JMenu("Accounts");
		menuBar.add(accountsMenu);

		JMenu archiveMenu = new JMenu("Archives");
		menuBar.add(archiveMenu);

		authorItem = new JMenuItem("Author");
		authorItem.addActionListener(this);
		helpMenu.add(authorItem);

		instructionsItem = new JMenuItem("Instructions");
		instructionsItem.addActionListener(this);
		helpMenu.add(instructionsItem);

		changelogItem = new JMenuItem("Changelog");
		changelogItem.addActionListener(this);
		helpMenu.add(changelogItem);

		newAccountItem = new JMenuItem("Add New Account");
		newAccountItem.addActionListener(this);
		accountsMenu.add(newAccountItem);

		viewAccountsItem = new JMenuItem("View Accounts");
		viewAccountsItem.addActionListener(this);
		accountsMenu.add(viewAccountsItem);

		updateArchivesItem = new JMenuItem("Update Archives");
		updateArchivesItem.addActionListener(this);
		archiveMenu.add(updateArchivesItem);

		changeArchivesItem = new JMenuItem("Change Archives");
		changeArchivesItem.addActionListener(this);
		archiveMenu.add(changeArchivesItem);

		searchItem = new JMenuItem("Search");
		searchItem.addActionListener(this);
		archiveMenu.add(searchItem);
		//End menu setup

		//Start the timedUpdate thread if needed.
		if(options.GetTimedUpdate())
		{
			TimedUpdate timedUpdate = new TimedUpdate(options);
		}
		//End

		//Initial Panel to display
		if(options.GetDisplayInstructions())
			iFrame = new Instructions(desktop.getWidth(), desktop.getHeight());
		else
			iFrame = new UpdateArchives(desktop.getWidth(), desktop.getHeight());
		desktop.add(iFrame);
		//End

		//Setup feedsLocation
		try
		{
			location.setupFeedsLocation();
		}
		catch(Exception e)
		{
		}
		//End feedsLocation setup
	}

	/**
	*	Overload of actionPerformed function.
	**/
	public void actionPerformed(ActionEvent event)
	{
		Component [] components = desktop.getComponents();
		for(int i = 0; i < components.length; i++)
		{
			desktop.remove(components[i]);
		}
		if(event.getSource() == authorItem)
			iFrame = new Author(desktop.getWidth(), desktop.getHeight());
		else if(event.getSource() == newAccountItem)
		{
			NewAccount acc = new NewAccount();
			acc.AddUserAccount();
		}
		else if(event.getSource() == viewAccountsItem)
			iFrame = new ViewAccounts(desktop.getWidth(), desktop.getHeight());
		else if(event.getSource() == updateArchivesItem)
			iFrame = new UpdateArchives(desktop.getWidth(), desktop.getHeight());
		else if(event.getSource() == changeArchivesItem)
			iFrame = new ChangeArchives(desktop.getWidth(), desktop.getHeight());
		else if(event.getSource() == instructionsItem)
			iFrame = new Instructions(desktop.getWidth(), desktop.getHeight());
		else if(event.getSource() == changelogItem)
			iFrame = new Changelog(desktop.getWidth(), desktop.getHeight());
		else if(event.getSource() == searchItem)
			iFrame = new Search(desktop.getWidth(), desktop.getHeight());
		iFrame.setLocation(xOff, yOff);
		desktop.add(iFrame);
		
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
		boolean resize = false;
		int width = getWidth(), height = getHeight();
		if(width < 610)
		{
			width = 610;
			resize = true;
		}
		if(height < 650)
		{
			height = 650;
			resize = true;
		}
		if(resize)
			setSize(width, height);
		Component c = (Component)e.getSource();
		width = c.getWidth();
		height = c.getHeight();
		if(width < 600)
		{
			width = 600;
		}
		if(height < 600)
		{
			height = 600;
		}

		Component [] components = desktop.getComponents();
		for(int i = 0; i < components.length; i++)
		{
			components[i].setSize(width, height);
			components[i].validate();
		}

	}

	/**
	*	Overload of the quit function.
	**/
	protected void quit()
	{
		System.exit(0);
	}

	/**
	*	Runs the program in updater mode, updates the feeds then closes the program.
	**/
	private static void updateAndClose()
	{
		JTextArea ta = new JTextArea("");
		Updater updater = new Updater(ta, -1);
		updater.start();
		System.exit(0);
	}

	/**
	*	Creates and displays the GUI on startup.
	**/
	private static void createAndShowGUI()
	{
		options.ReadOptions();
		if(options.GetUpdateMode() == true)
		{
			updateAndClose();
		}
		JFrame.setDefaultLookAndFeelDecorated(true);
		SocialArchivalTool frame = new SocialArchivalTool();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	*	Overload of the main class.
	**/
	public static void main(String [] args)
        {
		if(args.length > 0)
		{
			if(args[0].equals("-1"))
			{
				updateAndClose();
			}
		}
		else
		{
			javax.swing.SwingUtilities.invokeLater(new Runnable()
                	{
                		public void run()
                	        {
                	        	createAndShowGUI();
                	        }
			});
		}
	}
}
