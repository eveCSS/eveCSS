package de.ptb.epics.eve.util.graph;

import java.util.List;

/**
 * (Generic) Directed Graph.
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public interface Graph<T> {
	
	/**
	 * Adds a vertex.
	 * @param vertex the vertex to be added
	 */
	void addVertex(Vertex<T> vertex);
	
	/**
	 * Returns all vertices.
	 * @return all vertices
	 */
	List<Vertex<T>> getVertices();
	
	/**
	 * Adds an edge.
	 * @param from start point
	 * @param to end point
	 * @return whether operation was successful
	 */
	boolean addEdge(Vertex<T> from, Vertex<T> to);
	
	/**
	 * Returns all successors of the given vertex.
	 * @param v the predecessor
	 * @return all successors of the given vertex
	 */
	List<Vertex<T>> successors(Vertex<T> v);
}