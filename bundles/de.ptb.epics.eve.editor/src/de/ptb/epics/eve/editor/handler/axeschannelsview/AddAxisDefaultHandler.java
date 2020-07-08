package de.ptb.epics.eve.editor.handler.axeschannelsview;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager;
import de.ptb.epics.eve.data.scandescription.defaults.axis.DefaultsAxis;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddAxisDefaultHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			AddAxisDefaultHandler.class.getName());
	
	public static final String COMMAND_ID = 
			"de.ptb.epics.eve.editor.command.scanmodule.addaxis";
	public static final String PARAM_AXIS_ID = 
			"de.ptb.epics.eve.editor.command.scanmodule.addaxis.motoraxisid";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String axisId = event.getParameter(PARAM_AXIS_ID);
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AbstractScanModuleView) {
			ScanModule scanModule = ((AbstractScanModuleView) activePart).
					getScanModule();
			IMeasuringStation measuringStation = Activator.getDefault().
					getMeasuringStation();
			MotorAxis motorAxis = measuringStation.getMotorAxisById(axisId);
			Axis axis = new Axis(scanModule, motorAxis);
			if (ScanModuleTypes.SAVE_AXIS_POSITIONS.equals(scanModule.getType())) {
				axis.setStepfunction(Stepfunctions.PLUGIN);
				PlugIn motionDisabled = measuringStation.getPluginByName(
						"MotionDisabled");
				axis.setPluginController(new PluginController(motionDisabled));
				axis.getPluginController().setPlugin(motionDisabled);
			} else {
				if (axis.getMotorAxis().getGoto().isDiscrete()) {
					axis.getMotorAxis().removePropertyChangeListener(
							MotorAxis.DISCRETE_VALUES_PROP, axis);
					axis.getMotorAxis().disconnect();
				}
				DefaultsAxis defaultsAxis = Activator.getDefault().
						getDefaults().getAxis(motorAxis.getID());
				if (defaultsAxis != null) {
					DefaultsManager.transferDefaults(defaultsAxis, axis);
				}
			}
			scanModule.add(axis);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("MotorAxis " + motorAxis.getName() + " added.");
			}
		} else {
			LOGGER.error("Motor Axis could not be added.");
			throw new ExecutionException("Active Part is not a ScanModuleView.");
		}
		return null;
	}
}
