package de.ptb.epics.eve.editor.views;

import org.eclipse.ui.IMemento;

/**
 * Composites of Views in the Editor often have properties which sould exceed the 
 * lifetime of the application. The Memento behavior is only available in a view. 
 * A view using composites inheriting from this interface can "forward" memento
 * load/save operations to the composite. What should be done is implemented by 
 * the composites themselves.
 * 
 * @author Marcus Michalsky
 * @since 1.36
 */
public interface IEditorViewCompositeMemento {
	/**
	 * Called by the parentView when saving memento. 
	 * Composites should save the configuration that should be persisted here. 
	 * @param memento the memento interface
	 */
	public void saveState(IMemento memento);
	
	/**
	 * Called by the parentView to indicate that persisted states can 
	 * be retrieved now. Only called if memento is not <code>null</code>.
	 * @param memento the memento interface
	 */
	public void restoreState(IMemento memento);
}
