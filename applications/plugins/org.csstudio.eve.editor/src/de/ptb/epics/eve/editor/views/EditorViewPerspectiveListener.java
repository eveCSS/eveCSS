package de.ptb.epics.eve.editor.views;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;

/**
 * <code>EditorViewPerspectiveListener</code> listens to closing editor events.
 * If the last open editor is closed, it 'informs' its 
 * {@link de.ptb.epics.eve.editor.views.IEditorView} about it by calling 
 * {@link de.ptb.epics.eve.editor.views.IEditorView#reset()}.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 * @see {@link de.ptb.epics.eve.editor.views.IEditorView}
 */
public class EditorViewPerspectiveListener implements IPerspectiveListener {

	private static Logger logger = 
			Logger.getLogger(EditorViewPerspectiveListener.class.getName());
	
	private IEditorView view;
	
	/**
	 * Constructor.
	 * 
	 * @param view the view that should be reset
	 */
	public EditorViewPerspectiveListener(IEditorView view) {
		this.view = view;
	}
	
	/**
	 * {@inheritDoc}
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
		if(page.getWorkbenchWindow().getActivePage() == null) {
			return;
		}
		
		// if a new editor is opened, previously shown content has to be 
		// cleared
		if(changeId.equals(IWorkbenchPage.CHANGE_EDITOR_OPEN)) {
			view.reset();
		}
		
		// only update if no editor is open anymore
		if(page.getEditorReferences().length > 0) {
			return;
		}
		// Note: resetting views generally did not work, because the activation 
		// event of the editor/selection triggered before this event, 
		// such that the views were set with the new content and afterwards 
		// reset as result of the editor closing (vice versa as necessary).
		
		// was the change a close of an editor ?
		if(changeId.equals(IWorkbenchPage.CHANGE_EDITOR_CLOSE)) {
			logger.debug("last open editor was closed -> reset");
			view.reset();
		}
	}
}