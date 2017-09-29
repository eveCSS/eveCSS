package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * Copies positionings from a scan module to another.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class CopyScanModulePositionings extends Command {
	private ScanModule from;
	private ScanModule to;
	
	/**
	 * Constructor.
	 * 
	 * @param from the scan module positionings should be copied from
	 * @param to the scan module positionings should be copied to
	 */
	public CopyScanModulePositionings(ScanModule from, ScanModule to) {
		this.from = from;
		this.to = to;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		for (Positioning positioning : from.getPositionings()) {
			to.add(Positioning.newInstance(positioning, to));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		for (Positioning positioning : from.getPositionings()) {
			to.remove(positioning);
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