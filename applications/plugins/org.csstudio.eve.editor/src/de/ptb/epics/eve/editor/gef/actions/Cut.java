package de.ptb.epics.eve.editor.gef.actions;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Marcus Michalsky
 * @since 1.19
 */
public class Cut extends Copy {

	public Cut(IEditorPart editor) {
		super(editor);
		setId(ActionFactory.CUT.getId());
		setActionDefinitionId("org.eclipse.ui.edit.cut"); //$NON-NLS-1$
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		super.run();
		// TODO delete selected parts
	}
}