package de.ptb.epics.eve.editor.gef.commands;

import org.apache.log4j.Logger;
import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class CreateSMConnection extends Command {
	
	private static Logger logger = Logger.getLogger(CreateSMConnection.class
			.getName());
	
	private ScanModule source;
	private ScanModule target;
	private Connector conn;
	
	/**
	 * Constructor.
	 * 
	 * @param scanModule the scanModule
	 */
	public CreateSMConnection(ScanModule source, ScanModule target) {
		this.source = source;
		this.target = target;
		this.conn = new Connector();
		conn.setParentScanModul(source);
		conn.setChildScanModul(target);
	}
	
	/**
	 * Sets the target scan module.
	 * 
	 * @param target the target scan module to be set
	 */
	public void setTargetModule(ScanModule target) {
		this.target = target;
		this.conn.setChildScanModul(target);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		logger.debug("execute");
		this.source.setAppended(this.conn);
		this.target.setParent(this.conn);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		this.source.setAppended(null);
		this.target.setParent(null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		this.source.setAppended(this.conn);
		this.target.setParent(this.conn);
	}
}