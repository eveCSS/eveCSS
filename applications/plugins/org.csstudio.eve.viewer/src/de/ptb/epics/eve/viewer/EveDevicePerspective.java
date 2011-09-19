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
 */
public class EveDevicePerspective implements IPerspectiveFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible(false);

		layout.addView("MessagesView", IPageLayout.LEFT, 0.25f, 
				IPageLayout.ID_EDITOR_AREA);
		layout.addView("LocalDevicesView", 
				IPageLayout.TOP, 0.8f, "MessagesView");
	
		String sec_id = String.valueOf(System.nanoTime());
		
		IFolderLayout deviceInspectorFolder = 
				layout.createFolder("DeviceInspectorFolder", 
				IPageLayout.RIGHT, 0.40f, IPageLayout.ID_EDITOR_AREA);
		deviceInspectorFolder.addPlaceholder("DeviceInspectorView:*");	
		deviceInspectorFolder.addView("DeviceInspectorView:" + sec_id);
		
		IFolderLayout deviceOptionsFolder = 
				layout.createFolder("DeviceOptionsFolder", 
				IPageLayout.RIGHT, 0.80f, "DeviceInspectorView:" + sec_id);
		deviceOptionsFolder.addView("DeviceOptionsView:" + sec_id);
	}
}