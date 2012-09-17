package de.ptb.epics.eve.editor.gef.commands;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class CreateScanModule extends Command {
	
	private static Logger logger = Logger.getLogger(CreateScanModule.class
			.getName());
	
	private Chain chain;
	private Rectangle bounds;
	private ScanModule scanModule;
	
	/**
	 * Constructor.
	 * 
	 * @param chain the parent
	 * @param bounds the bounds
	 */
	public CreateScanModule(Chain chain, Rectangle bounds) {
		this.chain = chain;
		this.bounds = bounds;
		int id = this.chain.getAvailableScanModuleId();
		this.scanModule = new ScanModule(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		logger.debug("execute");
		this.scanModule.setX(this.bounds.x);
		this.scanModule.setY(this.bounds.y);
		this.scanModule.setName("SM "
				+ Integer.toString(this.scanModule.getId()));
		this.chain.add(this.scanModule);
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
	
	/**
	 * Returns the scan module.
	 * 
	 * @return the scan module
	 */
	public ScanModule getScanModule() {
		return this.scanModule;
	}
}