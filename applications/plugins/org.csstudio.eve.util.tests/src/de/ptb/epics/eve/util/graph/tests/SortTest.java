package de.ptb.epics.eve.util.graph.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.util.graph.Graph;
import de.ptb.epics.eve.util.graph.GraphALImpl;
import de.ptb.epics.eve.util.graph.Sort;
import de.ptb.epics.eve.util.graph.Vertex;
import de.ptb.epics.eve.util.graph.VertexImpl;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class SortTest {
	private Graph<String> graph;
	
	/**
	 * thanks to "Introduction to Algorithms, Cormen et. al"
	 */
	@Test
	public void testTopologicalSort() {
		Vertex<String> undershorts = new VertexImpl<String>("undershorts");
		this.graph.addVertex(undershorts);
		Vertex<String> pants = new VertexImpl<String>("pants");
		this.graph.addVertex(pants);
		Vertex<String> belt = new VertexImpl<String>("belt");
		this.graph.addVertex(belt);
		Vertex<String> shirt = new VertexImpl<String>("shirt");
		this.graph.addVertex(shirt);
		Vertex<String> tie = new VertexImpl<String>("tie");
		this.graph.addVertex(tie);
		Vertex<String> jacket = new VertexImpl<String>("jacket");
		this.graph.addVertex(jacket);
		Vertex<String> socks = new VertexImpl<String>("socks");
		this.graph.addVertex(socks);
		Vertex<String> shoes = new VertexImpl<String>("shoes");
		this.graph.addVertex(shoes);
		Vertex<String> watch = new VertexImpl<String>("watch");
		this.graph.addVertex(watch);
		this.graph.addEdge(undershorts, pants);
		this.graph.addEdge(pants, belt);
		this.graph.addEdge(undershorts, shoes);
		this.graph.addEdge(pants, shoes);
		this.graph.addEdge(shirt, belt);
		this.graph.addEdge(shirt, tie);
		this.graph.addEdge(tie, jacket);
		this.graph.addEdge(belt, jacket);
		this.graph.addEdge(socks, shoes);
		
		List<Vertex<String>> sortedList = Sort.topologicalSort(graph);
		for (int i = 0; i <= sortedList.size() - 2; i++) {
			assertTrue(sortedList.get(i).getFinishTime() > sortedList
					.get(i + 1).getFinishTime());
		}
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