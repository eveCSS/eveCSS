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
public class PlotNumeric extends PropertyTester {
	public static final String PROPERTY_NAMESPACE = "viewer.plot";
	public static final String PROPERTY_TRACE1NUMERIC = "trace1numeric";
	public static final String PROPERTY_TRACE2NUMERIC = "trace2numeric";

	private static final Logger LOGGER = Logger.getLogger(PlotNumeric.class.getName());
	
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
		if (PROPERTY_TRACE1NUMERIC.equals(property)) {
			if (traces.size() > 0) {
				LOGGER.debug("trace 1 property (numeric) evaluation requested: " + 
						traces.get(0).getTraceInfo().isyAxisNumeric());
				return traces.get(0).getTraceInfo().isyAxisNumeric();
			}
			return false;
		} else if (PROPERTY_TRACE2NUMERIC.equals(property)) {
			if (traces.size() > 1) {
				LOGGER.debug("trace 2 property (numeric) evaluation requested: " + 
						traces.get(1).getTraceInfo().isyAxisNumeric());
				return traces.get(1).getTraceInfo().isyAxisNumeric();
			}
			return false;
		}
		return false;
	}
}