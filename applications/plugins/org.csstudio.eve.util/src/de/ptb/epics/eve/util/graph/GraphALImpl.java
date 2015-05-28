package de.ptb.epics.eve.util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class GraphALImpl<T> implements Graph<T> {
	private final Map<Vertex<T>,List<Vertex<T>>> successors;
	
	/**
	 * Constructs a new Graph Adjacency List Implementation.
	 */
	public GraphALImpl() {
		this.successors = new HashMap<Vertex<T>, List<Vertex<T>>>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addVertex(Vertex<T> vertex) {
		List<Vertex<T>> list = new ArrayList<Vertex<T>>();
		successors.put(vertex, list);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Vertex<T>> getVertices() {
		return new ArrayList<Vertex<T>>(this.successors.keySet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addEdge(Vertex<T> from, Vertex<T> to) {
		List<Vertex<T>> list = this.successors.get(from);
		if (list == null || this.successors.get(to) == null) {
			return false;
		}
		return list.add(to);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Vertex<T>> successors(Vertex<T> vertex) {
		return this.successors.get(vertex);
	}
}