package de.ptb.epics.eve.viewer.handler.playlist;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import de.ptb.epics.eve.util.hdf5.HDF5Util;
import de.ptb.epics.eve.util.hdf5.SCMLExtractionException;
import de.ptb.epics.eve.util.hdf5.SCMLNotFoundException;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.MessageSource;
import de.ptb.epics.eve.viewer.views.messagesview.Levels;
import de.ptb.epics.eve.viewer.views.messagesview.ViewerMessage;

/**
 * @author Marcus Michalsky
 * @since 1.29
 */
public class AddFileToPlaylist extends AbstractHandler {
	private static final Logger LOGGER = 
			Logger.getLogger(AddFileToPlaylist.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getShell();
		if (shell == null) {
			throw new ExecutionException(
					"Could not get Shell of Workbench Window!");
		}
		String filterPath = de.ptb.epics.eve.resources.Activator.getDefault().
				getFilterPath();
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
		fileDialog.setFilterPath(filterPath);
		fileDialog.setFilterExtensions(new String[] {"*.scml;*.h5"});
		fileDialog.setFilterNames(
				new String[] { "Scan Description (*.scml,*.h5)" });
		fileDialog.open();
		
		// add chosen files to playlist
		for(String fileName : fileDialog.getFileNames()) {
			String filePath = fileDialog.getFilterPath() + 
					File.separator + fileName;
			if (fileName.endsWith(".h5")) {
				this.addH5File(filePath);
			} else {
				this.addSCMLFile(filePath);
			}
			
		}
		
		// refresh defaults working directory
		File chosenFile = new File(fileDialog.getFilterPath() + File.separator 
				+ fileDialog.getFileName());
		if (chosenFile.isFile()) {
			de.ptb.epics.eve.resources.Activator.getDefault().
					getDefaultsManager().setWorkingDirectory(
							chosenFile.getParentFile());
		}
		return null;
	}
	
	private void addH5File(String path) {
			try {
				File tempFile = HDF5Util.getSCMLTempFile(new File(path));
				this.addSCMLFile(tempFile.getAbsolutePath());
			} catch (SCMLNotFoundException e) {
				Activator.getDefault().getMessageList().add(
						new ViewerMessage(MessageSource.UNKNOWN, Levels.MINOR,
							"File " + path + 
							" could not be added (No embedded SCML found)!"));
			} catch (SCMLExtractionException e) {
				Activator.getDefault().getMessageList().add(
						new ViewerMessage(MessageSource.UNKNOWN, Levels.ERROR,
							"File " + path + 
							" could not be added (See the log for details)!"));
				LOGGER.error(e.getMessage(), e);
			}
		
	}
	
	private void addSCMLFile(String path) {
		try {
			Activator.getDefault().getEcp1Client().getPlayListController().
				addLocalFile(path);
			Activator.getDefault().getMessageList().add(
					new ViewerMessage(MessageSource.UNKNOWN, Levels.INFO,
						"Adding file " + path + " to playlist."));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}