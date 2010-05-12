
package de.ptb.epics.eve.viewer;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.viewer.actions.ChangeMessagesSourceFilterAction;
import de.ptb.epics.eve.viewer.actions.ChangeMessagesTypeFilterAction;
import de.ptb.epics.eve.viewer.actions.ClearMessagesAction;
import de.ptb.epics.eve.viewer.actions.SaveMessagesToFileAction;


public final class MessagesView extends ViewPart {

	private TableViewer tableViewer;
	
	private ClearMessagesAction clearMessageAction;
	private ChangeMessagesSourceFilterAction changeMessagesSourceFilterAction;
	private ChangeMessagesTypeFilterAction changeMessagesTypeFilterAction;
	private SaveMessagesToFileAction saveMessagesToFileAction;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl( final Composite parent ) {
		
		this.tableViewer = new TableViewer( parent, SWT.MULTI );
		TableColumn column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 0 );
	    column.setText( "Time" );
	    column.setWidth( 100 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
	    column.setText( "Source" );
	    column.setWidth( 80 );
	    
	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 2 );
	    column.setText( "T" );
	    column.setWidth( 20 );
	    
	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 3 );
	    column.setText( "Message" );
	    column.setWidth( 300 );
	    
	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );
	    
	    this.tableViewer.setContentProvider( new MessagesTableContentProvider() );
	    this.tableViewer.setLabelProvider( new MessagesTableLabelProvider() );
	    
	    this.tableViewer.setInput( Activator.getDefault().getMessagesContainer() );
		
		this.clearMessageAction = new ClearMessagesAction();
		
		this.clearMessageAction.setText( "Clear Messages" );
		this.clearMessageAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ) );
		this.getViewSite().getActionBars().getToolBarManager().add( this.clearMessageAction );

		this.changeMessagesSourceFilterAction = new ChangeMessagesSourceFilterAction( this.tableViewer );
		this.changeMessagesSourceFilterAction.setText( "Change sources filter." );
		this.changeMessagesSourceFilterAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_UP ) );
		this.getViewSite().getActionBars().getToolBarManager().add( this.changeMessagesSourceFilterAction );
		
		this.changeMessagesTypeFilterAction = new ChangeMessagesTypeFilterAction( this.tableViewer );
		this.changeMessagesTypeFilterAction.setText( "Change types filter." );
		this.changeMessagesTypeFilterAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_WARN_TSK ) );
		this.getViewSite().getActionBars().getToolBarManager().add( this.changeMessagesTypeFilterAction );
		
		this.saveMessagesToFileAction = new SaveMessagesToFileAction( this.tableViewer );
		this.saveMessagesToFileAction.setText( "Save messages to a file." );
		this.saveMessagesToFileAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJ_FILE ) );
		this.getViewSite().getActionBars().getToolBarManager().add( this.saveMessagesToFileAction );
		
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {

	}

}
