package de.ptb.epics.eve.util.csv;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import javafx.util.Pair;

/**
 * @author Marcus Michalsky
 * @since 1.20
 */
public class CSVUtil {
	private final static Logger LOGGER = Logger.getLogger(CSVUtil.class
			.getName());
	
	/**
	 * Returns a List of Pairs. Each pair contains the column name and a list 
	 * of values.
	 * 
	 * @param csvFile the csv file to import
	 * @return a List of Pairs. Each pair contains the column name and a list 
	 * of values
	 */
	public static List<Pair<String, List<String>>> getColumns(File csvFile) {
		try {
			CSVParser parser = CSVParser.parse(csvFile, Charset.defaultCharset(),
					CSVFormat.DEFAULT.withHeader());
			List<Pair<String, List<String>>> csvData = new LinkedList<>();
			for (String s : parser.getHeaderMap().keySet()) {
				Pair<String, List<String>> pair = new Pair<String, List<String>>(
						s, new LinkedList<String>());
				csvData.add(pair);
			}
			for (CSVRecord record : parser.getRecords()) {
				for (Pair<String, List<String>> pair : csvData) {
					pair.getValue().add(record.get(pair.getKey()));
				}
			}
			return csvData;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
}