package de.ptb.epics.eve.editor;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.ptb.epics.eve.editor.views.DetectorChannelView;
import de.ptb.epics.eve.editor.views.ErrorView;
import de.ptb.epics.eve.editor.views.MotorAxisView;
import de.ptb.epics.eve.editor.views.PlotWindowView;
import de.ptb.epics.eve.editor.views.ScanModulView;
import de.ptb.epics.eve.editor.views.ScanView;

public class EveEditorPerspective implements IPerspectiveFactory  {

	@Override
	public void createInitialLayout( final IPageLayout layout ) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		
		IFolderLayout motorAxisFolder = layout.createFolder("motorAxis", IPageLayout.RIGHT, 0.75f, editorArea);
		motorAxisFolder.addPlaceholder(MotorAxisView.ID + ":*");
		motorAxisFolder.addView(MotorAxisView.ID);
		layout.getViewLayout( MotorAxisView.ID ).setCloseable( false );
		
		
		IFolderLayout detectorChannelFolder = layout.createFolder("detectorChannels", IPageLayout.BOTTOM, 0.3f, "motorAxis");
		detectorChannelFolder.addPlaceholder(DetectorChannelView.ID + ":*");
		detectorChannelFolder.addView(DetectorChannelView.ID);
		layout.getViewLayout( DetectorChannelView.ID ).setCloseable( false );
		
		IFolderLayout plotWindowFolder = layout.createFolder("plotWindows", IPageLayout.BOTTOM, 0.50f, "detectorChannels");	
		plotWindowFolder.addPlaceholder(PlotWindowView.ID + ":*");
		plotWindowFolder.addView(PlotWindowView.ID);
		layout.getViewLayout( PlotWindowView.ID ).setCloseable( false );

		IFolderLayout scanFolder = layout.createFolder("scan", IPageLayout.RIGHT, 0.45f, editorArea);
		scanFolder.addPlaceholder(ScanView.ID + ":*");
		scanFolder.addView(ScanView.ID);
		layout.getViewLayout( ScanView.ID ).setCloseable( false );

		IFolderLayout scanModulFolder = layout.createFolder("scanModul", IPageLayout.BOTTOM, 0.38f, "scan");
		
		scanModulFolder.addPlaceholder(ScanModulView.ID + ":*");
		scanModulFolder.addView(ScanModulView.ID);
		layout.getViewLayout( ScanModulView.ID ).setCloseable( false );
		
		IFolderLayout errorFolder = layout.createFolder("error", IPageLayout.BOTTOM , 0.8f, editorArea );
		errorFolder.addPlaceholder(ErrorView.ID + ":*");
		errorFolder.addView(ErrorView.ID);
		layout.getViewLayout( ErrorView.ID ).setCloseable( false );

	
		
		layout.addActionSet( "de.ptb.epics.eve.editor.handOverActionSet" );
		
		

	}

}
