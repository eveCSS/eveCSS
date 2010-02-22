package de.ptb.epics.eve.editor.actions;


import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;

import de.ptb.epics.eve.editor.graphical.GraphicalEditor;
import de.ptb.epics.eve.viewer.Activator;

public class HandOverAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run( final IAction action ) {
		
		de.ptb.epics.eve.editor.Activator.getDefault().getWorkbench().saveAllEditors( true );
		
		IPerspectiveRegistry reg = WorkbenchPlugin.getDefault().getPerspectiveRegistry();
		PerspectiveDescriptor perspectiveDescriptor = (PerspectiveDescriptor)reg.findPerspectiveWithId( "EveViewerPerspective" );
		
		final IEditorPart currentEditor = window.getActivePage().getActiveEditor();
		if( currentEditor instanceof GraphicalEditor ) {
			
			final GraphicalEditor graphicalEditor = (GraphicalEditor)currentEditor;
			
			final FileStoreEditorInput editorInput = (FileStoreEditorInput)graphicalEditor.getEditorInput();
			IWorkbenchPage page = window.getActivePage();
			page.setPerspective( perspectiveDescriptor );
		
			final File file = new File( editorInput.getURI() );
			Activator.getDefault().addScanDescription( file );
			
			
		}
		
	}
	
	
	@Override
	public void init( final IWorkbenchWindow window ) {
		this.window = window;
	}

	@Override
	public void selectionChanged( final IAction action, final ISelection selection ) {
		// TODO Auto-generated method stub
		
	}
	
}
