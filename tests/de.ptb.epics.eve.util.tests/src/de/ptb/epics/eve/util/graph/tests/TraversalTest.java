package de.ptb.epics.eve.util.graph.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.util.graph.*;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class TraversalTest {
	private Graph<String> graph;
	
	@Test
	public void testDepthFirstSearch() {
		Vertex<String> aVertex = new VertexImpl<String>("A");
		this.graph.addVertex(aVertex);
		Vertex<String> bVertex = new VertexImpl<String>("B");
		this.graph.addVertex(bVertex);
		Vertex<String> cVertex = new VertexImpl<String>("C");
		this.graph.addVertex(cVertex);
		Vertex<String> dVertex = new VertexImpl<String>("D");
		this.graph.addVertex(dVertex);
		this.graph.addEdge(aVertex, bVertex);
		this.graph.addEdge(aVertex, cVertex);
		this.graph.addEdge(bVertex, dVertex);
		this.graph.addEdge(cVertex, dVertex);
		Traversal.DepthFirstSearch(graph);
		for (Vertex<String> vertex : this.graph.getVertices()) {
			assertEquals(vertex.getState(), VisitState.FINISHED);
		}
		// TODO ...
	}
	
	@BeforeClass
	public static void beforeClass() {
		
	}
	
	@Before
	public void before() {
		this.graph = new GraphALImpl<String>();
	}
	
	@After
	public void after() {
		
	}
	
	@AfterClass
	public static void afterClass() {
		
	}
}