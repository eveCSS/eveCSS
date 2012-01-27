package de.ptb.epics.eve.editor.handler;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.graphical.GraphicalEditor;
import de.ptb.epics.eve.editor.views.detectorchannelview.DetectorChannelView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.editor.views.scanview.ScanView;

/**
 * <code>AddEvent</code> is the default command handler for the 
 * <code>de.ptb.epics.eve.editor.command.addevent</code> command.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class AddEvent implements IHandler {

	private static Logger logger = Logger.getLogger(AddEvent.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent executionEvent) throws ExecutionException {
		String eventId = executionEvent.getParameter(
				"de.ptb.epics.eve.editor.command.AddEvent.EventId");
		EventTypes eventType = EventTypes.valueOf(executionEvent.getParameter(
				"de.ptb.epics.eve.editor.command.AddEvent.EventType"));
		EventImpacts eventImpact = EventImpacts.valueOf(executionEvent.getParameter(
				"de.ptb.epics.eve.editor.command.AddEvent.EventImpact"));
		String activePart = executionEvent.getParameter(
				"de.ptb.epics.eve.editor.command.AddEvent.ActivePart");
		
		logger.debug("Adding Event:");
		logger.debug("EventId: "+ eventId);
		logger.debug("EventType: " + eventType);
		logger.debug("EventImpact: "+ eventImpact);
		logger.debug("activePart: "+ activePart);
		
		Event event;
		
		if(eventType.equals(EventTypes.MONITOR)) {
			event = Activator.getDefault().getMeasuringStation().
			getEventById(eventId);
		} else {
			event = ((GraphicalEditor)Activator.getDefault().
					getWorkbench().getActiveWorkbenchWindow().getActivePage().
					getActiveEditor()).getContent().getEventById(eventId);
		}
		
		if (activePart.equals("de.ptb.epics.eve.editor.views.ScanView")) {
			Chain chain = ((ScanView)HandlerUtil.getActivePart(executionEvent)).
					getCurrentChain();
			if (eventImpact.equals(EventImpacts.PAUSE)) {
				chain.getPauseControlEventManager().addControlEvent(
						new PauseEvent(eventType, event, eventId));
			} else if (eventImpact.equals(EventImpacts.REDO)) {
				chain.getRedoControlEventManager().addControlEvent(
						new ControlEvent(eventType, event, eventId));
			} else if (eventImpact.equals(EventImpacts.BREAK)) {
				chain.getBreakControlEventManager().addControlEvent(
						new ControlEvent(eventType, event, eventId));
			} else if (eventImpact.equals(EventImpacts.STOP)) {
				chain.getStopControlEventManager().addControlEvent(
						new ControlEvent(eventType, event, eventId));
			}
		} else if(activePart.equals(
				"de.ptb.epics.eve.editor.views.ScanModulView")) {
			ScanModule scanModule = ((ScanModuleView)HandlerUtil.getActivePart(
					executionEvent)).getCurrentScanModule();
			if (eventImpact.equals(EventImpacts.PAUSE)) {
				scanModule.getPauseControlEventManager().addControlEvent(
						new PauseEvent(eventType, event, eventId));
			} else if (eventImpact.equals(EventImpacts.REDO)) {
				scanModule.getRedoControlEventManager().addControlEvent(
						new ControlEvent(eventType, event, eventId));
			} else if (eventImpact.equals(EventImpacts.BREAK)) {
				scanModule.getBreakControlEventManager().addControlEvent(
						new ControlEvent(eventType, event, eventId));
			} else if (eventImpact.equals(EventImpacts.TRIGGER)) {
				scanModule.getTriggerControlEventManager().addControlEvent(
						new ControlEvent(eventType, event, eventId));
			}
		} else if(activePart.equals(
				"de.ptb.epics.eve.editor.views.DetectorChannelView")) {
			Channel ch = ((DetectorChannelView)HandlerUtil.getActivePart(
					executionEvent)).getCurrentChannel();
			if (eventImpact.equals(EventImpacts.REDO)) {
				ch.getRedoControlEventManager().addControlEvent(
						new ControlEvent(eventType, event, eventId));
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