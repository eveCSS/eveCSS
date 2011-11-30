package org.csstudio.eve.product;

import java.util.Map;

import org.csstudio.startup.module.WorkbenchExtPoint;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * <code>Workbench</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class Workbench implements WorkbenchExtPoint {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object beforeWorkbenchCreation(Display display,
			IApplicationContext context, Map<String, Object> parameters)
			throws Exception {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object afterWorkbenchCreation(Display display,
			IApplicationContext context, Map<String, Object> parameters)
			throws Exception {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object runWorkbench(Display display, IApplicationContext context,
			Map<String, Object> parameters) throws Exception {
		return PlatformUI.createAndRunWorkbench(display,
			new ApplicationWorkbenchAdvisor());
	}
}