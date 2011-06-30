package de.ptb.epics.eve.editor;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;

import de.ptb.epics.eve.editor.views.ErrorView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanview.ScanView;

/**
 * <code>EveEditorPerspectiveListener</code> listens to perspective changes of 
 * the {@link EveEditorPerspective}.
 * <p>
 * It detects whether there are open editor windows. If no editor window is open 
 * the views are reset.
 * 
 * @author Marcus Michalsky
 * @since 0.4.1
 */
public class EveEditorPerspectiveListener implements IPerspectiveListener {

	private static Logger logger = 
			Logger.getLogger(EveEditorPerspectiveListener.class.getName());
	
	/**
	 *{@inheritDoc} 
	 */
	@Override
	public void perspectiveActivated(IWorkbenchPage page,
			IPerspectiveDescriptor perspective) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perspectiveChanged(IWorkbenchPage page,
			IPerspectiveDescriptor perspective, String changeId) {

		// if the program is being closed -> do nothing
		// (otherwise page references would lead to null pointers)
		if(page.getWorkbenchWindow().getActivePage() == null) return;
		
		// only update views if no editor is open anymore
		// (otherwise the setFocus of the then active editor will update them)
		if(page.getEditorReferences().length > 0) return;
		
		// was the change a close of an editor ?
		if(changeId.equals(IWorkbenchPage.CHANGE_EDITOR_CLOSE))
		{
			logger.debug("last open editor was closed");
			
			// get all views
			IViewReference[] ref = page.getViewReferences();
			
			// try to get the scan view
			ScanView scanView = null;
			for(int i = 0; i < ref.length; ++i) {
				if(ref[i].getId().equals(ScanView.ID)) {
					scanView = (ScanView)ref[i].getPart(false);
				}
			}
			
			// scan view found ?
			if(scanView != null) {
				// reset view
				scanView.setCurrentChain(null);
			}
			
			// try to get the scan module view
			ScanModuleView scanModuleView = null;
			for(int i = 0; i < ref.length; ++i) {
				if(ref[i].getId().equals(ScanModuleView.ID)) {
					scanModuleView = (ScanModuleView)ref[i].getPart(false);
				}
			}
			
			// scan module view found ?
			if(scanModuleView != null) {
				// reset view
				scanModuleView.setCurrentScanModule(null);
			}
			
			// try to get the error view
			ErrorView errorView = null;
			for(int i = 0; i < ref.length; ++i) {
				if(ref[i].getId().equals(ErrorView.ID)) {
					errorView = (ErrorView)ref[i].getPart(false);
				}
			}
			
			// error view found ?
			if(errorView != null) {
				errorView.setCurrentScanDescription(null);
			}
		}
	}
}