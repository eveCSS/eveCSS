package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.viewer.DeviceInspectorViewer;

public class RenameDeviceInspector extends Action implements IWorkbenchAction {

	private final DeviceInspectorViewer deviceInspectorView;
	private static final String ID = "de.ptb.epics.eve.viewer.actions.NewDeviceInspectorAction";
	
	public RenameDeviceInspector( final DeviceInspectorViewer deviceInspectorView ) {
		this.deviceInspectorView = deviceInspectorView; 
		this.setId( RenameDeviceInspector.ID ); 
	}
	
	@Override
	public void dispose() {
		

	}

	public void run() { 
		final InputDialog input = new InputDialog( null, "New name for DeviceInspector", "Please enter the new Name for the Device Inspector", this.deviceInspectorView.getPartName(), null );
		if( InputDialog.OK == input.open() ) {
			deviceInspectorView.setName( input.getValue() );
		}
		
	}

}
