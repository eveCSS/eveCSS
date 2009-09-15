package de.ptb.epics.eve.viewer.actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.viewer.MessagesTableContentProvider;
import de.ptb.epics.eve.viewer.dialogs.MessageSourcesFilterDialog;

public class SaveMessagesToFileAction extends Action implements IWorkbenchAction {

	private static final String ID = "de.ptb.epics.eve.viewer.actions.SaveMessagesToFileAction";  
	private final TableViewer tableViewer;
	
	public SaveMessagesToFileAction( final TableViewer tableViewer ){  
		this.setId( SaveMessagesToFileAction.ID ); 
		this.tableViewer = tableViewer;
	} 
	
	public void run() { 
		
		String name = new FileDialog( this.tableViewer.getTable().getShell(), SWT.SAVE ).open();
		
		if( name == null )
		      return;
		
		final File file = new File( name );
		
		if( !file.exists() ) {
			try {
				file.createNewFile();
			} catch( final IOException e ) {
				e.printStackTrace();
				MessageDialog.openInformation( this.tableViewer.getTable().getShell(), "Error", "Can not create file!" );
				return;
			}
		}
		
		Writer writer = null;
		try {
			writer = new FileWriter( file );
		} catch( final IOException e ) {
			e.printStackTrace();
			MessageDialog.openInformation( this.tableViewer.getTable().getShell(), "Error", "Can not open file for write access!" );
			return;
		}
		
		final TableItem[] tableItems = this.tableViewer.getTable().getItems();
		for( int i = 0; i < tableItems.length; ++i ) {
			try {
				writer.write( tableItems[ i ].getText( 0 ) );
				writer.write( "\t" );
				writer.write( tableItems[ i ].getText( 1 ) );
				writer.write( "\t" );
				writer.write( tableItems[ i ].getText( 2 ) );
				writer.write( "\t" );
				writer.write( tableItems[ i ].getText( 3 ) );
				writer.write( "\n" );
			} catch (IOException e) {
				MessageDialog.openInformation( this.tableViewer.getTable().getShell(), "Error", "Writing of file was interrupted!" );
				e.printStackTrace();
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			MessageDialog.openInformation( this.tableViewer.getTable().getShell(), "Error", "Can not close file!" );
			e.printStackTrace();
		}
	}   
	
	public void dispose() {
		
	}

}
