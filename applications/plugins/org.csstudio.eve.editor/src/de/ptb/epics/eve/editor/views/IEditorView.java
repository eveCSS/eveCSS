package de.ptb.epics.eve.editor.views;

/**
 * Behavior for {@link org.eclipse.ui.part.ViewPart}s registered in the 
 * {@link de.ptb.epics.eve.editor.EveEditorPerspective}.
 * <p>
 * Such Views are reset-able, e.g. when the last open editor is closed as used 
 * in {@link de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener}.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 * @see {@link de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener}
 */
public interface IEditorView {
	/**
	 * Resets its contents to reestablish its initial state.
	 */
	public void reset();
}