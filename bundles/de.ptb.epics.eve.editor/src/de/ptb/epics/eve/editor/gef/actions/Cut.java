package de.ptb.epics.eve.editor.gef.actions;

import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Marcus Michalsky
 * @since 1.19
 */
public class Cut extends Copy {
	private DeleteAction deleteAction;
	
	/**
	 * Constructor.
	 * 
	 * @param editor the editor
	 */
	public Cut(IEditorPart editor, DeleteAction deleteAction) {
		super(editor);
		setId(ActionFactory.CUT.getId());
		setActionDefinitionId("org.eclipse.ui.edit.cut"); //$NON-NLS-1$
		this.deleteAction = deleteAction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		super.run();
		this.deleteAction.run();
	}
}