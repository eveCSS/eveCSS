package de.ptb.epics.eve.viewer.handler.playlist;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;
import de.ptb.epics.eve.viewer.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public class SaveToTextFile extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(SaveToTextFile.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<PlayListEntry> entries = Activator.getDefault().getEcp1Client().getPlayListController().getEntries();
		
		FileDialog fileDialog = new FileDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), SWT.SAVE);
		fileDialog.setText("Select File");
		fileDialog.setFilterExtensions(new String[] {"*.txt"});
		fileDialog.setFilterNames(new String[] {"Textfiles (*.txt)"});
		
		/*
		String filePath;
		String rootdir = Activator.getDefault().getRootDirectory();
		File file = new File(rootdir + "eve/scml/");
		if(file.exists()) {
			filePath = rootdir + "eve/scml/";
		} else {
			filePath = rootdir + "eve/";
		}
		file = null;*/
		File workingDirectory = de.ptb.epics.eve.resources.Activator
				.getDefault().getDefaultsManager().getWorkingDirectory();
		if (workingDirectory.isDirectory()) {
			/*filePath = workingDirectory.getPath();*/
			fileDialog.setFilterPath(workingDirectory.getPath());
		}
		
		String selected = fileDialog.open();
		
		if (selected == null) {
			LOGGER.info("Save Playlist to File Dialog was cancelled");
			return null;
		}
		
		if (new File(selected).exists()) {
			boolean overwrite = MessageDialog.openConfirm(HandlerUtil.getActiveShell(event), "File already exists!", 
					"File already exists, overwrite ?");
			if (!overwrite) {
				LOGGER.info("File already exists but should not be overwritten");
				return null;
			}
		}
		
		BufferedWriter writer = null;
		try {
			File file = new File(selected);
			writer = new BufferedWriter(new FileWriter(file));
			for (PlayListEntry entry : entries) {
				writer.write(entry.getName()+"\n");
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				writer.close();
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		
		return null;
	}
}