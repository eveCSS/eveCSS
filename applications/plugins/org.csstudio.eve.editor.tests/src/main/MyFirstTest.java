package main;

import static de.ptb.epics.eve.editor.tests.internal.Helper.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MyFirstTest {

	@Test
	public void thisPasses() throws Exception {
		assertEquals(2, 2);
	}

	@Test
	public void thisFails() throws Exception {
		assertTrue(false);
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
