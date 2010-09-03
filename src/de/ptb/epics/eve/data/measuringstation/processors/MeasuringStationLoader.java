/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation.processors;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;

/**
 * This class transforms XML data to a measuring station model tree.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld ( -at- ) ptb.de>
 *
 */
public class MeasuringStationLoader {

	/**
	 * The name of the file that should be loaded.
	 * 
	 */
	private File fileToLoad;
	
	/**
	 * The loaded Measuring Station.
	 * 
	 */
	private MeasuringStation measuringStation;
	
	/**
	 * The schema file that is used to load the measuring station description.
	 */
	private final File schemaFile;
	
	/**
	 * The constructor.
	 * 
	 * @param schemaFile The schema file that should be used to load the measuring station.
	 */
	public MeasuringStationLoader( final File schemaFile ) {
		if( schemaFile == null ) {
			throw new IllegalArgumentException( "The parameter 'schemaFile' must not be null!" );
		}
		this.schemaFile = schemaFile;
	}
	
	/**
	 * This method loads a measuring station description from a byte array.
	 * 
	 * @param byteArray The byte array that contains the XML data.
	 * @return The loaded Measuring Station description.
	 * 
	 * @throws ParserConfigurationException Gets thrown if the parser has the wrong configuration.
	 * @throws SAXException Gets thrown by SAX.
	 * @throws IOException Gets thrown if something went wrong with the input stream.
	 */
	public IMeasuringStation loadFromByteArray( final byte[] byteArray ) throws ParserConfigurationException, SAXException, IOException {
		final SchemaFactory sFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI  );
		final Schema schema = sFactory.newSchema( this.schemaFile );

		
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setSchema( schema );
		factory.setValidating( true );
		final SAXParser saxParser = factory.newSAXParser();
		
		final MeasuringStationLoaderHandler handler = new MeasuringStationLoaderHandler(); 
		saxParser.parse( new ByteArrayInputStream( byteArray ), handler );
		
		this.measuringStation = handler.getMeasuringStation();
		this.measuringStation.setLoadedFileName("unknown");
		this.measuringStation.setSchemaFileName( schemaFile.getAbsolutePath() );
		return handler.getMeasuringStation();
	}
	
	/**
	 * Loads the given xml file.
	 * 
	 * @param fileToLoad
	 * @throws ParserConfigurationException Gets thrown if the parser has the wrong configuration.
	 * @throws SAXException Gets thrown by SAX.
	 * @throws IOException Gets thrown if something went wrong with the input stream.
	 */
	public void load( final File fileToLoad ) throws ParserConfigurationException, SAXException, IOException {
		this.fileToLoad = fileToLoad;
		this.load();
	}
	
	/**
	 * Loads the given file.
	 * 
	 * @throws ParserConfigurationException Gets thrown if the parser has the wrong configuration.
	 * @throws SAXException Gets thrown by SAX.
	 * @throws IOException Gets thrown if something went wrong with the input stream.
	 */
	public void load() throws ParserConfigurationException, SAXException, IOException {
		
		this.measuringStation = null;
		
		final SchemaFactory sFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI  );
		final Schema schema = sFactory.newSchema( this.schemaFile );
		
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setSchema( schema );
		factory.setValidating( true );
		final SAXParser saxParser = factory.newSAXParser();
		
		final MeasuringStationLoaderHandler handler = new MeasuringStationLoaderHandler(); 
		saxParser.parse( this.fileToLoad, handler );
		
		this.measuringStation = handler.getMeasuringStation();
		this.measuringStation.setLoadedFileName(fileToLoad.getAbsolutePath());
		this.measuringStation.setSchemaFileName(schemaFile.getAbsolutePath());
		
	}

	/**
	 * Gives back the name of the file that is loaded by this object.
	 * 
	 * @return The filename of the file that is loaded by this object.
	 */
	public File getFileToLoad() {
		return fileToLoad;
	}

	/**
	 * This method sets the filename that is loaded by this object.
	 * 
	 * @param fileToLoad The filename that is loaded by this object.
	 */
	public void setFileToLoad( final File fileToLoad ) {
		this.fileToLoad = fileToLoad;
	}

	/**
	 * Gives back the loaded measuring station.
	 * 
	 * @return The loaded measuring station.
	 */
	public IMeasuringStation getMeasuringStation() {
		return measuringStation;
	}
	
	
	
}
