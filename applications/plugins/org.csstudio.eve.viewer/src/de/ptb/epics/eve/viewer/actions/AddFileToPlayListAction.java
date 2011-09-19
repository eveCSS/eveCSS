package de.ptb.epics.eve.viewer.actions;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.MessageSource;
import de.ptb.epics.eve.viewer.messages.MessageTypes;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class AddFileToPlayListAction extends Action implements IWorkbenchAction {

	private static Logger logger = 
			Logger.getLogger(AddFileToPlayListAction.class.getName());
	
	private static final String ID = 
			"de.ptb.epics.eve.viewer.actions.AddFileToPlayListAction";
	
	/**
	 * 
	 */
	public AddFileToPlayListAction(){
		this.setId( AddFileToPlayListAction.ID);
	} 
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		String filePath;
		String rootdir = Activator.getDefault().getRootDirectory();
		File file = new File(rootdir + "scml/");
		if(file.exists()) {
			filePath = rootdir + "scml/";
		} else {
			filePath = rootdir;
		}
		file = null;
		
		logger.debug(filePath);
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
		fileDialog.setFilterPath(filePath);
		String[] extensions = {"*.scml"};
		fileDialog.setFilterExtensions(extensions);
		fileDialog.open();
		String[] names = fileDialog.getFileNames();

		for(int i = 0; i < names.length; ++i) {
			try {
				Activator.getDefault().getEcp1Client().getPlayListController().
					addLocalFile(fileDialog.getFilterPath() + 
								 File.separator + names[i]);
				Activator.getDefault().getMessagesContainer().addMessage(
					new ViewerMessage(MessageSource.UNKNOWN, MessageTypes.INFO, 
							"Adding file with the name: " + names[i] + "."));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}
}