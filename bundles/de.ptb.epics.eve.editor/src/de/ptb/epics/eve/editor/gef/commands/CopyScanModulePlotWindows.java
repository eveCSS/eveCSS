package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * Copies plot windows from a scan module to another.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class CopyScanModulePlotWindows extends Command {
	private ScanModule from;
	private ScanModule to;
	
	/**
	 * Constructor.
	 * 
	 * @param from the scan module the plot windows should be copied from
	 * @param to the scan module the plot windows should be copied to
	 */
	public CopyScanModulePlotWindows(ScanModule from, ScanModule to) {
		this.from = from;
		this.to = to;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		for (PlotWindow plotWindow : from.getPlotWindows()) {
			to.add(PlotWindow.newInstance(plotWindow, to));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		for (PlotWindow plotWindow : to.getPlotWindows()) {
			to.remove(plotWindow);
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