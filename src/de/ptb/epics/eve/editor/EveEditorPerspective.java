package de.ptb.epics.eve.editor;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.ptb.epics.eve.editor.views.ErrorView;
import de.ptb.epics.eve.editor.views.detectorchannelview.DetectorChannelView;
import de.ptb.epics.eve.editor.views.motoraxisview.MotorAxisView;
import de.ptb.epics.eve.editor.views.plotwindowview.PlotWindowView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanview.ScanView;

/**
 * <code>EveEditorPerspective</code> is the only perspective of the editor 
 * plugin.
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
		
		layout.addView(ScanView.ID, IPageLayout.RIGHT, 0.45f, editorArea);
		layout.getViewLayout(ScanView.ID).setCloseable(false);
		
		layout.addView(MotorAxisView.ID, IPageLayout.RIGHT, 0.75f, ScanView.ID);
		layout.getViewLayout(MotorAxisView.ID).setCloseable(false);
		
		layout.addView(DetectorChannelView.ID, 
				IPageLayout.BOTTOM, 0.3f, MotorAxisView.ID);
		layout.getViewLayout(DetectorChannelView.ID).setCloseable(false);
		
		layout.addView(PlotWindowView.ID, 
				IPageLayout.BOTTOM, 0.50f, DetectorChannelView.ID);
		layout.getViewLayout(PlotWindowView.ID).setCloseable(false);
		
		layout.addView(ScanModuleView.ID, IPageLayout.BOTTOM, 0.38f, ScanView.ID);
		layout.getViewLayout(ScanModuleView.ID).setCloseable(false);
		
		layout.addView(ErrorView.ID, IPageLayout.BOTTOM , 0.8f, editorArea);
		layout.getViewLayout(ErrorView.ID).setCloseable(false);
		
		layout.addActionSet("de.ptb.epics.eve.editor.handOverActionSet");
	}
}