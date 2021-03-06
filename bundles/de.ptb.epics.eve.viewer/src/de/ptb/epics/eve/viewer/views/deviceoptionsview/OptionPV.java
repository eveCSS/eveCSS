package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.preferences.Activator;
import de.ptb.epics.eve.preferences.PreferenceConstants;
import de.ptb.epics.eve.util.pv.PVWrapper;

/**
 * <code>OptionPV</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class OptionPV extends PVWrapper {

	private final Option option;

	/**
	 * Constructs an <code>OptionPV</code>.
	 * 
	 * @param option the option the PV is related to
	 */
	public OptionPV(final Option option) {
		super(option.getValue().getAccess().getVariableID(), 
				Activator.getDefault().getPreferenceStore().
				getInt(PreferenceConstants.P_PV_UPDATE_INTERVAL));
		this.option = option;
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.data.measuringstation.Option} wrapped 
	 * in the <code>OptionPV</code>.
	 * 
	 * @return the wrapped {@link de.ptb.epics.eve.data.measuringstation.Option}
	 */
	public Option getOption() {
		return this.option;
	}
}