package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.tests;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.DeviceMother;
import de.ptb.epics.eve.data.tests.mothers.measuringstation.OptionMother;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.PrePostscanEntry;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.PrePostscanHelper;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PrePostscanHelperTest {
	private ScanModule scanModule;
	
	@Test
	public void testDevicePrescan() {
		Prescan prescan = new Prescan(DeviceMother.createNewDevice());
		scanModule.add(prescan);
		List<PrePostscanEntry> entries = PrePostscanHelper.
				createEntries(scanModule);
		boolean found = false;
		for (PrePostscanEntry entry : entries) {
			if (entry.getPrescan().getAbstractPrePostscanDevice().equals(
					prescan.getAbstractPrePostscanDevice())) {
				found = true;
			}
		}
		assertTrue("prescan device not found", found);
	}
	
	@Test
	public void testDevicePostscan() {
		Postscan postscan = new Postscan(DeviceMother.createNewDevice());
		scanModule.add(postscan);
		List<PrePostscanEntry> entries = PrePostscanHelper.
				createEntries(scanModule);
		boolean found = false;
		for (PrePostscanEntry entry : entries) {
			if (entry.getPostscan().getAbstractPrePostscanDevice().equals(
					postscan.getAbstractPrePostscanDevice())) {
				found = true;
			}
		}
		assertTrue("postscan device not found", found);
	}
	
	@Test
	public void testDevicePrePostscan() {
		Device device = DeviceMother.createNewDevice();
		Prescan prescan = new Prescan(device);
		scanModule.add(prescan);
		Postscan postscan = new Postscan(device);
		scanModule.add(postscan);
		List<PrePostscanEntry> entries = PrePostscanHelper.
				createEntries(scanModule);
		boolean found = false;
		for (PrePostscanEntry entry : entries) {
			if (entry.getPrescan().getAbstractPrePostscanDevice().equals(
						prescan.getAbstractPrePostscanDevice())
					&& entry.getPostscan().getAbstractPrePostscanDevice()
							.equals(postscan.getAbstractPrePostscanDevice())) {
				found = true;
			}
		}
		assertTrue("pre-/postscan device not found", found);
	}
	
	@Test
	public void testOptionPrescan() {
		Prescan prescan = new Prescan(OptionMother.createNewOption());
		scanModule.add(prescan);
		List<PrePostscanEntry> entries = PrePostscanHelper.
				createEntries(scanModule);
		boolean found = false;
		for (PrePostscanEntry entry : entries) {
			if (entry.getPrescan().equals(prescan)) {
				found = true;
			}
		}
		assertTrue("prescan option not found", found);
	}
	
	@Test
	public void testOptionPostscan() {
		Postscan postscan = new Postscan(OptionMother.createNewOption());
		scanModule.add(postscan);
		List<PrePostscanEntry> entries = PrePostscanHelper.
				createEntries(scanModule);
		boolean found = false;
		for (PrePostscanEntry entry : entries) {
			if (entry.getPostscan().equals(postscan)) {
				found = true;
			}
		}
		assertTrue("postscan option not found", found);
	}
	
	@Test
	public void testOptionPrePostscan() {
		Option option = OptionMother.createNewOption();
		Prescan prescan = new Prescan(option);
		scanModule.add(prescan);
		Postscan postscan = new Postscan(option);
		scanModule.add(postscan);
		List<PrePostscanEntry> entries = PrePostscanHelper.
				createEntries(scanModule);
		boolean found = false;
		for (PrePostscanEntry entry : entries) {
			if (entry.getPrescan().getAbstractPrePostscanDevice().equals(
						prescan.getAbstractPrePostscanDevice())
					&& entry.getPostscan().getAbstractPrePostscanDevice()
							.equals(postscan.getAbstractPrePostscanDevice())) {
				found = true;
			}
		}
		assertTrue("pre-/postscan option not found", found);
	}
	
	@Before
	public void before() {
		this.scanModule = new ScanModule(1);
	}
}
