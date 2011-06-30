package de.ptb.epics.eve.viewer.actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.ptb.epics.eve.viewer.Activator;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class MessagesViewSaveMessagesToFileAction extends Action 
												implements IWorkbenchAction {

	// logging
	private static Logger logger = 
			Logger.getLogger(MessagesViewSaveMessagesToFileAction.class);
	
	private static final String ID = 
			"de.ptb.epics.eve.viewer.actions.SaveMessagesToFileAction";  
	private final TableViewer tableViewer;
	
	/**
	 * 
	 * @param tableViewer
	 */
	public MessagesViewSaveMessagesToFileAction(final TableViewer tableViewer) {
		this.setId(MessagesViewSaveMessagesToFileAction.ID); 
		this.tableViewer = tableViewer;
	} 
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() { 
		
		String filePath = Activator.getDefault().getRootDirectory();
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setFilterPath(filePath);
		
		String name = fileDialog.open();
		
		if(name == null) {
			return;
		}
		
		final File file = new File(name);
		
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch( final IOException e ) {
				logger.error(e.getMessage(), e);
				MessageDialog.openInformation( this.tableViewer.getTable().getShell(), "Error", "Can not create file!" );
				return;
			}
		}
		
		Writer writer = null;
		try {
			writer = new FileWriter( file );
		} catch( final IOException e ) {
			logger.error(e.getMessage(), e);
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
				logger.error(e.getMessage(), e);
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			MessageDialog.openInformation( this.tableViewer.getTable().getShell(), "Error", "Can not close file!" );
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {	
	}
}