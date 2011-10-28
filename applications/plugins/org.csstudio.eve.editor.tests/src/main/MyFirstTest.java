package main;

import static de.ptb.epics.eve.editor.tests.internal.Helper.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MyFirstTest {

	private static SWTWorkbenchBot bot;
	
@Test
public void thisPasses() throws Exception {
assertEquals(2, 2);
}

@Ignore("Always Fails")
@Test
public void thisFails() throws Exception {
assertTrue(false);
}

@Test
	public void canCreateANewJavaProject() throws Exception {
		
		// slow down tests
		SWTBotPreferences.PLAYBACK_DELAY = 1000;
	
		bot.menu("File").menu("New").click();
		
		SWTBotShell shell = bot.shell("New");
		shell.activate();
		bot.tree().select("Scan Description");
		bot.button("Next >").click();
		
		long time = System.currentTimeMillis();
		
		bot.textWithLabel("File name:").setText(
				"/tmp/eveTest" + time + ".scml");
		
		bot.button("Finish").click();
		
		SWTBotEditor editor = bot.activeEditor();
		
		assertEquals(editor.getTitle(), "eveTest" + time + ".scml");
		
		// set to the default speed
				SWTBotPreferences.PLAYBACK_DELAY = 0;
	}
	
	/* ******************************************************************** */
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		bot.viewByTitle("Welcome").close();
	}
  
  /* ***************************************************** */

  @Before
  public void beforeEachTest() {
	  UIThreadRunnable.syncExec(new VoidResult() {
          public void run() {
              resetWorkbench();
          }
      });
  }
}
