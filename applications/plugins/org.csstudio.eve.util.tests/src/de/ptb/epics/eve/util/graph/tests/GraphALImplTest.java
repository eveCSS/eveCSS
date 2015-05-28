package de.ptb.epics.eve.util.graph.tests;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.util.graph.Graph;
import de.ptb.epics.eve.util.graph.GraphALImpl;
import de.ptb.epics.eve.util.graph.Vertex;
import de.ptb.epics.eve.util.graph.VertexImpl;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class GraphALImplTest {
	private static Graph<String> graph;
	
	@Test
	public void testAddVertex() {
		Vertex<String> stringVertex = new VertexImpl<String>("Hello World!");
		graph.addVertex(stringVertex);
		assertTrue(graph.getVertices().contains(stringVertex));
	}
	
	@Test
	public void testAddEdge() {
		Vertex<String> fromVertex = new VertexImpl<String>("from");
		Vertex<String> toVertex = new VertexImpl<String>("to");
		graph.addVertex(fromVertex);
		graph.addVertex(toVertex);
		graph.addEdge(fromVertex, toVertex);
		assertTrue(graph.successors(fromVertex).contains(toVertex));
	}
	
	@BeforeClass
	public static void beforeClass() {
		GraphALImplTest.graph = new GraphALImpl<String>();
	}
	
	@Before
	public void before() {
		
	}
	
	@After
	public void after() {
		
	}
	
	@AfterClass
	public static void afterClass() {
		graph = null;
	}
}