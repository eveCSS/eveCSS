package de.ptb.epics.eve.viewer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.ptb.epics.eve.viewer.views.plotview.PlotView;

/**
 * 
 */
public final class EveEnginePerspective implements IPerspectiveFactory {

	/**
	 * {@inheritDoc}
	 */
	public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible( false );
		
		layout.addView( "MessagesView", IPageLayout.LEFT, 0.35f, IPageLayout.ID_EDITOR_AREA);
		layout.addView( "MeasuringStationView", IPageLayout.TOP, 0.8f, "MessagesView" );
		layout.addView( "PlayListView", IPageLayout.LEFT, 0.5f, "MeasuringStationView" );
		layout.addView( "EngineView", IPageLayout.BOTTOM, 0.75f, "PlayListView" );
		
		IFolderLayout folder = layout.createFolder("DeviceInspectorFolder", IPageLayout.RIGHT, 0.50f, IPageLayout.ID_EDITOR_AREA );
		folder.addPlaceholder("DeviceInspectorView:*");
		folder.addView( "DeviceInspectorView" );
		
		//layout.addView( "PlotView", IPageLayout.TOP, 0.50f, "DeviceInspectorView" );
        folder = layout.createFolder("PlotViewFolder", IPageLayout.TOP, 0.50f, "DeviceInspectorView");
        folder.addPlaceholder(PlotView.ID+":*");
        //folder.addView(PlotView.ID);
		layout.addView( "DeviceOptionsView", IPageLayout.RIGHT, 0.80f, "DeviceInspectorView" );
		
		layout.addActionSet( "de.ptb.epics.eve.viewer.engineControlActionSet" );
	}
}