package de.ptb.epics.eve.viewer.propertytester;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.plotview.plot.TraceDataCollector;
import de.ptb.epics.eve.viewer.views.plotview.ui.PlotView;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class PlotTraceCount extends PropertyTester {
	public static final String PROPERTY_NAMESPACE = "viewer.plot";
	public static final String PROPERTY_TRACE1PRESENT = "trace1present";
	public static final String PROPERTY_TRACE2PRESENT = "trace2present";

	private static final Logger LOGGER = Logger.getLogger(PlotTraceCount.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		if (workbench == null) {
			return false;
		}
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		if (workbenchWindow == null) {
			return false;
		}
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
		if (workbenchPage == null) {
			return false;
		}
		IWorkbenchPart activePart = workbenchPage.getActivePart();
		if (!(activePart instanceof PlotView)) {
			return false;
		}
		List<TraceDataCollector> traces = ((PlotView)activePart).
				getPlotFigure().getCollectors();
		if (traces.isEmpty()) {
			return false;
		}
		if (PROPERTY_TRACE1PRESENT.equals(property)) {
			LOGGER.debug("trace 1 property evaluation requested: " + 
					Boolean.toString(traces.size() > 0));
			if (traces.size() > 0) {
				return true;
			}
		} else if (PROPERTY_TRACE2PRESENT.equals(property)) {
			LOGGER.debug("trace 2 property evaluation requested: " + 
					Boolean.toString(traces.size() > 1));
			if (traces.size() > 1) {
				return true;
			}
		}
		return false;
	}
}