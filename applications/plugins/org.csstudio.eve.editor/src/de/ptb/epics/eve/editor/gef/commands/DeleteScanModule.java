package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class DeleteScanModule extends Command {
	
	private Chain chain;
	private ScanModule scanModule;
	
	
	
	private Connector parent;
	private Connector appended;
	private Connector nested;
	
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
		this.appended = this.scanModule.getAppended();
		this.nested = this.scanModule.getNested();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		if (this.parent != null) {
			if (this.parent.getParentScanModule().getAppended() == parent) {
				this.parent.getParentScanModule().setAppended(null);
				// TODO parent could also be an event !
			} else {
				this.parent.getParentScanModule().setNested(null);
			}
			this.parent.setChildScanModul(null);
		}
		if (this.appended != null) {
			this.appended.getChildScanModule().setParent(null);
			this.appended.setParentEvent(null);
		}
		if (this.nested != null) {
			this.nested.getChildScanModule().setParent(null);
			this.nested.setParentEvent(null);
		}
		this.chain.remove(this.scanModule);
		super.execute();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canUndo() {
		return false;
	}
}