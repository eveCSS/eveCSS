package de.ptb.epics.eve.util.graph.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.util.graph.Vertex;
import de.ptb.epics.eve.util.graph.VertexImpl;
import de.ptb.epics.eve.util.graph.VisitState;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class VertexTest {
	private static final String VERTEX_STRING = "Hellow World!";
	private static final int VERTEX_DISCOVERY_TIME = 5;
	private static final int VERTEX_FINISH_TIME = 7;
	private Vertex<String> vertex;
	
	
	@Test
	public void testGetContent() {
		assertEquals(VertexTest.VERTEX_STRING, this.vertex.getContent());
	}
	
	@Test
	public void testDiscovered() {
		this.vertex.discovered(VertexTest.VERTEX_DISCOVERY_TIME);
		assertEquals(VERTEX_DISCOVERY_TIME, this.vertex.getDiscoveryTime());
	}
	
	@Test
	public void testFinished() {
		this.vertex.finished(7);
		assertEquals(VERTEX_FINISH_TIME, this.vertex.getFinishTime());
	}
	
	@Test
	public void testState() {
		this.vertex.setState(VisitState.UNEXPLORED);
		assertEquals(VisitState.UNEXPLORED, this.vertex.getState());
		this.vertex.setState(VisitState.DISCOVERED);
		assertEquals(VisitState.DISCOVERED, this.vertex.getState());
		this.vertex.setState(VisitState.FINISHED);
		assertEquals(VisitState.FINISHED, this.vertex.getState());
	}
	
	@Test
	public void testPredecessor() {
		assertNull(this.vertex.getPredecessor());
		Vertex<String> predecessor = new VertexImpl<String>("Predecessor");
		this.vertex.setPredecessor(predecessor);
		assertEquals(predecessor, this.vertex.getPredecessor());
	}
	
	@BeforeClass
	public static void beforeClass() {
	}
	
	@Before
	public void beforeTest() {
		this.vertex = new VertexImpl<String>(VertexTest.VERTEX_STRING);
	}
	
	@After
	public void afterTest() {
		this.vertex = null;
	}
	
	@AfterClass
	public static void afterClass() {
	}
}