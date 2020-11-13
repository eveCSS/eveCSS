package de.ptb.epics.eve.editor.handler.prepostposplotview;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class AddPositioningDefaultHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			AddPositioningDefaultHandler.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String axisId = event.getParameter(
				"de.ptb.epics.eve.editor.command.scanmodule.addpositioning.motoraxisid");
		if (axisId == null) {
			String message = "Mandatory parameter axisId not found.";
			LOGGER.error(message);
			throw new ExecutionException(message);
		}
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AbstractScanModuleView) {
			ScanModule scanModule = ((AbstractScanModuleView)activePart).
					getScanModule();
			MotorAxis motorAxis = Activator.getDefault().getMeasuringStation().
					getMotorAxisById(axisId);
			scanModule.add(new Positioning(scanModule, motorAxis));
		} else {
			String message = "Active Part is not an AbstractScanModuleView";
			LOGGER.error(message);
			throw new ExecutionException(message);
		}
		return null;
	}
}
