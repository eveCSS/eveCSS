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
	private String type;
	
	/**
	 * Constructor.
	 * 
	 * @param source the connection source
	 * @param target the connection target
	 * @param type the connection type (either 
	 * 		{@link de.ptb.epics.eve.data.scandescription.Connector#APPENDED} or
	 *  	{@link de.ptb.epics.eve.data.scandescription.Connector#NESTED}
	 * @throws IllegalArgumentException if <code>type</code> incorrect
	 */
	public CreateSMConnection(ScanModule source, ScanModule target, String type) {
		if (!type.equals(Connector.APPENDED) && !type.equals(Connector.NESTED)) {
			throw new IllegalArgumentException(
				"type must be either 'Connector.APPENDED' or 'Connector.Nested'");
		}
		this.source = source;
		this.target = target;
		this.type = type;
		this.conn = new Connector();
		conn.setParentScanModule(source);
		conn.setChildScanModule(target);
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
		logger.debug("execute");
		if (type.equals(Connector.APPENDED)) {
			this.source.setAppended(this.conn);
		} else if (type.equals(Connector.NESTED)) {
			this.source.setNested(this.conn);
		}
		this.target.setParent(this.conn);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		if (type.equals(Connector.APPENDED)) {
			this.source.setAppended(null);
		} else if (type.equals(Connector.NESTED)) {
			this.source.setNested(null);
		}
		this.target.setParent(null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		if (type.equals(Connector.APPENDED)) {
			this.source.setAppended(this.conn);
		} else if (type.equals(Connector.NESTED)) {
			this.source.setNested(this.conn);
		}
		this.target.setParent(this.conn);
	}
}