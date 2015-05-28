package de.ptb.epics.eve.util.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class Sort {
	
	/**
	 * Returns a topologically sorted list of vertices of the given graph.
	 * @param graph the graph to sort topologically
	 * @return a topologically sorted list of vertices
	 */
	public static <T> List<Vertex<T>> topologicalSort(Graph<T> graph) {
		Traversal.DepthFirstSearch(graph);
		List<Vertex<T>> vertices = new ArrayList<>(graph.getVertices());
		Collections.sort(vertices);
		Collections.reverse(vertices);
		return vertices;
	}
}