package org.csstudio.eve.product;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.startup.module.StartupParametersExtPoint;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

/**
 * <code>StartupParameters</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class StartupParameters implements StartupParametersExtPoint {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> readStartupParameters(Display display,
			IApplicationContext context) throws Exception {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		 // Check command-line arguments
		final String args[] =
			(String []) context.getArguments().get("application.args"); //$NON-NLS-1$
		
		int i = 0;
		while(i < args.length) {
			if (args[i].equals("-eve.root")) {
				i++;
				parameters.put("eve.root", args[i]);
			}
			if (args[i].equals("-eve.debug")) {
				i++;
				parameters.put("eve.debug", args[i]);
			}
			i++;
		}
		
		return parameters;
	}
}