package de.ptb.epics.eve.data.scandescription.tests;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.eclipse.swt.graphics.RGB;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.data.scandescription.YAxisModifier;


/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public class YAxisTest implements PropertyChangeListener {

	private boolean channel;
	private boolean normalize;
	private boolean mode;
	private boolean modifier;
	private boolean linestyle;
	private boolean markstyle;
	private boolean color;
	
	@Test
	public void testPropertyChangeSupport() {
		DetectorChannel channel = new DetectorChannel();
		channel.setId("1");
		YAxis yAxis = new YAxis(channel);
		
		yAxis.addPropertyChangeListener(YAxis.CHANNEL_PROP, this);
		yAxis.addPropertyChangeListener(YAxis.COLOR_PROP, this);
		yAxis.addPropertyChangeListener(YAxis.LINESTYLE_PROP, this);
		yAxis.addPropertyChangeListener(YAxis.MARKSTYPE_PROP, this);
		yAxis.addPropertyChangeListener(YAxis.MODE_PROP, this);
		yAxis.addPropertyChangeListener(YAxis.MODIFIER_PROP, this);
		yAxis.addPropertyChangeListener(YAxis.NORMALIZE_PROP, this);
		
		DetectorChannel newChannel = new DetectorChannel();
		channel.setId("2");
		yAxis.setDetectorChannel(newChannel);
		yAxis.setColor(new RGB(255,127,55));
		yAxis.setLinestyle(TraceType.STEP_HORIZONTALLY);
		yAxis.setMarkstyle(PointStyle.FILLED_DIAMOND);
		yAxis.setMode(PlotModes.LOG);
		yAxis.setModifier(YAxisModifier.INVERSE);
		yAxis.setNormalizeChannel(newChannel);
		
		assertTrue("channel property did not fire", this.channel);
		assertTrue("color property did not fire", this.color);
		assertTrue("linestyle property did not fire", this.linestyle);
		assertTrue("markstyle property did not fire", this.markstyle);
		assertTrue("plot mode property did not fire", this.mode);
		assertTrue("modifier property did not fire", this.modifier);
		assertTrue("normalize property did not fire", this.normalize);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case YAxis.CHANNEL_PROP:
			this.channel = true;
			break;
		case YAxis.COLOR_PROP:
			this.color = true;
			break;
		case YAxis.LINESTYLE_PROP:
			this.linestyle = true;
			break;
		case YAxis.MARKSTYPE_PROP:
			this.markstyle = true;
			break;
		case YAxis.MODE_PROP:
			this.mode = true;
			break;
		case YAxis.MODIFIER_PROP:
			this.modifier = true;
			break;
		case YAxis.NORMALIZE_PROP:
			this.normalize = true;
			break;
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void testEquals() {
		YAxis empty1 = new YAxis();
		YAxis empty2 = new YAxis();
		assertEquals(empty1, empty2);
		
		DetectorChannel ch1 = new DetectorChannel();
		ch1.setId("1");
		YAxis axis1 = new YAxis(ch1);
		DetectorChannel ch2 = new DetectorChannel();
		ch2.setId("1");
		YAxis axis2 = new YAxis(ch2);
		assertEquals(axis1, axis2);
		
		axis1.setMode(PlotModes.LOG);
		assertFalse(axis1.equals(axis2));
		
		axis2.setMode(PlotModes.LOG);
		assertEquals(axis1, axis2);
		
		ch2.setId("2");
		assertFalse(axis1.equals(axis2));
	}
	
	/**
	 * Class wide setup method of the test
	 */
	@BeforeClass
	public static void runBeforeClass() {
	}

	/**
	 * test wide set up method
	 */
	@Before
	public void beforeEveryTest() {
	}

	/**
	 * test wide tear down method
	 */
	@After
	public void afterEveryTest() {
	}

	/**
	 * class wide tear down method
	 */
	@AfterClass
	public static void afterClass() {
	}
}
