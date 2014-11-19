package de.ptb.epics.eve.viewer.views.messagesview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.messagesview.FilterSettings;
import de.ptb.epics.eve.viewer.views.messagesview.LevelFilter;
import de.ptb.epics.eve.viewer.views.messagesview.MessageFilter;
import de.ptb.epics.eve.viewer.views.messagesview.MessageList;
import de.ptb.epics.eve.viewer.views.messagesview.SourceFilter;
import de.ptb.epics.eve.viewer.views.messagesview.TypeViewerComparator;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public final class MessagesView extends ViewPart implements PropertyChangeListener {
	private static final Logger LOGGER = Logger.getLogger(MessagesView.class
			.getName());
	
	// the only element in this view is a table viewer for the messages
	private TableViewer tableViewer;
	
	private boolean sort;
	private TypeViewerComparator typeViewerComparator;
	private FilterSettings filterSettings;
	
	private Image sortImage;
	
	private IMemento memento;
	
	private int charWidth;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		this.memento = memento;
		super.init(site, memento);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		this.tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | 
				SWT.V_SCROLL);

		// the first column contains the time
		TableViewerColumn timeColumn = new TableViewerColumn(this.tableViewer,
				SWT.LEFT);
		timeColumn.getColumn().setText("Time");
		timeColumn.getColumn().setWidth(220);

		// second column: source (the sender of the message)
		TableViewerColumn sourceColumn = new TableViewerColumn(this.tableViewer, 
				SWT.LEFT);
		sourceColumn.getColumn().setText("Source");
		sourceColumn.getColumn().setWidth(80);

		// third column: the type (e.g. I for Info or E for Error)
		TableViewerColumn typeColumn = new TableViewerColumn(this.tableViewer, 
				SWT.LEFT);
		typeColumn.getColumn().setText("");
		typeColumn.getColumn().setWidth(25);
		typeColumn.getColumn().addSelectionListener(
				new TypeColumnSelectionListener());
		
		// fourth column: the text (contents) of the message
		TableViewerColumn messageColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		messageColumn.getColumn().setText("Message");
		messageColumn.getColumn().setWidth(300);

		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);

		this.tableViewer.setContentProvider(new ObservableListContentProvider());
		this.tableViewer.setLabelProvider(new LabelProvider());
		
		this.tableViewer.setInput(Activator.getDefault().
				getMessageList().getList());
		
		Activator.getDefault().getMessageList().addPropertyChangeListener(
				MessageList.SOURCE_MAX_WIDTH_PROP, this);
		Activator.getDefault().getMessageList().addPropertyChangeListener(
				MessageList.MESSAGE_MAX_WIDTH_PROP, this);
		
		this.sortImage = Activator.getDefault().getImageRegistry()
				.get("SORTARROW");
		this.sort = false;
		this.typeViewerComparator = new TypeViewerComparator();
		
		this.filterSettings = new FilterSettings();
		MessageFilter sourceFilter = new SourceFilter(this.filterSettings);
		MessageFilter levelFilter = new LevelFilter(this.filterSettings);
		this.tableViewer.setFilters(new ViewerFilter[] { sourceFilter,
				levelFilter });
		
		this.restoreState();
		
		GC gc = new GC(tableViewer.getTable());
		FontMetrics fm = gc.getFontMetrics();
		this.charWidth = fm.getAverageCharWidth();
	}
	
	/**
	 * @return the filterSettings
	 */
	public FilterSettings getFilterSettings() {
		return this.filterSettings;
	}

	/**
	 * Refreshes the table of messages.
	 */
	public void refresh() {
		this.tableViewer.refresh();
	}
	
	/*
	 * 
	 */
	private void restoreState() {
		if (this.memento == null) {
			return;
		}
		this.sort = this.memento.getBoolean("sort") != null 
					? this.memento.getBoolean("sort")
					: false;

		if (this.sort) {
			tableViewer.getTable().getColumn(2).setImage(sortImage);
			tableViewer.setComparator(typeViewerComparator);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(IMemento memento) {
		memento.putBoolean("sort", this.sort);
		super.saveState(memento);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.tableViewer.getTable().setFocus();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(MessageList.SOURCE_MAX_WIDTH_PROP)) {
			LOGGER.debug("max width of source column changed");
			LOGGER.debug("current width: "
					+ this.tableViewer.getTable().getColumn(1).getWidth());
			LOGGER.debug(((int) e.getNewValue()) * this.charWidth + 8);
			this.tableViewer.getTable().getColumn(1)
					.setWidth(((int) e.getNewValue()) * this.charWidth + 8);
			//this.tableViewer.getTable().getColumn(1).pack();
		} else if (e.getPropertyName().equals(
				MessageList.MESSAGE_MAX_WIDTH_PROP)) {
			LOGGER.debug("max width of message column changed");
			LOGGER.debug("current width: "
					+ this.tableViewer.getTable().getColumn(1).getWidth());
			LOGGER.debug(((int) e.getNewValue()) * this.charWidth + 8);
			this.tableViewer.getTable().getColumn(3)
					.setWidth(((int) e.getNewValue()) * this.charWidth + 8);
			//this.tableViewer.getTable().getColumn(3).pack();
		}
	}
	
	/* ******************************************************************** */
	/* *************************** Listeners ****************************** */
	/* ******************************************************************** */

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of 
	 * typeColumn.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.4
	 */
	private class TypeColumnSelectionListener implements SelectionListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (sort) {
				tableViewer.getTable().getColumn(2).setImage(null);
				tableViewer.setComparator(null);
				sort = false;
			} else {
				tableViewer.getTable().getColumn(2).setImage(sortImage);
				tableViewer.setComparator(typeViewerComparator);
				tableViewer.getTable().showItem(
						tableViewer.getTable().getItem(0));
				sort = true;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}
}