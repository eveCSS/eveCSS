package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.viewer.Activator;

public class ClearMessagesAction extends Action implements IWorkbenchAction {

	private static final String ID = "de.ptb.epics.eve.viewer.actions.ClearMessagesAction";  
	
	public ClearMessagesAction(){  
		this.setId( ClearMessagesAction.ID );  
	} 
	
	public void run() {  
		Activator.getDefault().getMessagesContainer().clear();
	}   
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
