package org.csstudio.eve.product;
/**
 * 
 */


import java.util.HashMap;
import java.util.Map;

import org.csstudio.startup.module.StartupParametersExtPoint;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

/**
 * @author mmichals
 *
 */
public class StartupParameters implements StartupParametersExtPoint {

	/**
	 * 
	 */
	public StartupParameters() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.csstudio.startup.module.StartupParametersExtPoint#readStartupParameters(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
	public Map<String, Object> readStartupParameters(Display display,
			IApplicationContext context) throws Exception {
		
		final Map<String, Object> parameters = new HashMap<String, Object>();
		 // Check command-line arguments
		final String args[] =
           (String []) context.getArguments().get("application.args"); //$NON-NLS-1$
		
		return parameters;
	}

}
