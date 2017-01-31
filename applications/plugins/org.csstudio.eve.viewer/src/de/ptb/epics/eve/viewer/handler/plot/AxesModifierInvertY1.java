package de.ptb.epics.eve.viewer.handler.plot;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.YAxisModifier;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.plotview.plot.TraceDataCollector;
import de.ptb.epics.eve.viewer.views.plotview.ui.PlotView;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class AxesModifierInvertY1 extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			AxesModifierInvertY1.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		boolean oldState = HandlerUtil.toggleCommandState(event.getCommand());
		IWorkbenchPart part = Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (!(part instanceof PlotView)) {
			throw new ExecutionException("active part is not a plot view");
		}
		List<TraceDataCollector> collectors = 
				((PlotView)part).getPlotFigure().getCollectors();
		if (collectors.size() < 1) {
			throw new ExecutionException("plot window has no traces");
		}
		
		if (oldState) {
			collectors.get(0).setYAxisModifier(YAxisModifier.NONE);
		} else {
			collectors.get(0).setYAxisModifier(YAxisModifier.INVERSE);
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	//@Override
	public boolean isHandled2() {
		LOGGER.debug("is handled ?");
		return true;
	}
}