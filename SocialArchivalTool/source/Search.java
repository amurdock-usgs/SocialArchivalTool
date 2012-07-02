package code;

/**	@author amurdock	**/

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**	Search class.  This class is designed to allow the user to search the archives for messages that fit the parameters.	**/

public class Search extends JPanel implements ActionListener, ComponentListener
{
	JLabel searchLabel, savedSearchLabel, dateLabel, topicLabel, toLabel, fromLabel, monthYearLabel;
	JTextField topicText, toText, fromText;
	JComboBox savedSearchBox, yearBox, monthBox, dayBox, yearBox2, monthBox2, dayBox2;
	String[] savedSearchList = {""}, yearList = {""}, monthList = {""}, dayList = {""};
	JButton saveSearchButton, deleteSearchButton, searchButton;

	/**
	*	Class constructor.
	*	@param width	The width of the new frame.
	*	@param height	The height of the new frame.
	**/
	public Search(int width, int height)
	{
		//Panel setup
		setOpaque(true);
		setSize(width, height);
		setVisible(true);
		Dimension size;
		Insets insets = getInsets();
		setLayout(null);
		addComponentListener(this);
		//End panel setup

		searchLabel = new JLabel("Search Archives");
		add(searchLabel);
		size = searchLabel.getPreferredSize();
		searchLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - size.height/2 - 150, size.width, size.height);

		savedSearchLabel = new JLabel("Saved Searches:");
		add(savedSearchLabel);
		size = savedSearchLabel.getPreferredSize();
		savedSearchLabel.setBounds(insets.left + getWidth()/2 - size.width - 5, insets.top + getHeight()/2 - size.height/2 - 100, size.width, size.height);

		getSavedSearchList();
		savedSearchBox = new JComboBox(savedSearchList);
		add(savedSearchBox);
		size = savedSearchBox.getPreferredSize();
		savedSearchBox.setBounds(insets.left + getWidth()/2 + 5, insets.top + getHeight()/2 - size.height/2 - 100, size.width, size.height);

		dateLabel = new JLabel(" to ");
		add(dateLabel);
		size = dateLabel.getPreferredSize();
		dateLabel.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		getYearList();
		yearBox = new JComboBox(yearList);
		add(yearBox);
		size = yearBox.getPreferredSize();
		yearBox.setBounds(insets.left + getWidth()/2 - size.width - dateLabel.getPreferredSize().width/2, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		monthYearLabel = new JLabel(" / ");
		add(monthYearLabel);
		size = monthYearLabel.getPreferredSize();
		monthYearLabel.setBounds(insets.left + getWidth()/2 - yearBox.getPreferredSize().width - dateLabel.getPreferredSize().width/2 - size.width, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		getDayList();
		dayBox = new JComboBox(dayList);
		add(dayBox);
		size = dayBox.getPreferredSize();
		dayBox.setBounds(insets.left + getWidth()/2 - dateLabel.getPreferredSize().width/2 - yearBox.getPreferredSize().width - monthYearLabel.getPreferredSize().width - size.width, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		monthYearLabel = new JLabel(" / ");
		add(monthYearLabel);
		size = monthYearLabel.getPreferredSize();
		monthYearLabel.setBounds(insets.left + getWidth()/2 - yearBox.getPreferredSize().width - dateLabel.getPreferredSize().width/2 - size.width - size.width - dayBox.getPreferredSize().width, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		getMonthList();
		monthBox = new JComboBox(monthList);
		add(monthBox);
		size = monthBox.getPreferredSize();
		monthBox.setBounds(insets.left + getWidth()/2 - dateLabel.getPreferredSize().width/2  - size.width - monthYearLabel.getPreferredSize().width * 2 - yearBox.getPreferredSize().width - dayBox.getPreferredSize().width, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		monthBox2 = new JComboBox(monthList);
		add(monthBox2);
		size = monthBox2.getPreferredSize();
		monthBox2.setBounds(insets.left + getWidth()/2 + dateLabel.getPreferredSize().width/2, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		monthYearLabel = new JLabel(" / ");
		add(monthYearLabel);
		size = monthYearLabel.getPreferredSize();
		monthYearLabel.setBounds(insets.left + getWidth()/2 + dateLabel.getPreferredSize().width/2 + monthBox2.getPreferredSize().width, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		dayBox2 = new JComboBox(dayList);
		add(dayBox2);
		size = dayBox2.getPreferredSize();
		dayBox2.setBounds(insets.left + getWidth()/2 + dateLabel.getPreferredSize().width/2 + monthBox2.getPreferredSize().width + monthYearLabel.getPreferredSize().width, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		monthYearLabel = new JLabel(" / ");
		add(monthYearLabel);
		size = monthYearLabel.getPreferredSize();
		monthYearLabel.setBounds(insets.left + getWidth()/2 + dateLabel.getPreferredSize().width/2 + monthBox2.getPreferredSize().width + size.width + dayBox2.getPreferredSize().width, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		yearBox2 = new JComboBox(yearList);
		add(yearBox2);
		size = yearBox2.getPreferredSize();
		yearBox2.setBounds(insets.left + getWidth()/2 + dateLabel.getPreferredSize().width/2 + monthBox2.getPreferredSize().width + monthYearLabel.getPreferredSize().width * 2 + dayBox2.getPreferredSize().width, insets.top + getHeight()/2 - size.height/2 - 50, size.width, size.height);

		topicLabel = new JLabel("Topic:");
		add(topicLabel);
		size = topicLabel.getPreferredSize();
		topicLabel.setBounds(insets.left + getWidth()/2 - size.width - 5, insets.top + getHeight()/2 - size.height/2 + 100, size.width, size.height);

		topicText = new JTextField("");
		add(topicText);
		topicText.setBounds(insets.left + getWidth()/2 + 5, insets.top + getHeight()/2 - (size.height + 10)/2 + 100, 150, size.height + 10);

		toLabel = new JLabel("To:");
		add(toLabel);
		size = toLabel.getPreferredSize();
		toLabel.setBounds(insets.left + getWidth()/2 - size.width - 5, insets.top + getHeight()/2 - size.height/2, size.width, size.height);

		toText = new JTextField("");
		add(toText);
		toText.setBounds(insets.left + getWidth()/2 + 5, insets.top + getHeight()/2 - (size.height + 10)/2, 150, size.height + 10);

		fromLabel = new JLabel("From:");
		add(fromLabel);
		size = fromLabel.getPreferredSize();
		fromLabel.setBounds(insets.left + getWidth()/2 - size.width - 5, insets.top + getHeight()/2 - size.height/2 + 50, size.width, size.height);

		fromText = new JTextField("");
		add(fromText);
		fromText.setBounds(insets.left + getWidth()/2 + 5, insets.top + getHeight()/2 - (size.height + 10)/2 + 50, 150, size.height + 10);

		searchButton = new JButton("Search");
		add(searchButton);
		searchButton.setBounds(insets.left + getWidth()/2 - 215, insets.top + getHeight()/2 + 130, 140, 30);

		saveSearchButton = new JButton("Save Search");
		add(saveSearchButton);
		saveSearchButton.setBounds(insets.left + getWidth()/2 - 70, insets.top + getHeight()/2 + 130, 140, 30);

		deleteSearchButton = new JButton("Delete Search");
		add(deleteSearchButton);
		deleteSearchButton.setBounds(insets.left + getWidth()/2 + 75, insets.top + getHeight()/2 + 130, 140, 30);
	}

	private void getSavedSearchList()
	{
		savedSearchList = new String[1];
		savedSearchList[0] = "New Search";
		return;
	}

	private void getDayList()
	{
		dayList = new String[31];
		for(int i = 0; i < dayList.length; i++)
		{
			dayList[i] = String.valueOf(i + 1);
		}
	}

	private void getMonthList()
	{
		monthList = new String[12];
		monthList[0] = "Jan";
		monthList[1] = "Feb";
		monthList[2] = "Mar";
		monthList[3] = "Apr";
		monthList[4] = "May";
		monthList[5] = "Jun";
		monthList[6] = "Jul";
		monthList[7] = "Aug";
		monthList[8] = "Sep";
		monthList[9] = "Oct";
		monthList[10] = "Nov";
		monthList[11] = "Dec";
		return;
	}

	private void getYearList()
	{
		yearList = new String[1];
		yearList[0] = "2000";
		return;
	}

	/**
	*	Overload of actionPerformed function.
	**/
	public void actionPerformed(ActionEvent event)
	{
		/*if(e.getSource() == saveSearchButton)
		{}
		else if(e.getSource() == deleteSearchButton)
		{}
		else if(e.getSource() == searchButton)
		{}*/
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
	}
}
