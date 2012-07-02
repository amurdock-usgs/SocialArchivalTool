package code;

/**	@author amurdock	**/

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

/**	The UpdateAccounts internal frame class.  This class will display a label followed by a uneditable text field and a button.  The button will link to the ViewAccounts internal frame.  On start the frame will run code to pull twitter feeds for each account saved through the NewAccount internal frame then output the name of the account into the text field followed by parsing the input before saving it to a text file.	**/

public class UpdateArchives extends JPanel implements ActionListener, ComponentListener
{
	JLabel label;
	JTextArea textArea;
	JButton button;
	JScrollPane scrollingPane;
	Insets insets;

	/**
	*	Class constructor.
	*	@param width	The width of the new frame.
	*	@param height	The height of the new frame.
	**/
	public UpdateArchives(int width, int height)
	{
		//Panel setup
		setOpaque(true);
		setSize(width, height);
		setVisible(true);
		addComponentListener(this);

		Dimension size;
		insets = getInsets();
		setLayout(null);
		//End Panel setup

		//Component setup
		label = new JLabel("Archives updated for:");
		add(label);
		size = label.getPreferredSize();
		label.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - 175 - size.height/2, size.width, size.height);

		textArea = new JTextArea("");
		textArea.setLineWrap(true);
		scrollingPane = new JScrollPane(textArea);
		scrollingPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollingPane);
		textArea.setEditable(false);
		scrollingPane.setBounds(insets.left + getWidth()/2 - 200, insets.top + getHeight()/2 - 150, 400, 300);

		button = new JButton("View Archives");
		add(button);
		button.addActionListener(this);
		size = button.getPreferredSize();
		button.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 + 175 - size.height/2, size.width, size.height);
		//End component setup

		Updater updater = new Updater(textArea, 0);
		updater.start();
	}

	/**
	*	Overload of the actionPerformed function.
	**/
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == button)
		{
			Container parent = getParent();
			parent.add(new ViewAccounts(getWidth(), getHeight()));
			parent.remove(this);
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
		Dimension size = label.getPreferredSize();
		label.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 - 175 - size.height/2, size.width, size.height);

		size = scrollingPane.getPreferredSize();
		scrollingPane.setBounds(insets.left + getWidth()/2 - 200, insets.top + getHeight()/2 - 150, 400, 300);

		size = button.getPreferredSize();
		button.setBounds(insets.left + getWidth()/2 - size.width/2, insets.top + getHeight()/2 + 175 - size.height/2, size.width, size.height);

		validate();
	}
}
