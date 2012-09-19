package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.StartEvent;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class DeleteScanModule extends Command {
	/*
	 * A scan module is connected to another (or a start event) via a Connector.
	 * There are four pointers involved:
	 *  - scan module to connector
	 *  - connector to scan module
	 *  - connector to connected scan module
	 *  - connected scan module to connector
	 *  ScanModule (StartEvent) <- -> Connector <- -> ScanModule
	 *  
	 *  Since a ScanModule has a parent appended and nested anchor point there 
	 *  are at most twelve pointers which have to be changed.
	 */
	
	private Chain chain;
	private ScanModule scanModule;
	
	private Connector parent;
	private Connector appended;
	private Connector nested;
	
	private StartEvent parentEvent;
	private ScanModule parentScanModule;
	private ScanModule appendedScanModule;
	private ScanModule nestesScanModule;
	
	private String connection;
	
	/**
	 * Constructor.
	 * 
	 * @param chain the chain the scan module belongs to
	 * @param scanModule the scan module that should be deleted
	 */
	public DeleteScanModule(Chain chain, ScanModule scanModule) {
		this.chain = chain;
		this.scanModule = scanModule;
		
		this.parent = this.scanModule.getParent();
		if (this.parent != null) {
			this.parentEvent = this.parent.getParentEvent();
			this.parentScanModule = this.parent.getParentScanModule();
		} else {
			this.parentScanModule = null;
		}
		this.appended = this.scanModule.getAppended();
		if (this.appended != null) {
			this.appendedScanModule = this.appended.getChildScanModule();
		} else {
			this.appendedScanModule = null;
		}
		this.nested = this.scanModule.getNested();
		if (this.nested != null) {
			this.nestesScanModule = this.nested.getChildScanModule();
		} else {
			this.nestesScanModule = null;
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
		if (this.parent != null) {
			if (this.parentEvent != null) {
				// parent is either a start event...
				this.parentEvent.setConnector(this.parent);
				this.parent.setParentEvent(this.parentEvent);
				this.parent.setChildScanModule(this.scanModule);
				this.scanModule.setParent(this.parent);
			} else if (this.parentScanModule != null) {
				// ... or a scan module
				if (this.connection.equals(Connector.APPENDED)) {
					this.parentScanModule.setAppended(this.parent);
					this.parent.setParentScanModule(this.parentScanModule);
					this.parent.setChildScanModule(this.scanModule);
					this.scanModule.setParent(this.parent);
				} else if (this.connection.equals(Connector.NESTED)) {
					this.parentScanModule.setNested(this.parent);
					this.parent.setParentScanModule(this.parentScanModule);
					this.parent.setChildScanModule(this.scanModule);
					this.scanModule.setParent(this.parent);
				}
			}
		}
		if (this.appended != null) {
			this.appendedScanModule.setParent(this.appended);
			this.appended.setChildScanModule(this.appendedScanModule);
			this.scanModule.setAppended(this.appended);
			this.appended.setParentScanModule(this.scanModule);
		}
		if (this.nested != null) {
			this.nestesScanModule.setParent(this.nested);
			this.nested.setChildScanModule(this.nestesScanModule);
			this.scanModule.setNested(this.nested);
			this.nested.setParentScanModule(this.scanModule);
		}
		this.chain.add(this.scanModule);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		// check for parent
		if (this.parent != null) {
			// parent is either a start event or a scan module
			if (this.parentEvent != null) {
				this.parentEvent.setConnector(null);
			} else if (this.parentScanModule != null) {
				if (this.parentScanModule.getAppended() == parent) {
					// scan module is appended to parent
					this.parentScanModule.setAppended(null);
					this.connection = Connector.APPENDED;
				} else {
					// scan module is nested to parent
					this.parentScanModule.setNested(null);
					this.connection = Connector.NESTED;
				}
				this.parent.setParentScanModule(null);
				this.parent.setParentEvent(null);
				this.parent.setChildScanModule(null);
			}
		}
		// check for appended scan module
		if (this.appended != null) {
			this.appendedScanModule.setParent(null);
			this.appended.setChildScanModule(null);
			this.appended.setParentScanModule(null);
		}
		// check for nested scan module
		if (this.nested != null) {
			this.nestesScanModule.setParent(null);
			this.nested.setChildScanModule(null);
			this.nested.setParentScanModule(null);
		}
		this.scanModule.setParent(null);
		this.scanModule.setAppended(null);
		this.scanModule.setNested(null);
		this.chain.remove(this.scanModule);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		return true;
	}
}