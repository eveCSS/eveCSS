package de.ptb.epics.eve.viewer.handler.messages;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;

/**
 * @author Marcus Michalsky
 * @since 1.4
 */
public class Save implements IHandler {

	private static Logger logger = Logger.getLogger(Save.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// file dialog
		String filePath = Activator.getDefault().getRootDirectory();
		Shell shell = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setFilterPath(filePath);
		String name = fileDialog.open();
		if (name == null) {
			logger.debug("Save aborted.");
			return null;
		}
		
		File file = new File(name);
		if (file.exists()) {
			String[] labels = {"Yes", "No"};
			MessageDialog messageDialog = new MessageDialog(shell,
					"File already exists", null, 
					"File already exists, overwrite ?",
					MessageDialog.WARNING, labels, 1);
			int returnCode = messageDialog.open();
			if(returnCode == 1) {
				logger.debug("Save aborted (file already exists).");
				return null;
			}
		}
		
		List<ViewerMessage> messages = Activator.getDefault().
				getMessagesContainer().getList();
		de.ptb.epics.eve.viewer.jobs.messages.Save saveJob = 
				new de.ptb.epics.eve.viewer.jobs.messages.Save(
						"Save Messages to File", name, messages);
		saveJob.setUser(true);
		saveJob.schedule();
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHandled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}
}