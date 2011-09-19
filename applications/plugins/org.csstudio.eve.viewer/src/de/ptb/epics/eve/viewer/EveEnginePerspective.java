package de.ptb.epics.eve.viewer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.ptb.epics.eve.viewer.views.plotview.PlotView;

/**
 * <code>EveEnginePerspective</code> is the factory for the engine perspective.
 * <p>
 * The perspective is usually used when running scan descriptions to monitor the 
 * progress and measured data.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public final class EveEnginePerspective implements IPerspectiveFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		
		layout.addView("MessagesView", IPageLayout.LEFT, 0.35f, 
				IPageLayout.ID_EDITOR_AREA);
		layout.addView("DevicesView", IPageLayout.TOP, 0.8f, "MessagesView");
		layout.addView("PlayListView", IPageLayout.LEFT, 0.5f, "DevicesView");
		layout.addView("EngineView", IPageLayout.BOTTOM, 0.5f, "PlayListView");
		
		String sec_id = String.valueOf(System.nanoTime());
		
		IFolderLayout deviceInspectorFolder = layout.createFolder(
				"DeviceInspectorFolder", 
				IPageLayout.RIGHT, 0.50f, IPageLayout.ID_EDITOR_AREA);
		deviceInspectorFolder.addPlaceholder("DeviceInspectorView:*");
		deviceInspectorFolder.addView("DeviceInspectorView:" + sec_id);
		
		IFolderLayout plotViewFolder = layout.createFolder("PlotViewFolder", 
				IPageLayout.TOP, 0.50f, "DeviceInspectorView:" + sec_id);
		plotViewFolder.addPlaceholder(PlotView.ID+":*");
		
		IFolderLayout deviceOptionsFolder = layout.createFolder(
				"DeviceOptionsFolder", 
				IPageLayout.RIGHT, 0.80f, "DeviceInspectorView:" + sec_id);
		deviceOptionsFolder.addView("DeviceOptionsView:" + sec_id);
		
		layout.getViewLayout("PlayListView").setCloseable(false);
		layout.getViewLayout("EngineView").setCloseable(false);
	}
}