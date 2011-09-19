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

public class MessageTypesFilterDialog extends TitleAreaDialog {
	
	private TableViewer tableViewer;
	
	private Button showDebugMessagesButton;
	private Button showInfoMessagesButton;
	private Button showMinorMessagesButton;
	private Button showErrorMessagesButton;
	private Button showFatalMessagesButton;
	
	private boolean showDebugMessages;
	private boolean showInfoMessages;
	private boolean showMinorMessages;
	private boolean showErrorMessages;
	private boolean showFatalMessages;
	
	public MessageTypesFilterDialog( final Shell shell, final TableViewer tableViewer ) {
		super( shell );
		this.tableViewer = tableViewer;
	}
	
	protected Control createDialogArea( final Composite parent) {
        Composite area = (Composite) super.createDialogArea( parent );
        
        this.showDebugMessagesButton = new Button( area, SWT.CHECK );
        this.showDebugMessagesButton.setText( "Show debug messages." );
        
        this.showInfoMessagesButton = new Button( area, SWT.CHECK );
        this.showInfoMessagesButton.setText( "Show info messages." );
        
        this.showMinorMessagesButton = new Button( area, SWT.CHECK );
        this.showMinorMessagesButton.setText( "Show minor messages." );
        
        this.showErrorMessagesButton = new Button( area, SWT.CHECK );
        this.showErrorMessagesButton.setText( "Show error messages." );
        
        this.showFatalMessagesButton = new Button( area, SWT.CHECK );
        this.showFatalMessagesButton.setText( "Show fatal messages." );

        final MessagesTableContentProvider messagesTableContentProvider = (MessagesTableContentProvider)this.tableViewer.getContentProvider();
        
        this.showDebugMessagesButton.setSelection( messagesTableContentProvider.isShowDebugMessages() );
        this.showDebugMessages = messagesTableContentProvider.isShowDebugMessages();
        this.showDebugMessagesButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
			}

			public void widgetSelected( final SelectionEvent e ) {
				showDebugMessages = showDebugMessagesButton.getSelection();
			}
        	
        });
        
        this.showInfoMessagesButton.setSelection( messagesTableContentProvider.isShowInfoMessages() );
        this.showInfoMessages = messagesTableContentProvider.isShowInfoMessages();
        this.showInfoMessagesButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
			}

			public void widgetSelected( final SelectionEvent e ) {
				showInfoMessages = showInfoMessagesButton.getSelection();
			}
        	
        });
        
        this.showMinorMessagesButton.setSelection( messagesTableContentProvider.isShowMinorMessages() );
        this.showMinorMessages = messagesTableContentProvider.isShowMinorMessages();
        this.showMinorMessagesButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
			}

			public void widgetSelected( final SelectionEvent e ) {
				showMinorMessages = showMinorMessagesButton.getSelection();
			}
        	
        });
        
        this.showErrorMessagesButton.setSelection( messagesTableContentProvider.isShowErrorMessages() );
        this.showErrorMessages = messagesTableContentProvider.isShowErrorMessages();
        this.showErrorMessagesButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
			}

			public void widgetSelected( final SelectionEvent e ) {
				showErrorMessages = showErrorMessagesButton.getSelection();
			}
        	
        });
        
        this.showFatalMessagesButton.setSelection( messagesTableContentProvider.isShowFatalMessages() );
        this.showFatalMessages = messagesTableContentProvider.isShowFatalMessages();
        this.showFatalMessagesButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
			}

			public void widgetSelected( final SelectionEvent e ) {
				showFatalMessages = showFatalMessagesButton.getSelection();
			}
        	
        });
        
        this.setTitle( "Change message types filter." );
        this.setMessage( "" );
        
        return area;
        
	}
	
	public boolean isShowDebugMessages() {
		return this.showDebugMessages;
	}
	
	public boolean isShowInfoMessages() {
		return this.showInfoMessages;
	}
	
	public boolean isShowMinorMessages() {
		return this.showMinorMessages;
	}
	
	public boolean isShowErrorMessages() {
		return this.showErrorMessages;
	}
	
	public boolean isShowFatalMessages() {
		return this.showFatalMessages;
	}
}
