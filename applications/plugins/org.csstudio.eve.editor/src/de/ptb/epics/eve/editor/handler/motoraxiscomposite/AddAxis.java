package de.ptb.epics.eve.editor.handler.motoraxiscomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsAxis;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * Default handler of the add axis command.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class AddAxis implements IHandler {

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
			ScanModule sm = ((ScanModuleView) activePart)
					.getCurrentScanModule();
			MotorAxis ma = sm.getChain().getScanDescription()
					.getMeasuringStation().getMotorAxisById(axisId);
			Axis sma = new Axis(sm, ma);
			DefaultsAxis defMa = Activator.getDefault().getDefaults()
					.getAxis(ma.getID());
			if (defMa != null) {
				DefaultsManager.transferDefaults(defMa, sma);
			}
			sm.add(sma);
			if (logger.isDebugEnabled()) {
				logger.debug("MotorAxis " + ma.getName() + " added.");
			}
		} else {
			logger.warn("Motor Axis was not added!");
			throw new ExecutionException("ScanModulView is not the active part!");
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHandled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}
}