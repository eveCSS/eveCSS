package de.ptb.epics.eve.editor.tests;

import static de.ptb.epics.eve.editor.tests.internal.Helper.*;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <code>MenuTest</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class MenuTest {
	
	private static Logger logger = Logger.getLogger(MenuTest.class.getName());
	
	private static SWTWorkbenchBot bot;
	
	/**
	 * Tries to create a new scan description via the file menu.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateNewScanDescription() throws Exception {
		bot.menu("File").menu("New").click();
		bot.sleep(1000);
		SWTBotShell shell = bot.shell("New");
		shell.activate();
		bot.tree().select("Scan Description");
		bot.button("Next >").click();
		
		long time = System.currentTimeMillis();
		
		bot.textWithLabel("File name:").setText(
				"/tmp/eveTest" + time + ".scml");
		
		bot.button("Finish").click();
		
		try {
			bot.waitUntil(shellCloses(shell));
		} catch(TimeoutException e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
		
		SWTBotEditor editor = bot.editorByTitle("eveTest" + time + ".scml");
		
		assertEquals(editor.getTitle(), "eveTest" + time + ".scml");
	}
	
	/* ******************************************************************** */
	
	/**
	 * Class-Wide setup method. Tries to start the Eve Editor plugin.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		bot.sleep(1000);
		bot.viewByTitle("Welcome").close();
		bot.menu("CSS").menu("Editors").menu("EveEditor").click();
		bot.sleep(1000);
	}
	
	/**
	 * Test-Wide setup method. Sets an initial (defined) state of the 
	 * application.
	 */
	@Before
	public void beforeEachTest() {
		UIThreadRunnable.syncExec(new VoidResult() {
			@Override public void run() {
				resetWorkbench();
			}
		});
	}
}