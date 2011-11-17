package de.ptb.epics.eve.editor.views.motoraxisview.tests;

import static de.ptb.epics.eve.editor.tests.internal.Helper.resetWorkbench;

import org.apache.log4j.Logger;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MotorAxisViewTest {

	private static Logger logger = 
			Logger.getLogger(MotorAxisViewTest.class.getName());
	
	private static SWTWorkbenchBot bot;
	
	
	
	/* ********************************************************************* */
	
	/**
	 * Class-Wide setup method. Tries to start the Eve Editor plugin.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		bot.viewByTitle("Welcome").close();
		bot.menu("CSS").menu("Editors").menu("EveEditor").click();
		bot.sleep(1000);
		
		// bot.viewById("de.ptb.epics.eve.editor.views.MotorAxisView")
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
