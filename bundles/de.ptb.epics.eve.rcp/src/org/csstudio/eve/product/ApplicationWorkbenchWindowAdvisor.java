package org.csstudio.eve.product;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.osgi.framework.Version;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.resources.init.Startup;

/**
 * <code>ApplicationWorkbenchWindowAdvisor</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	private static Logger LOGGER = Logger.getLogger(
			ApplicationWorkbenchWindowAdvisor.class.getName());
	
	/**
	 * @param configurer
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1024, 768));
		configurer.setShowPerspectiveBar(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowFastViewBars(true);
		configurer.setShowProgressIndicator(true);
		Version version = Platform.getProduct().getDefiningBundle().getVersion();
		StringBuilder sb = new StringBuilder();
		sb.append("eveCSS v" + version.getMajor() + "." + version.getMinor());
		try {
			IMeasuringStation measuringStation = Startup.loadMeasuringStation(LOGGER);
			String name = measuringStation.getName();
			String xmlVersion = measuringStation.getVersion();
			sb.append(" - " 
					+ name
					+ " (XML v"
					+ xmlVersion
					+ ")");
		} catch (Exception e) {
			LOGGER.error("device definition file could not be loaded.");
			
		}
		configurer.setTitle(sb.toString());
	}
}