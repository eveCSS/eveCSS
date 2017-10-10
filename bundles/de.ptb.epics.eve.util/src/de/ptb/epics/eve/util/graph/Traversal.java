package de.ptb.epics.eve.util.graph;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class Traversal {
	private static int time;
	
	public static <T> void DepthFirstSearch(Graph<T> graph) {
		for (Vertex<T> v: graph.getVertices()) {
			v.setState(VisitState.UNEXPLORED);
			v.setPredecessor(null);
		}
		time = 0;
		for (Vertex<T> v : graph.getVertices()) {
			if (v.getState().equals(VisitState.UNEXPLORED)) {
				Traversal.DFSVisit(v, graph);
			}
		}
	}
	
	private static <T> void DFSVisit(Vertex<T> v, Graph<T> graph) {
		v.discovered(++time);
		for (Vertex<T> succ : graph.successors(v)) {
			if (succ.getState().equals(VisitState.UNEXPLORED)) {
				succ.setPredecessor(v);
				Traversal.DFSVisit(succ, graph);
			}
		}
		v.finished(++time);
	}
	
	private Traversal() {
	}
}