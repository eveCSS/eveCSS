package de.ptb.epics.eve.editor.wizards;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import org.eclipse.ui.*;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionSaverToXMLusingXerces;
import de.ptb.epics.eve.editor.Activator;

/**
 * <code>NewScandescriptionWizard</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class NewScanDescriptionWizard extends Wizard implements INewWizard {
	
	private static Logger logger = 
			Logger.getLogger(NewScanDescriptionWizard.class.getName());
	
	// the only page in the wizard
	private NewScanDescriptionWizardPage page;

	/**
	 * Constructs a <code>NewScanDescriptionWizard</code>.
	 */
	public NewScanDescriptionWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		page = new NewScanDescriptionWizardPage();
		addPage(page);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		final String fileName = page.getFileName();
	
		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override public void run(IProgressMonitor monitor) 
											throws InvocationTargetException {
				try {
					doFinish(fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/*
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.<br><br>
	 */
	private void doFinish(String fileName, IProgressMonitor monitor) 
														throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		
		final File file = new File(fileName);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			// file already exists
			/*
			if(!MessageDialog.openConfirm(getShell(), 
					"Overwrite existing file ?", 
					"File already exists, overwrite ?")) {
				// user wishes not to overwrite the file
				MessageDialog.openWarning(getShell(), "File not created.", 
						"Could not create a new file.");
				return;
			}*/
			return;
		}
		
		final IMeasuringStation measuringStation =  
				Activator.getDefault().getMeasuringStation();
		final ScanDescription scanDescription = 
				new ScanDescription(measuringStation);
		final Chain chain = new Chain(1);
		final StartEvent startEvent = 
				new StartEvent(scanDescription.getEventById("S0") , chain);
		chain.setStartEvent(startEvent);
		scanDescription.add(chain);
		
		try {
			final FileOutputStream os = new FileOutputStream(file);
			final ScanDescriptionSaverToXMLusingXerces scanDescriptionSaver = 
					new ScanDescriptionSaverToXMLusingXerces(os, 
															 measuringStation, 
															 scanDescription);
			scanDescriptionSaver.save();
		} catch(final FileNotFoundException e1) {
			logger.error(e1.getMessage(), e1);
		}
		
		final IFileStore fileStore = 
				EFS.getLocalFileSystem().getStore(new Path(fileName));
		final FileStoreEditorInput fileStoreEditorInput = 
				new FileStoreEditorInput(fileStore);	
			
		getShell().getDisplay().asyncExec(new Runnable() {
			@Override public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().
												 getActiveWorkbenchWindow().
												 getActivePage();
				try {
					IDE.openEditor(page, fileStoreEditorInput, 
						"de.ptb.epics.eve.editor.graphical.GraphicalEditor");
				} catch(final PartInitException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
		
		monitor.worked(1);
	}
}