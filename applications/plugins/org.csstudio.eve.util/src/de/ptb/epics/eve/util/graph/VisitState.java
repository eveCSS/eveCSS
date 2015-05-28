package de.ptb.epics.eve.util.graph;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public enum VisitState {
	
	/**
	 * Marker for unexplored state (white).
	 */
	UNEXPLORED,
	
	/**
	 * Marker for discovered state (gray).
	 */
	DISCOVERED,
	
	/**
	 * Marker for finished state (black).
	 */
	FINISHED
}