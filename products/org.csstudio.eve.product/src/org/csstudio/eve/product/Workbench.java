package org.csstudio.eve.product;
/**
 * 
 */


import java.util.Map;

import org.csstudio.startup.module.WorkbenchExtPoint;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author mmichals
 *
 */
public class Workbench implements WorkbenchExtPoint {

	/**
	 * 
	 */
	public Workbench() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.csstudio.startup.module.WorkbenchExtPoint#beforeWorkbenchCreation(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	@Override
	public Object beforeWorkbenchCreation(Display display,
			IApplicationContext context, Map<String, Object> parameters)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.startup.module.WorkbenchExtPoint#afterWorkbenchCreation(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	@Override
	public Object afterWorkbenchCreation(Display display,
			IApplicationContext context, Map<String, Object> parameters)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.startup.module.WorkbenchExtPoint#runWorkbench(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	@Override
	public Object runWorkbench(Display display, IApplicationContext context,
			Map<String, Object> parameters) throws Exception {
		// TODO Auto-generated method stub
		
		PlatformUI.createAndRunWorkbench(display,
                new ApplicationWorkbenchAdvisor());
		
		return null;
	}

}
