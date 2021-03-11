package de.ptb.epics.eve.editor.handler.pauseconditions;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.PauseCondition;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.chainview.ChainView;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class AddPauseConditionDefaultHandler extends AbstractHandler {
	public static final String ID = 
			"de.ptb.epics.eve.editor.command.addpausecondition";
	public static final String PARAM_ID = 
			"de.ptb.epics.eve.editor.command.addpausecondition.param.id";

	private static final Logger LOGGER = Logger.getLogger(
			AddPauseConditionDefaultHandler.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String deviceId = event.getParameter(PARAM_ID);
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (!(activePart instanceof ChainView)) {
			String message = "Pause Condition could not be added." + 
					" Active part is not Chain View";
			LOGGER.error(message);
			throw new ExecutionException(message);
		}
		Chain chain = ((ChainView)activePart).getCurrentChain();
		IMeasuringStation measuringStation = Activator.getDefault().
				getMeasuringStation();
		AbstractDevice device = measuringStation.getAbstractDeviceById(deviceId);
		if (device == null) {
			String message = "Pause Condition could not be added. Device not found.";
			LOGGER.error(message);
			throw new ExecutionException(message);
		}
		chain.addPauseCondition(new PauseCondition(device));
		return null;
	}
}
