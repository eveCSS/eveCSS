package de.ptb.epics.eve.editor.propertytester;

import org.apache.log4j.Logger;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import de.ptb.epics.eve.data.scandescription.MonitorOption;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanview.ui.ScanView;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class MonitorOptionCustom extends PropertyTester {
	private static final Logger LOGGER = Logger.getLogger(
			MonitorOptionCustom.class.getName());
	public static final String PROPERTY_NAMESPACE = "editor.scanview";
	public static final String PROPERTY_MONITOR_OPTION_CUSTOM = 
			"monitorOptionCustom";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (property.equals(MonitorOptionCustom.PROPERTY_MONITOR_OPTION_CUSTOM)) {
			IWorkbenchWindow activeWindow = Activator.getDefault().
					getWorkbench().getActiveWorkbenchWindow();
			if (activeWindow == null) {
				LOGGER.debug("no active window");
				return false;
			}
			IWorkbenchPage activePage = activeWindow.getActivePage();
			if (activePage == null) {
				LOGGER.debug("no active page");
				return false;
			}
			IWorkbenchPart activePart = activePage.getActivePart();
			if (activePart == null) {
				LOGGER.debug("no active part");
				return false;
			}
			if (!activePart.getSite().getId().equals(ScanView.ID)) {
				LOGGER.debug("wrong view");
				return false;
			}
			ScanDescription scanDescription = ((ScanView)activePart).
					getCurrentScanDescription();
			if (scanDescription == null) {
				LOGGER.debug("no scanDescription available");
				return false;
			}
			boolean customMonitor = scanDescription.getMonitorOption().
					equals(MonitorOption.CUSTOM);
			LOGGER.debug("evaluated to " + customMonitor);
			return customMonitor;
		}
		return false;
	}
}