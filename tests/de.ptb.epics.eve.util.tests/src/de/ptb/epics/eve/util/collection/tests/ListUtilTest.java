package de.ptb.epics.eve.util.collection.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.ptb.epics.eve.util.collection.ListUtil;

/**
 * @author Marcus Michalsky
 * @since 1.25
 */
public class ListUtilTest {
	
	@Test
	public void testRotate() {
		List<String> list = new ArrayList<>();
		list.add("Zero");
		list.add("One");
		list.add("Two");
		list.add("Three");
		list.add("Four");
		
		assertEquals(list.get(0), "Zero");
		assertEquals(list.get(1), "One");
		assertEquals(list.get(2), "Two");
		assertEquals(list.get(3), "Three");
		assertEquals(list.get(4), "Four");
		
		ListUtil.move(list, 1, 3);
		
		assertEquals(list.get(0), "Zero");
		assertEquals(list.get(1), "Two");
		assertEquals(list.get(2), "Three");
		assertEquals(list.get(3), "One");
		assertEquals(list.get(4), "Four");
	}
}