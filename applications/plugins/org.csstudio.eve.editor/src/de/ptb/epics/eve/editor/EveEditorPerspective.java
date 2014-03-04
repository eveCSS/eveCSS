package de.ptb.epics.eve.editor;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.ptb.epics.eve.editor.views.chainview.ChainView;
import de.ptb.epics.eve.editor.views.detectorchannelview.DetectorChannelView;
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
		
		layout.addView(ScanView.ID, IPageLayout.RIGHT, 0.33f, editorArea);
		layout.getViewLayout(ScanView.ID).setCloseable(false);
		
		layout.addView(MotorAxisView.ID, IPageLayout.RIGHT, 0.50f, ScanView.ID);
		layout.getViewLayout(MotorAxisView.ID).setCloseable(false);
		
		layout.addView(ErrorView.ID, IPageLayout.BOTTOM , 0.67f, editorArea);
		layout.getViewLayout(ErrorView.ID).setCloseable(false);
		
		layout.addStandaloneView(IPageLayout.ID_OUTLINE, true, IPageLayout.TOP,
				0.67f, ErrorView.ID);
		
		layout.addView(ChainView.ID, IPageLayout.BOTTOM, 0.20f, ScanView.ID);
		layout.getViewLayout(ChainView.ID).setCloseable(false);
		
		layout.addView(ScanModuleView.ID, IPageLayout.BOTTOM, 0.40f, ChainView.ID);
		layout.getViewLayout(ScanModuleView.ID).setCloseable(false);
		
		layout.addView(DetectorChannelView.ID, 
				IPageLayout.BOTTOM, 0.33f, MotorAxisView.ID);
		layout.getViewLayout(DetectorChannelView.ID).setCloseable(false);
		
		layout.addView(PlotWindowView.ID, 
				IPageLayout.BOTTOM, 0.40f, DetectorChannelView.ID);
		layout.getViewLayout(PlotWindowView.ID).setCloseable(false);
		
		IFolderLayout plotViewFolder = layout.createFolder("PlotViewFolder", 
				IPageLayout.BOTTOM, 0.60f, editorArea);
		plotViewFolder.addPlaceholder("PlotView:*");
	}
}