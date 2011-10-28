package de.ptb.epics.eve.editor.tests.internal;

import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class Helper {

	/**
	 * 
	 */
	public static void resetWorkbench() {
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow workbenchWindow = 
					workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			Shell activeShell = Display.getCurrent().getActiveShell();
			if (activeShell != workbenchWindow.getShell()) {
				activeShell.close();
			}
			workbench.showPerspective("EveEditorPerpective", workbenchWindow);
			page.closeAllEditors(false);
			page.resetPerspective();
		} catch (WorkbenchException e) {
			throw new RuntimeException(e);
		}
	}
}