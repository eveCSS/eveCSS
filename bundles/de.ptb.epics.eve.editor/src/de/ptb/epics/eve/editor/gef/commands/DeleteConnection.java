package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.StartEvent;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class DeleteConnection extends Command {

	private Connector connector;
	private String type;
	private StartEvent event;
	private ScanModule source;
	private ScanModule target;
	
	/**
	 * Constructor.
	 * 
	 * @param connector the connector to delete
	 */
	public DeleteConnection(Connector connector) {
		this.connector = connector;
		this.type = "";
		this.event = null;
		this.source = null;
		this.target = this.connector.getChildScanModule();
		if (this.connector.getParentEvent() != null) {
			// connection is StartEvent->ScanModule
			this.event = this.connector.getParentEvent();
		} else if (this.connector.getParentScanModule() != null) {
			// connection is ScanModule->ScanModule
			this.source = this.connector.getParentScanModule();
			if (this.source.getAppended() != null && this.source.getAppended().
					getChildScanModule() == target) {
				this.type = Connector.APPENDED;
			} else {
				this.type = Connector.NESTED;
			}
		}
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
		if (this.event != null) {
			this.event.setConnector(this.connector);
			this.connector.setParentEvent(this.event);
		} else if (this.source != null) {
			if (type.equals(Connector.APPENDED)) {
				this.source.setAppended(this.connector);
			} else {
				this.source.setNested(this.connector);
			}
			this.connector.setParentScanModule(this.source);
		}
		if (this.target != null) {
			this.connector.setChildScanModule(this.target);
			this.target.setParent(this.connector);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		this.connector.setChildScanModule(null);
		this.connector.setParentEvent(null);
		this.connector.setParentScanModule(null);
		
		if (event != null) {
			this.event.setConnector(null);
		} else if (this.source != null) {
			if (type.equals(Connector.APPENDED)) {
				this.source.setAppended(null);
			} else {
				this.source.setNested(null);
			}
		}
		this.target.setParent(null);
	}
}