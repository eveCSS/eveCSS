package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * Copies postscans from a scan module to another.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class CopyScanModulePostScans extends Command {
	private ScanModule from;
	private ScanModule to;
	
	/**
	 * Constructor.
	 * 
	 * @param from scan module postscans should be copied from
	 * @param to scan module postscans should be copied to
	 */
	public CopyScanModulePostScans(ScanModule from, ScanModule to) {
		this.from = from;
		this.to = to;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		for (Postscan postscan : from.getPostscans()) {
			to.add(Postscan.newInstance(postscan));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		for (Postscan postscan : from.getPostscans()) {
			to.remove(postscan);
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