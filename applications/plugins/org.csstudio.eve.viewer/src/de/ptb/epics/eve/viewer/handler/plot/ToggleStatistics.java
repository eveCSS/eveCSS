package de.ptb.epics.eve.viewer.handler.plot;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.viewer.views.plotview.ui.PlotView;

/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public class ToggleStatistics extends AbstractHandler {
	private static final Logger LOGGER = Logger
			.getLogger(ToggleStatistics.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (HandlerUtil.getActivePart(event) instanceof PlotView) {
			State state = event.getCommand().getState(
					"org.eclipse.ui.commands.toggleState");
				boolean currentState = (Boolean)state.getValue();
			((PlotView) HandlerUtil.getActivePart(event))
					.showStatistics(!currentState);
			HandlerUtil.toggleCommandState(event.getCommand());
		} else {
			LOGGER.warn("no active plot view detected!");
		}
		return null;
	}
}