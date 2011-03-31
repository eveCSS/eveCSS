package de.ptb.epics.eve.viewer.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.actions.MessagesViewChangeMessagesSourceFilterAction;
import de.ptb.epics.eve.viewer.actions.MessagesViewChangeMessagesTypeFilterAction;
import de.ptb.epics.eve.viewer.actions.MessagesViewClearMessagesAction;
import de.ptb.epics.eve.viewer.actions.MessagesViewSaveMessagesToFileAction;
import de.ptb.epics.eve.viewer.messages.MessagesTableContentProvider;
import de.ptb.epics.eve.viewer.messages.MessagesTableLabelProvider;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public final class MessagesView extends ViewPart {

	// the only element in this view is a table viewer for the messages
	private TableViewer tableViewer;
	
	// actions provided to manipulate the messages table
	private MessagesViewClearMessagesAction clearMessageAction;
	private MessagesViewChangeMessagesSourceFilterAction changeMessagesSourceFilterAction;
	private MessagesViewChangeMessagesTypeFilterAction changeMessagesTypeFilterAction;
	private MessagesViewSaveMessagesToFileAction saveMessagesToFileAction;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		
		this.tableViewer = new TableViewer(parent, SWT.MULTI);
		
		// the first column contains the time
		TableColumn column = 
			new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 0);
	    column.setText("Time");
	    column.setWidth(180);

	    // second column: source (the sender of the message)
	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 1);
	    column.setText("Source");
	    column.setWidth(80);
	    
	    // third column: the type (e.g. I for Info or E for Error)
	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 2);
	    column.setText("T");
	    column.setWidth(20);
	    
	    // fourth column: the text (contents) of the message
	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 3);
	    column.setText("Message");
	    column.setWidth(300);
	    
	    this.tableViewer.getTable().setHeaderVisible(true);
	    this.tableViewer.getTable().setLinesVisible(true);
	    
	    // set provider classes which fill the tables labels and content
	    this.tableViewer.setContentProvider(new MessagesTableContentProvider());
	    this.tableViewer.setLabelProvider(new MessagesTableLabelProvider());
	    
	    // the MessageContainer is the input object of the table viewer
	    this.tableViewer.setInput(Activator.getDefault().getMessagesContainer());
	    
	    // initialize actions
	    IToolBarManager tbmngr = 
	    		this.getViewSite().getActionBars().getToolBarManager();
	    
	    // action to clear messages (empty the table)
		this.clearMessageAction = new MessagesViewClearMessagesAction();
		this.clearMessageAction.setText("Clear Messages");
		this.clearMessageAction.setImageDescriptor(PlatformUI.getWorkbench().
			getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		tbmngr.add(this.clearMessageAction);

		// action to filter sources
		this.changeMessagesSourceFilterAction = 
				new MessagesViewChangeMessagesSourceFilterAction(this.tableViewer);
		this.changeMessagesSourceFilterAction.setText("Change sources filter.");
		this.changeMessagesSourceFilterAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_TOOL_UP));
		tbmngr.add(this.changeMessagesSourceFilterAction);
		
		// action to filter types
		this.changeMessagesTypeFilterAction = 
			new MessagesViewChangeMessagesTypeFilterAction(this.tableViewer);
		this.changeMessagesTypeFilterAction.setText("Change types filter.");
		this.changeMessagesTypeFilterAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_WARN_TSK));
		tbmngr.add(this.changeMessagesTypeFilterAction);
		
		// action to save the messages to a file
		this.saveMessagesToFileAction = 
			new MessagesViewSaveMessagesToFileAction(this.tableViewer);
		this.saveMessagesToFileAction.setText("Save messages to a file.");
		this.saveMessagesToFileAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		tbmngr.add(this.saveMessagesToFileAction);		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
	}
}