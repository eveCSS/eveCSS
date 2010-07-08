package de.ptb.epics.eve.viewer;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class EveDevicePerspective implements IPerspectiveFactory {

	public void createInitialLayout( final IPageLayout layout ) {
		layout.setEditorAreaVisible( false );

		layout.addView( "MessagesView", IPageLayout.LEFT, 0.35f, IPageLayout.ID_EDITOR_AREA);
		layout.addView( "LocalDevicesView", IPageLayout.TOP, 0.8f, "MessagesView" );
		layout.addView( "DeviceInspectorView", IPageLayout.RIGHT, 0.50f, IPageLayout.ID_EDITOR_AREA );
		layout.addView( "DeviceOptionsView", IPageLayout.RIGHT, 0.80f, "DeviceInspectorView" );		

	}

}
