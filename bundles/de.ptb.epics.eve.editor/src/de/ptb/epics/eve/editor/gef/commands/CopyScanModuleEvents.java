package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * Copies events from a scan module to another.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class CopyScanModuleEvents extends Command {
	private ScanModule from;
	private ScanModule to;

	/**
	 * Constructor.
	 * 
	 * @param from the scan module events should be copied from
	 * @param to the scan module events should be copied to
	 */
	public CopyScanModuleEvents(ScanModule from, ScanModule to) {
		this.from = from;
		this.to = to;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		for (ControlEvent event : from.getRedoEvents()) {
			to.addRedoEvent(ControlEvent.newInstance(event));
		}
		for (ControlEvent event : from.getBreakEvents()) {
			to.addBreakEvent(ControlEvent.newInstance(event));
		}
		for (ControlEvent event : from.getTriggerEvents()) {
			to.addTriggerEvent(ControlEvent.newInstance(event));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		for (ControlEvent event : from.getRedoEvents()) {
			to.removeRedoEvent(event);
		}
		for (ControlEvent event : from.getBreakEvents()) {
			to.removeBreakEvent(event);
		}
		for (ControlEvent event : from.getTriggerEvents()) {
			to.removeTriggerEvent(event);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		this.execute();
	}
}