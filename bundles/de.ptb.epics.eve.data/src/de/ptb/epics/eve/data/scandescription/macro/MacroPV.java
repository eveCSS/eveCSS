package de.ptb.epics.eve.data.scandescription.macro;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.util.pv.PVWrapper;


/**
 * A Macro containing a Process Variable.
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class MacroPV extends Macro implements PropertyChangeListener {
	private static final Logger LOGGER = Logger.getLogger(MacroPV.class
			.getName());
	
	private static final String LONG_STRING_SUFFIX = " {\"longString\":true}";
	
	private String value;
	private PVWrapper pv;
	private boolean resolved;
	
	/**
	 * Constructor.
	 * 
	 * @param pvmacro the pv macro (has to be <code>${PV:<pvname>}</code>)
	 * @throws IllegalArgumentException if argument does not match pattern <code>${PV:<pvname>}</code>
	 */
	public MacroPV(String pvmacro) {
		if (!(pvmacro.startsWith("${PV:") && pvmacro.endsWith("}"))) {
			throw new IllegalArgumentException(
					"macro does not match pattern ${PV:<pvname>}");
		}
		this.setName(pvmacro);
		this.setDescription("PV macro: " + pvmacro);
		this.value = pvmacro;
		this.resolved = false;
		this.connect();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolve() {
		return this.value;
	}

	/**
	 * Returns whether the macro is resolved (i.e., pv is read).
	 * 
	 * @return whether the macro is resolved
	 */
	public boolean isResolved() {
		return this.resolved;
	}
	
	private void connect() {
		String pvName = this.getName().substring(5,
				this.getName().length() - 1);
		this.pv = new PVWrapper(pvName + MacroPV.LONG_STRING_SUFFIX);
		this.pv.addPropertyChangeListener(PVWrapper.VALUE, this);
	}
	
	public synchronized void disconnect() {
		this.pv.removePropertyChangeListener(PVWrapper.VALUE, this);
		this.pv.disconnect();
		if (!this.resolved) {
			value = "---";
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PVWrapper.VALUE)) {
			this.value = evt.getNewValue().toString();
			this.resolved = true;
			this.disconnect();
		}
		LOGGER.info("Value received. PV macro is resolved.");
	}
}