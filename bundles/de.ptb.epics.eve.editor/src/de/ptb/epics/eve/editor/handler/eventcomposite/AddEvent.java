package de.ptb.epics.eve.editor.handler.eventcomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.measuringstation.event.ScheduleEvent;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.ScanDescriptionEditor;
import de.ptb.epics.eve.editor.views.chainview.ChainView;
import de.ptb.epics.eve.editor.views.detectorchannelview.ui.DetectorChannelView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

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
		EventTypes eventType = EventTypes.stringToType(executionEvent.getParameter(
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
		
		Event event = null;
		
		ScanDescription sd = ((ScanDescriptionEditor) Activator.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor()).getContent();
		
		switch(eventType) {
		case DETECTOR:
			DetectorEventValueConverter detectorConv = 
					new DetectorEventValueConverter(sd);
			try {
				event = (DetectorEvent)detectorConv.convertToObject(eventId);
			} catch (ParameterValueConversionException e) {
				logger.error(e.getMessage(), e);
				throw new ExecutionException(e.getMessage(), e);
			}
			break;
		case MONITOR:
			event = Activator.getDefault().getMeasuringStation().
					getEventById(eventId);
			break;
		case SCHEDULE:
			ScheduleEventValueConverter scheduleConv = 
					new ScheduleEventValueConverter(sd);
			try {
				event = (ScheduleEvent)scheduleConv.convertToObject(eventId);
			} catch (ParameterValueConversionException e) {
				logger.error(e.getMessage(), e);
				throw new ExecutionException(e.getMessage(), e);
			}
			break;
		default:
			break;
		}
		
		if (event == null) {
			throw new ExecutionException("unknown event type");
		}
		
		if (activePart.equals("de.ptb.epics.eve.editor.views.ChainView")) {
			Chain chain = ((ChainView)HandlerUtil.getActivePart(executionEvent)).
					getCurrentChain();
			if (eventImpact.equals(EventImpacts.REDO)) {
				chain.addRedoEvent(
						new ControlEvent(eventType, event, eventId));
			} else if (eventImpact.equals(EventImpacts.BREAK)) {
				chain.addBreakEvent(
						new ControlEvent(eventType, event, eventId));
			} else if (eventImpact.equals(EventImpacts.STOP)) {
				chain.addStopEvent(
						new ControlEvent(eventType, event, eventId));
			}
		} else if(activePart.equals(
				"de.ptb.epics.eve.editor.views.ScanModulView")) {
			ScanModule scanModule = ((ScanModuleView)HandlerUtil.getActivePart(
					executionEvent)).getCurrentScanModule();
			if (eventImpact.equals(EventImpacts.REDO)) {
				scanModule.addRedoEvent(
						new ControlEvent(eventType, event, eventId));
			} else if (eventImpact.equals(EventImpacts.BREAK)) {
				scanModule.addBreakEvent(
						new ControlEvent(eventType, event, eventId));
			} else if (eventImpact.equals(EventImpacts.TRIGGER)) {
				scanModule.addTriggerEvent(
						new ControlEvent(eventType, event, eventId));
			}
		} else if(activePart.equals(
				"de.ptb.epics.eve.editor.views.DetectorChannelView")) {
			Channel ch = ((DetectorChannelView)HandlerUtil.getActivePart(
					executionEvent)).getCurrentChannel();
			if (eventImpact.equals(EventImpacts.REDO)) {
				ch.addRedoEvent(new ControlEvent(eventType, event, eventId));
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