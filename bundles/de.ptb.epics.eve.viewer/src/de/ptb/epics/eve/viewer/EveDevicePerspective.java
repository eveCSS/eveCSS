package de.ptb.epics.eve.viewer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * <code>EveDevicePerspective</code> is the factory for the device perspective.
 * <p>
 * The device perspective usually is used to monitor devices of the loaded 
 * measuring station and sending (channel access) commands to them.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class EveDevicePerspective implements IPerspectiveFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		
		String secId = String.valueOf(System.nanoTime());
		
		IFolderLayout deviceInspectorFolder = 
				layout.createFolder("DeviceInspectorFolder", 
				IPageLayout.RIGHT, 1.00f, IPageLayout.ID_EDITOR_AREA);
		deviceInspectorFolder.addPlaceholder("DeviceInspectorView:*");	
		deviceInspectorFolder.addView("DeviceInspectorView:" + secId);
		
		IFolderLayout deviceOptionsFolder = 
				layout.createFolder("DeviceOptionsFolder", 
				IPageLayout.RIGHT, 0.66f, "DeviceInspectorView:" + secId);
		deviceOptionsFolder.addView("DeviceOptionsView:" + secId);

		layout.addView("MessagesView", 
				IPageLayout.BOTTOM, 0.85f, "DeviceInspectorView:" + secId);

		layout.addView("LocalDevicesView", 
				IPageLayout.LEFT, 0.25f, "DeviceInspectorView:" + secId);
		
	}
}