package de.ptb.epics.eve.util.graph;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public interface Vertex<T> extends Comparable<Vertex<T>> {
	/**
	 * Returns the (domain specific) content of the vertex
	 * @return the (domain specific) content of the vertex
	 */
	T getContent();
	
	/**
	 * Marks the vertex as discovered. (Used during traversal)
	 * @param time discovery time
	 */
	void discovered(int time);
	
	/**
	 * Returns the time the vertex was discovered. (Used during traversal)
	 * @return the time the vertex was discovered
	 */
	int getDiscoveryTime();
	
	/**
	 * Marks the vertex as finished. (Used during traversal)
	 * @param time finish time
	 */
	void finished(int time);
	
	/**
	 * Returns the time the vertex was finished. (Used during traversal)
	 * @return the time the vertex was finished
	 */
	int getFinishTime();
	
	/**
	 * Sets the (traversal) state.
	 * @param state the (traversal) state to set
	 */
	void setState(VisitState state);
	
	/**
	 * Returns the visitation state of the vertex. (Used during traversal)
	 * @return the visitation state of the vertex
	 */
	VisitState getState();
	
	/**
	 * Returns the predecessor. (Used during traversal)
	 * @return the predecessor
	 */
	Vertex<T> getPredecessor();
	
	/**
	 * Set the predecessor. (Used during traversal)
	 * @param v the predecessor to set
	 */
	void setPredecessor(Vertex<T> v);
}