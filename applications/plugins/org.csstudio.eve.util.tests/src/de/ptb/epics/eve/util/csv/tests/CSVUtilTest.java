package de.ptb.epics.eve.util.csv.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javafx.util.Pair;

import org.junit.Test;

import de.ptb.epics.eve.util.csv.CSVUtil;

/**
 * @author Marcus Michalsky
 * @since 1.20
 */
public class CSVUtilTest {
	
	@Test
	public void testGetColumns() {
		assertNotNull(new File(CSVUtilTest.class.getResource("test1.csv").getFile()));
		
		List<Pair<String, List<String>>> csvdata = 
				CSVUtil.getColumns(new File(
						CSVUtilTest.class.getResource("test1.csv").getFile()));
		
		for (Pair<String, List<String>> pair : csvdata) {
			if (pair.getKey().equals("col1")) {
				List<String> testList = 
						Arrays.asList("1", "0", "5", "10", "22");
				assertTrue(pair.getValue().equals(testList));
			} else if (pair.getKey().equals("col2")) {
				List<String> testList = 
						Arrays.asList("2", "1", "6", "14", "22");
				assertTrue(pair.getValue().equals(testList));
			} else if (pair.getKey().equals("col3")) {
				List<String> testList = 
						Arrays.asList("3", "2", "7", "331", "11");
				assertTrue(pair.getValue().equals(testList));
			} else if (pair.getKey().equals("col4")) {
				List<String> testList = 
						Arrays.asList("4", "3", "8", "21", "2");
				assertTrue(pair.getValue().equals(testList));
			} else if (pair.getKey().equals("col5")) {
				List<String> testList = 
						Arrays.asList("3.210", "0.184", "5.0", "4.873", "1.784");
				assertTrue(pair.getValue().equals(testList));
			} else {
				fail("column not found");
			}
		}
	}
}
