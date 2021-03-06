package de.ptb.epics.eve.viewer;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.ptb.epics.eve.viewer.views.plotview.ui.PlotView;

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
		
		String secId = String.valueOf(System.nanoTime());
		
		IFolderLayout deviceInspectorFolder = layout.createFolder(
				"DeviceInspectorFolder", 
				IPageLayout.RIGHT, 1.00f, IPageLayout.ID_EDITOR_AREA);
		deviceInspectorFolder.addPlaceholder("DeviceInspectorView:*");
		deviceInspectorFolder.addView("DeviceInspectorView:" + secId);
		
		layout.addView("DevicesView", IPageLayout.LEFT, 0.40f, 
				"DeviceInspectorView:" + secId);
		
		layout.addView("MessagesView", IPageLayout.BOTTOM, 0.80f, 
				"DevicesView");
		
		layout.addView("PlayListView", IPageLayout.LEFT, 0.66f, "DevicesView");
		
		layout.addView("EngineView", IPageLayout.BOTTOM, 0.5f, "PlayListView");
		
		IFolderLayout plotViewFolder = layout.createFolder("PlotViewFolder", 
				IPageLayout.TOP, 0.50f, "DeviceInspectorView:" + secId);
		plotViewFolder.addPlaceholder(PlotView.ID+":*");
		
		IFolderLayout deviceOptionsFolder = layout.createFolder(
				"DeviceOptionsFolder", 
				IPageLayout.RIGHT, 0.75f, "DeviceInspectorView:" + secId);
		deviceOptionsFolder.addView("DeviceOptionsView:" + secId);

		layout.getViewLayout("PlayListView").setCloseable(false);
		layout.getViewLayout("EngineView").setCloseable(false);
	}
}