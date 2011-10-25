package de.ptb.epics.eve.data.tests.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <code>Pair</code> represents a tuple containing a Value A paired with a 
 * List of B types. A Pair is (A, List<B>)
 * 
 * @author Marcus Michalsky
 * @since 1.1
 *
 * @param <A> the type of the first value
 * @param <B> the type of the list elements
 */
public class Pair<A,B> {
	
	private A firstValue;
	private List<B> secondValue;
	
	/**
	 * Constructs a Pair (firstVal, List<secondVal>).
	 * 
	 * @precondition secondVal must be a {@link java.util.List}
	 * @param firstVal the first value of the tuple
	 * @param secondVal the second value of the tuple
	 */
	public Pair(A firstVal, B secondVal) {
		this.firstValue = firstVal;
		this.secondValue = new ArrayList<B>((Collection<? extends B>) secondVal);
	}
	
	/**
	 * Returns the first value of the tuple.
	 * 
	 * @return the first value of the tuple
	 */
	public A getFirstValue() {
		return this.firstValue;
	}
	
	/**
	 * Returns the second value (List) of the tuple.
	 * 
	 * @return the second value of the tuple
	 */
	public List<B> getSecondValue() {
		return this.secondValue;
	}
}