package de.ptb.epics.eve.viewer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener;
import de.ptb.epics.eve.ecp1.client.model.Error;

public class EngineErrorReader implements IErrorListener {

	public void errorOccured( final Error error ) {
		
		Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( error ) );
		
		if (error.getText().contains("unable to use filename")) {
			Activator.getDefault().getWorkbench().getDisplay().asyncExec(new Runnable () {
				public void run() {
					Shell shell = Activator.getDefault().getWorkbench().getDisplay().getActiveShell();
					MessageDialog.openWarning(shell, "Warning", error.getText());
				}
			});
		}

	}

}
