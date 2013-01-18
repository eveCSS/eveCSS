package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.StartEvent;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class CreateSEConnection extends Command {
	private StartEvent source;
	private ScanModule target;
	private Connector conn;
	
	/**
	 * Constructor.
	 * 
	 * @param source the start event
	 * @param target the scan module
	 */
	public CreateSEConnection(StartEvent source, ScanModule target) {
		this.source = source;
		this.target = target;
		this.conn = new Connector();
		this.conn.setParentEvent(source);
		this.conn.setChildScanModule(target);
	}
	
	/**
	 * Sets the target scan module.
	 * 
	 * @param target the target scan module to be set
	 */
	public void setTargetModule(ScanModule target) {
		this.target = target;
		this.conn.setChildScanModule(target);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		this.redo();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		this.source.setConnector(null);
		this.target.setParent(null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		this.source.setConnector(conn);
		this.target.setParent(conn);
	}
}