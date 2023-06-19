package pl.edu.wit.presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

/**
 * Class defines window visible to user during and after files copying process. Provides feedback
 * with number of copied files.
 * @author Bartłomiej Kilim
 *
 */
public class WaitWindow {

	/**
	 *  Displays feedback info
	 */
	private JLabel label = null;
	
	/**
	 *  Allows wait windows close when user decides to do so
	 */
	private JButton okButton = null;
	
	/**
	 *  Container for label and button
	 */
	private JWindow message = null;
	
	/**
	 *  Font used to customize message display for user
	 */
	Font font2 = new Font(Font.DIALOG, Font.PLAIN, 20);

	public WaitWindow(FilesGUI gui) {

		message = new JWindow(gui.getFrame());
		message.setSize(400, 150);
		
		/**
		 * Provides layout and border for window
		 */
		JPanel panel = new JPanel(new GridLayout(2, 1));
		
		/**
		 * Container for proper button placement
		 */
		JPanel panelB = new JPanel();
		panel.setBorder(new LineBorder(Color.GRAY, 2));
		message.getContentPane().add(panel, "Center");
		message.setLocationRelativeTo(null);
		label = new JLabel("Copying files ...", SwingConstants.CENTER);
		label.setFont(font2);
		panel.add(label);
		okButton = new JButton("OK");
		okButton.setEnabled(false);
		okButton.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
		okButton.setPreferredSize(new Dimension(150, 40));

		panelB.add(okButton);
		panel.add(panelB);
		
		/**
		 *  Pushing okButton closes the wait window and activates GUI main frame with all of it's components, allowing further use of application.
		 */
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				message.dispose();
				gui.getFrame().setEnabled(true);
				gui.getFieldTo().setEditable(true);
				gui.getFieldFrom().setEditable(true);
				gui.getStartButton().setEnabled(true);
			}
		});
		message.setVisible(true);
	}

	public JLabel getLabel() {
		return label;
	}

	public JButton getOkButton() {
		return okButton;
	}

	public JWindow getMessage() {
		return message;
	}		
}
