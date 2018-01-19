package de.ptb.epics.eve.viewer.propertytester;

import org.eclipse.core.expressions.PropertyTester;

import de.ptb.epics.eve.viewer.Activator;

/**
 * Property Tester checking whether the engine is connected.
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public class EngineConnected extends PropertyTester {
	public static final String PROPERTY_NAMESPACE = "viewer.playlist";
	public static final String PROPERTY_ENGINE_CONNECTED = "engineConnected";
	
	/**
	 * 
	 */
	public EngineConnected() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		return Activator.getDefault().getEcp1Client().isRunning();
	}
}
