package de.ptb.epics.eve.editor.views.errorview;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.gef.ScanDescriptionEditor;

/**
 * <code>PartListener</code> listens to 
 * {@link de.ptb.epics.eve.editor.graphical.GraphicalEditor} part activations 
 * and notifies the {@link de.ptb.epics.eve.editor.views.errorview.ErrorView} 
 * about them.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class PartListener implements IPartListener {
	
	private static Logger logger = 
			Logger.getLogger(PartListener.class.getName());
	
	private ErrorView errorView;
	
	/**
	 * Constructor.
	 * 
	 * @param errorView the 
	 * 		{@link de.ptb.epics.eve.editor.views.errorview.ErrorView} work is 
	 * 		delegated from.
	 */
	public PartListener(ErrorView errorView) {
		this.errorView = errorView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partActivated(IWorkbenchPart part) {
		if(part instanceof ScanDescriptionEditor) {
			if(logger.isDebugEnabled()) {
				logger.debug("Editor '" + part.getTitle() + "' activated.");
			}
			ScanDescriptionEditor ge = (ScanDescriptionEditor) part;
			ScanDescription sd = ge.getContent();
			this.errorView.setCurrentScanDescription(sd);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partClosed(IWorkbenchPart part) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partDeactivated(IWorkbenchPart part) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void partOpened(IWorkbenchPart part) {
	}
}