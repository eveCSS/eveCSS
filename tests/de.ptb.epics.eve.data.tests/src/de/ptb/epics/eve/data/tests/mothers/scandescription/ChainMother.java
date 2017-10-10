package de.ptb.epics.eve.data.tests.mothers.scandescription;

import de.ptb.epics.eve.data.scandescription.Chain;

/**
 * Fabricates chain test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public class ChainMother {
	private static int counter = 0;
	
	public static Chain createNewChain() {
		return new Chain(++counter);
	}
}