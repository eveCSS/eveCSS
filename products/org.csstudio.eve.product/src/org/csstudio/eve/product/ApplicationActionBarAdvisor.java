package org.csstudio.eve.product;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	/**
	 * Constructor.
	 * 
	 * @param configurer
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void makeActions(IWorkbenchWindow window) {
		// due to Eclipse Bug #270007 and #361935
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=270007
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=361935
		// TODO remove in 4.3 ?
		register(ActionFactory.PRINT.create(window));
		register(ActionFactory.UNDO.create(window));
		register(ActionFactory.REDO.create(window));
		register(ActionFactory.DELETE.create(window));
	}

	/**
	 * {@inheritDoc}
	 */
	protected void fillMenuBar(IMenuManager menuBar) {
	}
}