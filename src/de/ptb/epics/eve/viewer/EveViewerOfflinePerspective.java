package de.ptb.epics.eve.viewer;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class EveViewerOfflinePerspective implements IPerspectiveFactory {

	public void createInitialLayout( final IPageLayout layout ) {
		layout.setEditorAreaVisible( false );
		
		

	}

}
