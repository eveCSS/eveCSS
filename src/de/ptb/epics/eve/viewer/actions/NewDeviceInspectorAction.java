package de.ptb.epics.eve.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.viewer.DeviceInspectorViewer;
import de.ptb.epics.eve.viewer.DeviceOptionsViewer;

public class NewDeviceInspectorAction extends Action implements
		IWorkbenchAction {

	private final DeviceInspectorViewer deviceInspectorView;
	private static final String ID = "de.ptb.epics.eve.viewer.actions.NewDeviceInspectorAction";
	
	public NewDeviceInspectorAction( final DeviceInspectorViewer deviceInspectorView ) {
		this.deviceInspectorView = deviceInspectorView; 
		this.setId( NewDeviceInspectorAction.ID ); 
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void run() { 
		try {
			final IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView( "DeviceInspectorView", "" + System.nanoTime(), IWorkbenchPage.VIEW_ACTIVATE );
			
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
