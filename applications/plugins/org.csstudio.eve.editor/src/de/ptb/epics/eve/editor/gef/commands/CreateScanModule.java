package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class CreateScanModule extends Command {
	
	private Chain chain;
	private Rectangle bounds;
	private ScanModule scanModule;
	
	/**
	 * Constructor.
	 * 
	 * @param scanModule the scan module the scan module should be appended to
	 */
	public CreateScanModule(Chain chain, Rectangle bounds) {
		this.chain = chain;
		this.bounds = bounds;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		int id = this.chain.getAvailableScanModuleId();
		this.scanModule = new ScanModule(id);
		this.scanModule.setX(this.bounds.x);
		this.scanModule.setY(this.bounds.y);
		this.scanModule.setName("SM " + Integer.toString(id));
		this.chain.add(this.scanModule);
		super.execute();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		this.chain.remove(this.scanModule);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		this.chain.add(this.scanModule);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		return true;
	}
}