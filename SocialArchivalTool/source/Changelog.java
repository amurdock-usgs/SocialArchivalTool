package code;

/**	@author amurdock	**/

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

/**	The Changelog class.  This class is designed as a way to display the changelog in the program for the user	**/

public class Changelog extends JPanel implements ComponentListener
{
	JTextArea changelogText;
	JScrollPane scrollPane;
	JLabel changelogLabel;
	Insets insets;
	final static String CHANGELOG_URL = "https://raw.github.com/wiki/amurdock-usgs/SocialArchivalTool/changelog.txt";
	Logs logs = new Logs();

	/**
	*	Class constructor
	*	@param width	The width of the new panel.
	*	@param height	The height of the new panel.
	**/
	public Changelog(int width, int height)
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

		//Component setup
		changelogLabel = new JLabel("Changelog:");
		size = changelogLabel.getPreferredSize();
		changelogLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/8, size.width, 20);
		add(changelogLabel);

		String str = "";
		try
		{
			URL url = new URL(CHANGELOG_URL);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			int code = urlConn.getResponseCode();
			str = urlConn.getResponseMessage();
			InputStream in = new BufferedInputStream(urlConn.getInputStream());
			Reader r = new InputStreamReader(in);
			int c;
			str = "";
			while((c = r.read()) != -1)
			{
				str = str + (char)c;
			}
		}
		catch(MalformedURLException e)
		{
			System.err.println(CHANGELOG_URL + " is not a valid url.");
			str = CHANGELOG_URL + " is not a valid url.";
		}
		catch(IOException e)
		{
			System.err.println("IOError: " + e.getMessage());
		}
		changelogText = new JTextArea(str);
		changelogText.setLineWrap(true);
		changelogText.setEditable(false);
		scrollPane = new JScrollPane(changelogText);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane);
		scrollPane.setBounds(insets.left + getWidth()/8, insets.top + getHeight()/8 + 25, 3*getWidth()/4, 3*getHeight()/4 - 25);
		//End component setup
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
		Dimension size = changelogLabel.getPreferredSize();
		changelogLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/8, size.width, 20);

		scrollPane.setBounds(insets.left + getWidth()/8, insets.top + getHeight()/8 + 25, 3*getWidth()/4, 3*getHeight()/4 - 25);

		validate();
	}
}
