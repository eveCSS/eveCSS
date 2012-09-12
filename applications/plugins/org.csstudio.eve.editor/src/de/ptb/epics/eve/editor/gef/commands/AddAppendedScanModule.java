package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.6
 */
public class AddAppendedScanModule extends Command {
	
	private ScanModule scanModule;
	
	/**
	 * Constructor.
	 * 
	 * @param scanModule the scan module the scan module should be appended to
	 */
	public AddAppendedScanModule(ScanModule scanModule) {
		this.scanModule = scanModule;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		
		// TODO Auto-generated method stub
		super.execute();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		return this.scanModule.getAppended().getChildScanModule() != null;
	}
}