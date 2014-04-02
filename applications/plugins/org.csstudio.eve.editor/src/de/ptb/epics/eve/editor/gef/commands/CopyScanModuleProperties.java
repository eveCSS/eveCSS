package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.Storage;

/**
 * Copies general properties of a scan module to another.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class CopyScanModuleProperties extends Command {
	private ScanModule from;
	private ScanModule to;
	
	// old values
	private ScanModuleTypes type;
	private Storage storage;
	private String name;
	private int valueCount;
	private double settleTime;
	private double triggerDelay;
	private boolean triggerConfirmAxis;
	private boolean triggerConfirmChannel;
	
	/**
	 * Constructor.
	 * 
	 * @param from scan module properties should be copied from
	 * @param to scan module properties should be copied to
	 */
	public CopyScanModuleProperties(ScanModule from, ScanModule to) {
		this.from = from;
		this.to = to;
		
		this.type = to.getType();
		this.storage = to.getStorage();
		this.name = to.getName();
		this.valueCount = to.getValueCount();
		this.settleTime = to.getSettleTime();
		this.triggerDelay = to.getTriggerDelay();
		this.triggerConfirmAxis = to.isTriggerConfirmAxis();
		this.triggerConfirmChannel = to.isTriggerConfirmChannel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		this.to.setType(this.from.getType());
		this.to.setStorage(this.from.getStorage());
		this.to.setName(this.from.getName() + " copy");
		this.to.setValueCount(this.from.getValueCount());
		this.to.setSettleTime(this.from.getSettleTime());
		this.to.setTriggerDelay(this.from.getTriggerDelay());
		this.to.setTriggerConfirmAxis(this.from.isTriggerConfirmAxis());
		this.to.setTriggerConfirmChannel(this.from.isTriggerConfirmChannel());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		this.to.setType(this.type);
		this.to.setStorage(this.storage);
		this.to.setName(this.name);
		this.to.setValueCount(this.valueCount);
		this.to.setSettleTime(this.settleTime);
		this.to.setTriggerDelay(this.triggerDelay);
		this.to.setTriggerConfirmAxis(this.triggerConfirmAxis);
		this.to.setTriggerConfirmChannel(this.triggerConfirmChannel);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		this.execute();
	}
}