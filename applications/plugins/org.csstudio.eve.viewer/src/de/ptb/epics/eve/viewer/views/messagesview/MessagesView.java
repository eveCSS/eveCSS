package de.ptb.epics.eve.viewer.views.messagesview;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.viewer.Activator;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public final class MessagesView extends ViewPart {

	// the only element in this view is a table viewer for the messages
	private TableViewer tableViewer;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {

		this.tableViewer = new TableViewer(parent, SWT.MULTI);

		// the first column contains the time
		TableViewerColumn timeColumn = new TableViewerColumn(this.tableViewer,
				SWT.LEFT);
		timeColumn.getColumn().setText("Time");
		timeColumn.getColumn().setWidth(180);

		// second column: source (the sender of the message)
		TableViewerColumn sourceColumn = new TableViewerColumn(this.tableViewer, 
				SWT.LEFT);
		sourceColumn.getColumn().setText("Source");
		sourceColumn.getColumn().setWidth(80);

		// third column: the type (e.g. I for Info or E for Error)
		TableViewerColumn typeColumn = new TableViewerColumn(this.tableViewer, 
				SWT.LEFT);
		typeColumn.getColumn().setText("T");
		typeColumn.getColumn().setWidth(20);

		// fourth column: the text (contents) of the message
		TableViewerColumn messageColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		messageColumn.getColumn().setText("Message");
		messageColumn.getColumn().setWidth(300);

		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);

		// set provider classes which fill the tables labels and content
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setLabelProvider(new LabelProvider());

		// the MessageContainer is the input object of the table viewer
		this.tableViewer.setInput(Activator.getDefault().
				getMessagesContainer());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.tableViewer.getTable().setFocus();
	}
}