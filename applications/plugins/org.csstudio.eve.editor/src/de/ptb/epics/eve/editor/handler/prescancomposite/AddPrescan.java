package de.ptb.epics.eve.editor.handler.prescancomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * Default handler of the add prescan command.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class AddPrescan implements IHandler {

	private static Logger logger = Logger.getLogger(AddPrescan.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String prescanId = event.getParameter(
				"de.ptb.epics.eve.editor.command.addprescan.prescanid");
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart.getSite().getId().equals(
			"de.ptb.epics.eve.editor.views.ScanModulView")) {
				ScanModule sm = ((ScanModuleView)activePart).
						getCurrentScanModule();
				AbstractPrePostscanDevice dev = sm.getChain().
						getScanDescription().getMeasuringStation().
						getPrePostscanDeviceById(prescanId);
				sm.add(new Prescan(dev));
				if(logger.isDebugEnabled()) {
					logger.debug("Prescan " + dev.getName() + " added.");
				}
		} else {
			logger.warn("Prescan was not added!");
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