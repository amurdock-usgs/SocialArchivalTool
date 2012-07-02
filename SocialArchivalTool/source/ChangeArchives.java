package code;

/**	@author amurdock	**/

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**	The ChangeArchives internal frame class.  This class will display a GUI for the user to change the location that the archives are saved as well as to move all the current archives to the new location.	**/

public class ChangeArchives extends JPanel implements ActionListener, ComponentListener
{
	JLabel title, currentLocation, warningLabel;
	JTextField newLocation;
	JButton confirm;
	FeedsLocation location = new FeedsLocation();
	String feedLocation = "";
	Insets insets;
	Logs logs = new Logs();

	/**
	*	Class constructor.
	*	@param width	the width of the new frame
	*	@param height	the height of the new frame
	**/
	public ChangeArchives(int width, int height)
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
		
		//Initialize feedsLocation
		location.getFeedsLocation();
		feedLocation = location.feedLocation;
		//End initialization of feedsLocation

		//Component setup
		warningLabel = new JLabel("<HTML><center><font color=\"red\">WARNING:</font><br />This will copy all files in the current save location into the new location then delete ALL the files in the old save location.</center></HTML>");
		add(warningLabel);
		size = warningLabel.getPreferredSize();
		warningLabel.setBounds(insets.left + getWidth()/2 - 250, insets.top + getHeight()/2 - 90, 500, 50);

		title = new JLabel("Current save location:");
		add(title);
		size = title.getPreferredSize();
		title.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - 30, size.width, 25);

		currentLocation = new JLabel(feedLocation);
		add(currentLocation);
		size = currentLocation.getPreferredSize();
		currentLocation.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - 10, size.width, 25);

		newLocation = new JTextField("");
		add(newLocation);
		size = newLocation.getPreferredSize();
		newLocation.setBounds(insets.left + getWidth()/2 - 380/2, insets.top + getHeight()/2 + 20, 275, 25);

		confirm = new JButton("Change");
		add(confirm);
		size = confirm.getPreferredSize();
		confirm.setBounds(insets.left + getWidth()/2 + 380/2 - 105, insets.top + getHeight()/2 + 20, 105, 24);
		confirm.addActionListener(this);
		//End component setup
	}

	/**
	*	Overload of actionPerformed.
	**/
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == confirm)
		{
			setNewLocation(newLocation.getText());
		}
	}

	/**
	*	Function to change the location of the arhives.
	*	@param text	the new arhive location
	**/
	public void setNewLocation(String text)
	{
		String oldLocation = location.feedLocation;
		location.feedLocation = text;
		if(location.setFeedsLocation())
		{
			if(oldLocation.equals(location.feedLocation))
			{
				logs.updateLogs("No change required on updating feed location.");
			}
			else
			{
				moveToNewLocation(oldLocation);
			}
		}
		else
		{
			location.feedLocation = oldLocation;
			logs.updateLogs("Failed to update feed location.");
		}
		feedLocation=location.feedLocation;
		return;
	}

	/**
	*	Function used to move the archives to the new location.
	*	@param oldLocation	The location where the archives currently are, prior to being moved.
	**/
	public void moveToNewLocation(String oldLocation)
	{
		if(location.moveToLocation(oldLocation, location.feedLocation))
		{
			JOptionPane.showMessageDialog(this, "Feeds were relocated to " + location.feedLocation + ".");
			logs.updateLogs("Feeds were relocated to " + location.feedLocation + ".");
		}
		else
		{
			logs.updateLogs("Failed to relocate feeds to new feeds location.");
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
		Dimension size;

		size = warningLabel.getPreferredSize();
		warningLabel.setBounds(insets.left + getWidth()/2 - 250, insets.top + getHeight()/2 - 90, 500, 50);

		size = title.getPreferredSize();
		title.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - 30, size.width, 25);

		size = currentLocation.getPreferredSize();
		currentLocation.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - 10, size.width, 25);

		size = newLocation.getPreferredSize();
		newLocation.setBounds(insets.left + getWidth()/2 - 380/2, insets.top + getHeight()/2 + 20, 275, 25);

		size = confirm.getPreferredSize();
		confirm.setBounds(insets.left + getWidth()/2 + 380/2 - 105, insets.top + getHeight()/2 + 20, 105, 24);

		validate();
	}
}
