package de.ptb.epics.eve.viewer.views.messagesview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.util.ui.swt.FontHelper;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.messagesview.FilterSettings;
import de.ptb.epics.eve.viewer.views.messagesview.IMessageList;
import de.ptb.epics.eve.viewer.views.messagesview.LevelFilter;
import de.ptb.epics.eve.viewer.views.messagesview.Levels;
import de.ptb.epics.eve.viewer.views.messagesview.MessageFilter;
import de.ptb.epics.eve.viewer.views.messagesview.MessageList;
import de.ptb.epics.eve.viewer.views.messagesview.SourceFilter;
import de.ptb.epics.eve.viewer.views.messagesview.TimeViewerComparator;

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
	
	private FilterSettings filterSettings;
	
	private boolean scrollLock;
	
	private IMemento memento;
	private static final String SHOW_VIEWER_MESSAGES_MEMENTO = "showViewerMessages";
	private static final String SHOW_ENGINE_MESSAGES_MEMENTO = "showEngineMessages";
	private static final String MESSAGE_LEVEL_MEMENTO = "messageLevel";
	private static final String AUTO_SCROLL_MEMENTO = "scrollLock";

	private IMessageList messageList;
	private IListChangeListener messageListListChangeListener;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
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
		//typeColumn.getColumn().addSelectionListener(
		//		new TypeColumnSelectionListener());
		
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
		
		this.messageList = Activator.getDefault().getMessageList();
		this.messageList.addPropertyChangeListener(
				MessageList.SOURCE_MAX_WIDTH_PROP, this);
		this.messageList.addPropertyChangeListener(
				MessageList.MESSAGE_MAX_WIDTH_PROP, this);
		this.messageListListChangeListener = new IListChangeListener() {
			@Override
			public void handleListChange(ListChangeEvent event) {
				if (!scrollLock) {
					LOGGER.debug("new element & auto scroll on -> show top");
					tableViewer.getTable().setTopIndex(0);
				}
			}
		};
		this.messageList.getList().addListChangeListener(
				this.messageListListChangeListener);
		
		this.filterSettings = new FilterSettings();
		this.restoreState();
		MessageFilter sourceFilter = new SourceFilter(this.filterSettings);
		MessageFilter levelFilter = new LevelFilter(this.filterSettings);
		
		this.tableViewer.setFilters(new ViewerFilter[] { sourceFilter,
				levelFilter });
		
		this.tableViewer.setComparator(new TimeViewerComparator());
		
		this.refreshToggleButton();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		messageList.removePropertyChangeListener(
				MessageList.SOURCE_MAX_WIDTH_PROP, this);
		messageList.removePropertyChangeListener(
				MessageList.MESSAGE_MAX_WIDTH_PROP, this);
		messageList.getList().removeListChangeListener(
				this.messageListListChangeListener);
		super.dispose();
	}
	
	/**
	 * @param scrollLock
	 * @since 1.24
	 */
	public void setScrollLock(boolean scrollLock) {
		this.scrollLock = scrollLock;
	}
	
	/*
	 * @since 1.24
	 */
	private void refreshToggleButton() {
		ICommandService commandService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		Command toggleCommand = commandService
				.getCommand("de.ptb.epics.eve.viewer.messages.scrolllock");

		State state = toggleCommand
				.getState("org.eclipse.ui.commands.toggleState");

		state.setValue(this.scrollLock);
		
		commandService.refreshElements(
				"de.ptb.epics.eve.viewer.messages.scrolllock", null);
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.tableViewer.getTable().setFocus();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 1.24
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putBoolean(MessagesView.SHOW_VIEWER_MESSAGES_MEMENTO, 
				this.filterSettings.isShowViewerMessages());
		memento.putBoolean(MessagesView.SHOW_ENGINE_MESSAGES_MEMENTO, 
				this.filterSettings.isShowEngineMessages());
		memento.putString(MessagesView.MESSAGE_LEVEL_MEMENTO, 
				this.filterSettings.getMessageThreshold().toString());
		memento.putBoolean(MessagesView.AUTO_SCROLL_MEMENTO, this.scrollLock);
	}
	
	private void restoreState() {
		if (this.memento == null) {
			return;
		}
		Boolean showViewerMessages = this.memento.getBoolean(
				MessagesView.SHOW_VIEWER_MESSAGES_MEMENTO);
		if (showViewerMessages != null) {
			this.filterSettings.setShowViewerMessages(showViewerMessages);
		}
		Boolean showEngineMessages = this.memento.getBoolean(
				MessagesView.SHOW_ENGINE_MESSAGES_MEMENTO);
		if (showEngineMessages != null) {
			this.filterSettings.setShowEngineMessages(showEngineMessages);
		}
		String messageLevel = this.memento.getString(MESSAGE_LEVEL_MEMENTO);
		if (messageLevel != null) {
			this.filterSettings.setMessageThreshold(Levels.stringToEnum(
					messageLevel.toUpperCase()));
		}
		this.scrollLock = 
			this.memento.getBoolean(MessagesView.AUTO_SCROLL_MEMENTO) == null 
				? false
				: this.memento.getBoolean(MessagesView.AUTO_SCROLL_MEMENTO);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(MessageList.SOURCE_MAX_WIDTH_PROP)) {
			int charWidth = FontHelper.getCharWidth(
					new GC(tableViewer.getTable()), Activator.getDefault()
							.getMessageList().getSourceString());
			LOGGER.debug("max width of source column changed");
			LOGGER.debug("current width: "
					+ this.tableViewer.getTable().getColumn(1).getWidth() + 
					", new width: " + charWidth);
			this.tableViewer.getTable().getColumn(1)
					.setWidth(charWidth + FontHelper.MARGIN_WIDTH);
		} else if (e.getPropertyName().equals(
				MessageList.MESSAGE_MAX_WIDTH_PROP)) {
			int charWidth = FontHelper.getCharWidth(
					new GC(tableViewer.getTable()), Activator.getDefault()
							.getMessageList().getMessageString());
			LOGGER.debug("max width of message column changed");
			LOGGER.debug("current width: "
					+ this.tableViewer.getTable().getColumn(1).getWidth() + 
					", new width: " + charWidth);
			this.tableViewer.getTable().getColumn(3)
					.setWidth(charWidth + FontHelper.MARGIN_WIDTH);
		}
	}
}