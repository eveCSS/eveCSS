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
	public T getContent();
	
	/**
	 * Marks the vertex as discovered. (Used during traversal)
	 * @param time discovery time
	 */
	public void discovered(int time);
	
	/**
	 * Returns the time the vertex was discovered. (Used during traversal)
	 * @return the time the vertex was discovered
	 */
	public int getDiscoveryTime();
	
	/**
	 * Marks the vertex as finished. (Used during traversal)
	 * @param time finish time
	 */
	public void finished(int time);
	
	/**
	 * Returns the time the vertex was finished. (Used during traversal)
	 * @return the time the vertex was finished
	 */
	public int getFinishTime();
	
	/**
	 * Sets the (traversal) state.
	 * @param state the (traversal) state to set
	 */
	public void setState(VisitState state);
	
	/**
	 * Returns the visitation state of the vertex. (Used during traversal)
	 * @return the visitation state of the vertex
	 */
	public VisitState getState();
	
	/**
	 * Returns the predecessor. (Used during traversal)
	 * @return the predecessor
	 */
	public Vertex<T> getPredecessor();
	
	/**
	 * Set the predecessor. (Used during traversal)
	 * @param v the predecessor to set
	 */
	public void setPredecessor(Vertex<T> v);
}