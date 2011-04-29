package de.ptb.epics.eve.editor;

import org.eclipse.ui.IFolderLayout;
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
public class EveEditorPerspective implements IPerspectiveFactory  {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createInitialLayout(final IPageLayout layout) {
		
		// (most of) the elements are positioned relative to the editor area, 
		// therefore get the identifier of it
		String editorArea = layout.getEditorArea();
		// the editor should be visible here
		layout.setEditorAreaVisible(true);
		
		// the motor axis view
		IFolderLayout motorAxisFolder = layout.createFolder(
				"motorAxis", IPageLayout.RIGHT, 0.75f, editorArea);
		motorAxisFolder.addPlaceholder(MotorAxisView.ID + ":*");
		motorAxisFolder.addView(MotorAxisView.ID);
		layout.getViewLayout(MotorAxisView.ID).setCloseable(false);
		
		// the detector channel view
		IFolderLayout detectorChannelFolder = layout.createFolder(
				"detectorChannels", IPageLayout.BOTTOM, 0.3f, "motorAxis");
		detectorChannelFolder.addPlaceholder(DetectorChannelView.ID + ":*");
		detectorChannelFolder.addView(DetectorChannelView.ID);
		layout.getViewLayout(DetectorChannelView.ID).setCloseable(false);
		
		// the plot window view
		IFolderLayout plotWindowFolder = layout.createFolder(
				"plotWindows", IPageLayout.BOTTOM, 0.50f, "detectorChannels");	
		plotWindowFolder.addPlaceholder(PlotWindowView.ID + ":*");
		plotWindowFolder.addView(PlotWindowView.ID);
		layout.getViewLayout(PlotWindowView.ID).setCloseable(false);

		// the scan view
		IFolderLayout scanFolder = layout.createFolder(
				"scan", IPageLayout.RIGHT, 0.45f, editorArea);
		scanFolder.addPlaceholder(ScanView.ID + ":*");
		scanFolder.addView(ScanView.ID);
		layout.getViewLayout( ScanView.ID ).setCloseable(false);

		// the scan modul view
		IFolderLayout scanModulFolder = layout.createFolder(
				"scanModul", IPageLayout.BOTTOM, 0.38f, "scan");		
		scanModulFolder.addPlaceholder(ScanModuleView.ID + ":*");
		scanModulFolder.addView(ScanModuleView.ID);
		layout.getViewLayout(ScanModuleView.ID).setCloseable(false);
		
		// the messages view
		IFolderLayout errorFolder = layout.createFolder(
				"error", IPageLayout.BOTTOM , 0.8f, editorArea);
		errorFolder.addPlaceholder(ErrorView.ID + ":*");
		errorFolder.addView(ErrorView.ID);
		layout.getViewLayout(ErrorView.ID).setCloseable(false);

		// register the handover actions
		layout.addActionSet("de.ptb.epics.eve.editor.handOverActionSet");
	}
}