package de.ptb.epics.eve.viewer.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.MessageSource;
import de.ptb.epics.eve.viewer.MessageTypes;
import de.ptb.epics.eve.viewer.ViewerMessage;

public class AddFileToPlayListAction extends Action implements IWorkbenchAction {

	private static final String ID = "de.ptb.epics.eve.viewer.actions.AddFileToPlayListAction";  
	
	public AddFileToPlayListAction(){  
		this.setId( AddFileToPlayListAction.ID );  
	} 
	
	public void run() {  
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog fileDialog = new FileDialog( shell, SWT.OPEN | SWT.MULTI );
		fileDialog.open();
		String[] names = fileDialog.getFileNames(); 

		for( int i = 0; i < names.length; ++i ) {
			try {
				Activator.getDefault().getEcp1Client().getPlayListController().addLocalFile( fileDialog.getFilterPath() + File.separator + names[i] );
				Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, "Adding file with the name: " + names[i] + "." ) );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}   
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
