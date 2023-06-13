package pl.edu.wit.file;

import javax.swing.SwingWorker;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.util.concurrent.ExecutorService;

/**
 * 	Class executes files copying process as separate thread allowing EDT to process wait window with
 *  feedback to user during files copying process.
 * @author Bartłomiej Kilim
 *
 */
public class FilesWorker extends SwingWorker<Integer, Integer> {

	// Component's text will be updated with number of copied files
	private WaitWindow wait = null;
	// FilesGUI object with paths entered by user, which are necessary for files copying process.
	private FilesGUI gui = null;
	private static final Logger log = LogManager.getLogger(FilesWorker.class.getName());
	// Number of copied files by the application. Will be used by wait window to provide final
	// feedback to user.
	int filesCopied = 0;

	// Constructor
	public FilesWorker(WaitWindow wait, FilesGUI gui) {
		this.wait = wait;
		this.gui = gui;
	}

	
	/**
	 * Executes files copying process as time consuming in additional thread.
	 */
	@Override
	protected Integer doInBackground() throws Exception {

		//Source path
		String sourceDirectory = gui.getPathFrom();
		//Destination path
        String destinationDirectory = gui.getPathTo();

        PropertySource propertySource = new PropertySource("src/main/resources/application.properties");
        ExecutorService executorService = ExecutorConfigurator.getConfiguredExecutor(propertySource);

        try (FileCopyingService copyingService = new FileCopyingService(executorService)) {
            filesCopied = copyingService.copyFiles(sourceDirectory, destinationDirectory);
        } catch (RuntimeException e) {
            log.error("Exception occurred while copying the files: " + e.getMessage());
        }

        log.info("Processing finished - copied " + filesCopied + " files");
			
		return 0;
	}

	/**
	 * Closing actions after doInBackground() function finishes. User gets number of copied files, 
	 * OK button is activated.
	 */
	@Override
	protected void done() {
		try {
			if(filesCopied == 1) {
				wait.getLabel().setText("Copied ".concat(String.valueOf(filesCopied)).concat(" file"));
			}
			else {
				wait.getLabel().setText("Copied ".concat(String.valueOf(filesCopied)).concat(" files"));
			}
			wait.getOkButton().setEnabled(true);
		} catch (Exception e) {
			log.error("Exception occurred during SwingWorker's done function: " + e.getMessage());
		}
	}
}
