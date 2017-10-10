package de.ptb.epics.eve.editor.handler.motoraxiscomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

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
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * Default handler of the add axis command.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class AddAxis extends AbstractHandler {
	private static Logger logger = Logger.getLogger(AddAxis.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String axisId = event.getParameter(
				"de.ptb.epics.eve.editor.command.addaxis.motoraxisid");
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart.getSite().getId()
				.equals("de.ptb.epics.eve.editor.views.ScanModulView")) {
			ScanModule scanModule = ((ScanModuleView) activePart)
					.getCurrentScanModule();
			MotorAxis motorAxis = scanModule.getChain().getScanDescription()
					.getMeasuringStation().getMotorAxisById(axisId);
			Axis axis = new Axis(scanModule, motorAxis);
			if (scanModule.getType() == ScanModuleTypes.SAVE_AXIS_POSITIONS) {
				axis.setStepfunction(Stepfunctions.PLUGIN);
				PlugIn motionDisabled = scanModule.getChain().getScanDescription().
						getMeasuringStation().getPluginByName("MotionDisabled");
				axis.setPluginController(new PluginController(motionDisabled));
				axis.getPluginController().setPlugin(motionDisabled);
			} else {
				DefaultsAxis defMa = Activator.getDefault().getDefaults()
						.getAxis(motorAxis.getID());
				if (defMa != null) {
					axis.getMotorAxis().removePropertyChangeListener(
							MotorAxis.DISCRETE_VALUES_PROP, axis);
					axis.getMotorAxis().disconnect();
					DefaultsManager.transferDefaults(defMa, axis);
				}
			}
			scanModule.add(axis);
			if (logger.isDebugEnabled()) {
				logger.debug("MotorAxis " + motorAxis.getName() + " added.");
			}
		} else {
			logger.warn("Motor Axis was not added!");
			throw new ExecutionException("ScanModulView is not the active part!");
		}
		return null;
	}
}