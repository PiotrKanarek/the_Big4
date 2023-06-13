package pl.edu.wit;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pl.edu.wit.file.FilesGUI;

public class MainVBK {
	
	private static final Logger log = LogManager.getLogger(MainVBK.class.getName());

	public static void main(String[] args){
	
		log.info("Application started");
		try {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			log.error(e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
		} catch (InstantiationException e) {
			log.error(e.getMessage());
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
		}
		//GUI object. It's constructor launches main frame creation and executes functionalities
		// according to event driven logic.
		FilesGUI gui = new FilesGUI();
		gui.getFrame().setVisible(true);
		gui.getFrame().setLocationRelativeTo(null);
	}
}
