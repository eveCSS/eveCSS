package de.ptb.epics.eve.util.graph.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.util.graph.tree.Node;

/**
 * @author Marcus Michalsky
 * @since 1.26
 */
public class NodeTest {
	private static Node<Integer> binTree;
	
	@Test
	public void testPreOrder() {
		List<Integer> result = new ArrayList<Integer>();
		binTree.preOrder(result);
		List<Integer> preOrderList = Arrays.asList(1, 3, 4, 2, 5, 6);
		assertEquals(preOrderList, result);
	}
	
	@BeforeClass
	public static void beforeClass() {
		binTree = new Node<Integer>(
				1, 
				new Node<Integer>(3, null, new Node<Integer>(4, null, null)), 
				new Node<Integer>(2, new Node<Integer>(5, null, 
						new Node<Integer>(6, null, null)), null));
	}
}