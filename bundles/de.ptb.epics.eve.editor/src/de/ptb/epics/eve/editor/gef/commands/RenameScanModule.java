package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class RenameScanModule extends Command {
	
	private String oldName;
	private String newName;
	private ScanModule scanModule;
	
	/**
	 * Constructor.
	 * 
	 * @param newName the name to set
	 * @param scanModule the scan module which name should be set
	 */
	public RenameScanModule(String newName, ScanModule scanModule) {
		super();
		this.newName = newName;
		this.scanModule = scanModule;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		this.oldName = this.scanModule.getName();
		this.scanModule.setName(newName);
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
	public void undo() {
		this.scanModule.setName(oldName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		this.scanModule.setName(newName);
	}
}