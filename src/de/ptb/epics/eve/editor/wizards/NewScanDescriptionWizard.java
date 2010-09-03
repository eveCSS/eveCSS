package de.ptb.epics.eve.editor.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import org.eclipse.ui.*;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileStore;

import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionSaverToXMLusingXerces;
import de.ptb.epics.eve.editor.Activator;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "scml". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class NewScanDescriptionWizard extends Wizard implements INewWizard {
	private NewScanDescriptionWizardPage page;
	private ISelection selection;

	/**
	 * Constructor for NewScanDescriptionWizard.
	 */
	public NewScanDescriptionWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new NewScanDescriptionWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		
		final String fileName = page.getFileName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish( fileName, monitor );
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
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish( String fileName, IProgressMonitor monitor ) throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		
		final File file = new File( fileName );
		if( !file.exists() ) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		final IMeasuringStation measuringStation =  Activator.getDefault().getMeasuringStation();
		final ScanDescription scanDescription = new ScanDescription( measuringStation );
		final Chain chain = new Chain( 1 );
		final StartEvent startEvent = new StartEvent( scanDescription.getEventById( "S0" ) , chain );
		chain.setStartEvent( startEvent );
		scanDescription.add( chain );
		
		try {
			final FileOutputStream os = new FileOutputStream( file );
			final ScanDescriptionSaverToXMLusingXerces scanDescriptionSaver = new ScanDescriptionSaverToXMLusingXerces( os, measuringStation, scanDescription );
			scanDescriptionSaver.save();
		} catch( final FileNotFoundException e1 ) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		final IFileStore fileStore = EFS.getLocalFileSystem().getStore( new Path( fileName ) );
		final FileStoreEditorInput fileStoreEditorInput = new FileStoreEditorInput( fileStore );
		
			
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor( page, fileStoreEditorInput, "de.ptb.epics.eve.editor.graphical.GraphicalEditor" );
				} catch( final PartInitException e ) {
				}
			}
		});
		
		// IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		//IResource resource = root.findMember(new Path(containerName));
		//if (!resource.exists() || !(resource instanceof IContainer)) {
		//	throwCoreException("Container \"" + containerName + "\" does not exist.");
		//}
		//IContainer container = (IContainer) resource;
		//final IFile file = container.getFile(new Path(fileName));
		/*try {
			InputStream stream = openContentStream();
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});*/
		monitor.worked(1);
	}
	
	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream() {
		String contents =
			"This is the initial file contents for *.scml file that should be word-sorted in the Preview page of the multi-page editor";
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "de.ptb.epics.eve.editor", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}