package de.ptb.epics.eve.editor.handler.eventcomposite;

import static de.ptb.epics.eve.editor.views.eventcomposite.EventMenuContributionHelper.determineEventImpact;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.chainview.ChainView;
import de.ptb.epics.eve.editor.views.detectorchannelview.ui.DetectorChannelView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * Default handler of the remove all events command.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class RemoveAllEvents implements IHandler {

	private static Logger logger = 
			Logger.getLogger(RemoveAllEvents.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		EventImpacts eventImpact = determineEventImpact();
		
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection)selection).getFirstElement();
			if(!(o instanceof ControlEvent)) {
				logger.warn("could not remove event");
				return null;
			}
		} else {
			logger.warn("could not remove event");
			return null;
		}
		
		if (activePart.getSite().getId().equals(
				"de.ptb.epics.eve.editor.views.ChainView")) {
			Chain chain = ((ChainView)HandlerUtil.getActivePart(event)).
				getCurrentChain();
			if (eventImpact.equals(EventImpacts.REDO)) {
				chain.removeRedoEvents();
			} else if (eventImpact.equals(EventImpacts.BREAK)) {
				chain.removeBreakEvents();
			} else if (eventImpact.equals(EventImpacts.STOP)) {
				chain.removeStopEvents();
			}
		} else if (activePart.getSite().getId().equals(
				"de.ptb.epics.eve.editor.views.ScanModulView")) {
			ScanModule scanModule = ((ScanModuleView)HandlerUtil.getActivePart(
				event)).getCurrentScanModule();
			if (eventImpact.equals(EventImpacts.REDO)) {
				scanModule.removeRedoEvents();
			} else if (eventImpact.equals(EventImpacts.BREAK)) {
				scanModule.removeBreakEvents();
			} else if (eventImpact.equals(EventImpacts.TRIGGER)) {
				scanModule.removeTriggerEvents();
			}
		} else if (activePart.getSite().getId().equals(
				"de.ptb.epics.eve.editor.views.DetectorChannelView")) {
			Channel ch = ((DetectorChannelView)HandlerUtil.getActivePart(
				event)).getCurrentChannel();
			if (eventImpact.equals(EventImpacts.REDO)) {
				ch.getRedoControlEventManager().removeAllEvents();
			}
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