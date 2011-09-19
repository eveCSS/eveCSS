package de.ptb.epics.eve.viewer.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.viewer.messages.MessagesTableContentProvider;


public class MessageSourcesFilterDialog extends TitleAreaDialog {

	private final TableViewer tableViewer;
	
	private Button showApplicationMessagesButton;
	private Button showEngineMessagesButton;
	
	private boolean showApplicationMessages;
	private boolean showEngineMessages;
	
	public MessageSourcesFilterDialog( final Shell shell, final TableViewer tableViewer ) {
		super( shell );
		this.tableViewer = tableViewer;
	}
	
	protected Control createDialogArea( final Composite parent) {
        Composite area = (Composite) super.createDialogArea( parent );
        
        this.showApplicationMessagesButton = new Button( area, SWT.CHECK );
        this.showApplicationMessagesButton.setText( "Show messages from Viewer application." );
        
        this.showEngineMessagesButton = new Button( area, SWT.CHECK );
        this.showEngineMessagesButton.setText( "Show messages from engine." );

        final MessagesTableContentProvider messagesTableContentProvider = (MessagesTableContentProvider)this.tableViewer.getContentProvider();
        this.showApplicationMessagesButton.setSelection( messagesTableContentProvider.isShowApplicationMessages() );
        this.showApplicationMessages = messagesTableContentProvider.isShowApplicationMessages();
        this.showApplicationMessagesButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
			}

			public void widgetSelected( final SelectionEvent e ) {
				showApplicationMessages = showApplicationMessagesButton.getSelection();
			}
        	
        });
        
        
        this.showEngineMessagesButton.setSelection( messagesTableContentProvider.isShowEngineMessages() );
        this.showEngineMessages = messagesTableContentProvider.isShowEngineMessages();
        this.showEngineMessagesButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
			}

			public void widgetSelected( final SelectionEvent e ) {
				showEngineMessages = showEngineMessagesButton.getSelection();
			}
        	
        });
        
        this.setTitle( "Change message sources filter." );
        this.setMessage( "" );
        
        return area;
    }
	
	public boolean isShowApplicationMessages() {
		return this.showApplicationMessages;
	}
	
	public boolean isShowEngineMessages() {
		return this.showEngineMessages;
	}

	
}
