package de.ptb.epics.eve.editor;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.ptb.epics.eve.editor.views.axeschannelsview.ui.AxesChannelsView;
import de.ptb.epics.eve.editor.views.chainview.ChainView;
import de.ptb.epics.eve.editor.views.detectorchannelview.ui.DetectorChannelView;
import de.ptb.epics.eve.editor.views.errorview.ErrorView;
import de.ptb.epics.eve.editor.views.motoraxisview.MotorAxisView;
import de.ptb.epics.eve.editor.views.plotwindowview.PlotWindowView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanview.ui.ScanView;

/**
 * <code>EveEditorPerspective</code> is the factory for the editor perspective.
 * <p> 
 * The editor perspective is used to construct and modify scan descriptions.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class EveEditorPerspective implements IPerspectiveFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createInitialLayout(final IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		
		IFolderLayout scanChainViewFolder = layout.createFolder(
				"ScanChainViewFolder", IPageLayout.RIGHT, 0.33f, editorArea);
		scanChainViewFolder.addView(ScanView.ID);
		layout.getViewLayout(ScanView.ID).setCloseable(false);
		scanChainViewFolder.addView(ChainView.ID);
		layout.getViewLayout(ChainView.ID).setCloseable(false);
		
		layout.addView(ErrorView.ID, IPageLayout.BOTTOM , 0.67f, editorArea);
		layout.getViewLayout(ErrorView.ID).setCloseable(false);
		
		layout.addView(MotorAxisView.ID, IPageLayout.RIGHT, 0.55f, ChainView.ID);
		layout.getViewLayout(MotorAxisView.ID).setCloseable(false);
		
		layout.addView(DetectorChannelView.ID, 
				IPageLayout.BOTTOM, 0.33f, MotorAxisView.ID);
		layout.getViewLayout(DetectorChannelView.ID).setCloseable(false);
		
		layout.addView(PlotWindowView.ID, 
				IPageLayout.BOTTOM, 0.5f, DetectorChannelView.ID);
		layout.getViewLayout(PlotWindowView.ID).setCloseable(false);
		
		IFolderLayout scanModuleViewsFolder = layout.createFolder(
				"ScanModuleViews", IPageLayout.BOTTOM, 0.28f, ChainView.ID);
		scanModuleViewsFolder.addView(AxesChannelsView.ID);
		layout.getViewLayout(AxesChannelsView.ID).setCloseable(false);
		scanModuleViewsFolder.addView(ScanModuleView.ID);
		layout.getViewLayout(ScanModuleView.ID).setCloseable(false);
		
		IFolderLayout plotViewFolder = layout.createFolder("PlotViewFolder", 
				IPageLayout.BOTTOM, 0.60f, editorArea);
		plotViewFolder.addPlaceholder("PlotView:*");
	}
}