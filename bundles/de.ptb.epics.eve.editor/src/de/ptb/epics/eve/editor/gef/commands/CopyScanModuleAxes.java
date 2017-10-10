package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * Copies axes of a scan module to another.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class CopyScanModuleAxes extends Command {
	private ScanModule from;
	private ScanModule to;
	
	/**
	 * Constructor.
	 * 
	 * @param from scan module axes should be copied from
	 * @param to scan module axes should be copied to
	 */
	public CopyScanModuleAxes(ScanModule from, ScanModule to) {
		this.from = from;
		this.to = to;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		for (Axis axis : from.getAxes()) {
			to.add(Axis.newInstance(axis, to));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		for (Axis axis : from.getAxes()) {
			to.remove(axis);
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