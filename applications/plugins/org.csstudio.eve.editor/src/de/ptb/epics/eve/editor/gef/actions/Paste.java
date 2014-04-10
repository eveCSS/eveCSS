package de.ptb.epics.eve.editor.gef.actions;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.PasteTemplateAction;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.ClipboardContent;
import de.ptb.epics.eve.editor.gef.ScanDescriptionEditor;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModuleAxes;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModuleChannels;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModuleEvents;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModulePlotWindows;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModulePositionings;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModulePostScans;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModulePreScans;
import de.ptb.epics.eve.editor.gef.commands.CopyScanModuleProperties;
import de.ptb.epics.eve.editor.gef.commands.CreateSMConnection;
import de.ptb.epics.eve.editor.gef.commands.CreateScanModule;
import de.ptb.epics.eve.util.math.geometry.Geometry;

/**
 * @author Marcus Michalsky
 * @since 1.19
 */
public class Paste extends PasteTemplateAction {
	private static final Logger LOGGER = Logger.getLogger(Paste.class.getName());
	
	/**
	 * Constructor.
	 * 
	 * @param editor the editor
	 */
	public Paste(IWorkbenchPart editor) {
		super(editor);
		setActionDefinitionId("org.eclipse.ui.edit.paste"); //$NON-NLS-1$
		setId(ActionFactory.PASTE.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean calculateEnabled() {
		if (Clipboard.getDefault().getContents() != null) {
			LOGGER.debug("Paste enabled");
			return true;
		}
		LOGGER.debug("Paste disabled");
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createPasteCommand() {
		LOGGER.debug("create paste command");
		Object o = Clipboard.getDefault().getContents();
		if (o == null) {
			LOGGER.error("Paste not possible, Clipboard is empty!");
			return null;
		}
		ClipboardContent clipboardContent = (ClipboardContent)o;
		
		CompoundCommand pasteCommand = new CompoundCommand();
		
		IEditorPart editor = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (!(editor instanceof ScanDescriptionEditor)) {
			LOGGER.error("No Editor active!");
			return null;
		}
		
		Chain chain = ((ScanDescriptionEditor) editor).getContent().getChains()
				.get(0);
		chain.setReserveIds(true);
		
		Map<ScanModule, ScanModule> smRelations = 
				new HashMap<ScanModule, ScanModule>(
						clipboardContent.getScanModules().size());
		
		Point point = ((ScanDescriptionEditor)editor).getContextMenuPosition();
		if (point == null) {
			point = ((ScanDescriptionEditor)editor).getCursorPosition();
			
			Viewport viewPort = (((FigureCanvas) ((ScanDescriptionEditor) editor)
					.getViewer().getControl())).getViewport();
			if (!viewPort.containsPoint(new org.eclipse.draw2d.geometry.Point(
					point.x, point.y))) {
				LOGGER.debug("Paste through Edit menu");
				
				List<java.awt.Point> points = new ArrayList<java.awt.Point>(chain
						.getScanModules().size() * 4);
				for (ScanModule scanModule : chain.getScanModules()) {
					points.add(new java.awt.Point(scanModule.getX(), scanModule
							.getY()));
					points.add(new java.awt.Point(scanModule.getX()
							+ ScanModule.DEFAULT_WIDTH, scanModule.getY()));
					points.add(new java.awt.Point(scanModule.getX(), scanModule
							.getY() + ScanModule.DEFAULT_HEIGHT));
					points.add(new java.awt.Point(scanModule.getX()
							+ ScanModule.DEFAULT_WIDTH, scanModule.getY()
							+ ScanModule.DEFAULT_HEIGHT));
				}
				Polygon convexHull = Geometry.convexHull(points);
				point = new Point(viewPort.getBounds().x
						+ viewPort.getBounds().width / 2
						- ScanModule.DEFAULT_WIDTH / 2,
						convexHull.getBounds().y + 
						convexHull.getBounds().height + 10);
			} else {
				LOGGER.debug("Paste trough CTRL+V");
			}
		} else {
			LOGGER.debug("Paste through context menu.");
		}
		
		for (ScanModule sm : clipboardContent.getScanModules()) {
			CreateScanModule createCommand = new CreateScanModule(chain,
					null, ScanModuleTypes.CLASSIC);
			
			createCommand.getScanModule().setX(point.x);
			createCommand.getScanModule().setY(point.y);
			LOGGER.debug("Paste Position: (" + point.x + ", " + point.y + ")");
			
			smRelations.put(sm, createCommand.getScanModule());
			
			CopyScanModuleProperties propertiesCommand = 
					new CopyScanModuleProperties(
							sm, createCommand.getScanModule());
			CopyScanModuleAxes axesCommand = 
					new CopyScanModuleAxes(
							sm, createCommand.getScanModule());
			CopyScanModuleChannels channelsCommand = 
					new CopyScanModuleChannels(
							sm, createCommand.getScanModule());
			CopyScanModulePreScans prescanCommand =
					new CopyScanModulePreScans(
							sm, createCommand.getScanModule());
			CopyScanModulePostScans postscanCommand = 
					new CopyScanModulePostScans(
							sm, createCommand.getScanModule());
			CopyScanModulePositionings positioningCommand = 
					new CopyScanModulePositionings(
							sm, createCommand.getScanModule());
			CopyScanModulePlotWindows plotWindowCommand = 
					new CopyScanModulePlotWindows(
							sm, createCommand.getScanModule());
			CopyScanModuleEvents eventCommand = 
					new CopyScanModuleEvents(
							sm, createCommand.getScanModule());
			
			pasteCommand.add(createCommand.
					chain(propertiesCommand).
					chain(axesCommand).
					chain(channelsCommand).
					chain(prescanCommand).
					chain(postscanCommand).
					chain(positioningCommand).
					chain(plotWindowCommand).
					chain(eventCommand));
		}
		
		for (Connector conn : clipboardContent.getConnections()) {
			ScanModule parent = conn.getParentScanModule();
			ScanModule child = conn.getChildScanModule();
			LOGGER.debug("Parent: " + parent.getName() + " , child: "
					+ child.getName() + " , type: " + conn.getType());
			if (smRelations.get(parent) != null && 
					smRelations.get(child) != null) {
				// both SMs of connection are in the clipboard
				CreateSMConnection createConnectionCommand = 
						new CreateSMConnection(
								smRelations.get(parent), smRelations.get(child),
								conn.getType());
				pasteCommand.add(createConnectionCommand);
			}
		}
		
		chain.resetReservedIds();
		chain.setReserveIds(false);
		return pasteCommand;
	}
	
	/**
	 * Refreshes the enabled state.
	 */
	public void refresh() {
		super.refresh();
	}
}