package de.ptb.epics.eve.editor.gef.commands;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.preferences.PreferenceConstants;

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
	 * @param bounds the bounds (can be <code>null</code>)
	 * @param type the scan module type
	 */
	public CreateScanModule(Chain chain, Rectangle bounds, ScanModuleTypes type) {
		this.chain = chain;
		this.bounds = bounds;
		int id = this.chain.getAvailableScanModuleId();
		this.scanModule = new ScanModule(id);
		this.scanModule.setType(type);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		logger.debug("execute");
		if (this.bounds != null) {
			this.scanModule.setX(this.bounds.x);
			this.scanModule.setY(this.bounds.y);
		}
		this.chain.add(this.scanModule);
		String name = "";
		IMeasuringStation measuringStation = Activator.getDefault().getDeviceDefinition();
		switch (this.scanModule.getType()) {
		case CLASSIC:
			name = ScanModule.DEFAULT_NAME_CLASSIC + " " +
					Integer.toString(this.scanModule.getId());
			break;
		case SAVE_AXIS_POSITIONS:
			name = ScanModule.DEFAULT_NAME_SAVE_AXIS_POSITIONS;
			this.scanModule.saveAllAxisPositions(measuringStation);
			break;
		case SAVE_CHANNEL_VALUES:
			name = ScanModule.DEFAULT_NAME_SAVE_CHANNEL_VALUES;
			this.scanModule.saveAllChannelValues(measuringStation);
			break;
		case TOP_UP:
			name = "TOP UP";
			this.scanModule.setType(ScanModuleTypes.CLASSIC);
			this.scanModule.topUp(measuringStation,
					Activator.getDefault().getPreferenceStore()
							.getString(PreferenceConstants.P_TOPUP_PV));
			break;
		case DYNAMIC_AXIS_POSITIONS:
			name = ScanModule.DEFAULT_NAME_DYNAMIC_AXIS_POSITIONS;
			break;
		case DYNAMIC_CHANNEL_VALUES:
			name = ScanModule.DEFAULT_NAME_DYNAMIC_CHANNEL_VALUES;
			break;
		default:
			break;
		}
		this.scanModule.setName(name);
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