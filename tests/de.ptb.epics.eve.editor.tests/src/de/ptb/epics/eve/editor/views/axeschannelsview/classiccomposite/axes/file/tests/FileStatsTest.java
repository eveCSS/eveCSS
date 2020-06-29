package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.FileStats;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class FileStatsTest {
	private static final Double DOUBLE_DELTA = 0.0001d;
	
	@Test
	public void testFileStats() {
		List<Double> doubles = Arrays.asList(1.0, 2.0, 5.0, 3.0, 0.0, -4.0, 4.0);
		FileStats fileStats = new FileStats(doubles);
		assertEquals(doubles.size(), fileStats.getValueCount());
		assertEquals(1.0, fileStats.getFirstValue(), DOUBLE_DELTA);
		assertEquals(4.0, fileStats.getLastValue(), DOUBLE_DELTA);
		assertEquals(-4.0, fileStats.getMinimum(), DOUBLE_DELTA);
		assertEquals(5.0, fileStats.getMaximum(), DOUBLE_DELTA);
	}
}
