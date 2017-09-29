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
import de.ptb.epics.eve.editor.gef.commands.SelectScanModules;
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
		// clipboard contents
		Object o = Clipboard.getDefault().getContents();
		if (o == null) {
			LOGGER.error("Paste not possible, Clipboard is empty!");
			return null;
		}
		ClipboardContent clipboardContent = (ClipboardContent)o;
		
		// active editor
		IEditorPart editorPart = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (!(editorPart instanceof ScanDescriptionEditor)) {
			LOGGER.error("No Editor active!");
			return null;
		}
		ScanDescriptionEditor editor = (ScanDescriptionEditor)editorPart;
		
		// paste Command (composed of chained commands for each scan module)
		CompoundCommand pasteCommand = new CompoundCommand();
		
		// the chain the editor is representing
		Chain chain = editor.getContent().getChains().get(0);
		chain.setReserveIds(true);
		
		// determine free (graphical) space to paste to
		List<java.awt.Point> smPoints = new ArrayList<java.awt.Point>(chain
				.getScanModules().size() * 4);
		for (ScanModule scanModule : chain.getScanModules()) {
			smPoints.add(new java.awt.Point(scanModule.getX(), scanModule
					.getY()));
			smPoints.add(new java.awt.Point(scanModule.getX()
					+ ScanModule.DEFAULT_WIDTH, scanModule.getY()));
			smPoints.add(new java.awt.Point(scanModule.getX(), scanModule
					.getY() + ScanModule.DEFAULT_HEIGHT));
			smPoints.add(new java.awt.Point(scanModule.getX()
					+ ScanModule.DEFAULT_WIDTH, scanModule.getY()
					+ ScanModule.DEFAULT_HEIGHT));
		}
		Polygon convexHullOfSMs = Geometry.convexHull(smPoints);
		
		// determine "bounding box" of SMs that should be pasted
		List<java.awt.Point> smPastePoints = new ArrayList<java.awt.Point>(
				clipboardContent.getScanModules().size() * 4);
		for (ScanModule scanModule : clipboardContent.getScanModules()) {
			smPastePoints.add(new java.awt.Point(scanModule.getX(), scanModule
					.getY()));
			smPastePoints.add(new java.awt.Point(scanModule.getX()
					+ ScanModule.DEFAULT_WIDTH, scanModule.getY()));
			smPastePoints.add(new java.awt.Point(scanModule.getX(), scanModule
					.getY() + ScanModule.DEFAULT_HEIGHT));
			smPastePoints.add(new java.awt.Point(scanModule.getX()
					+ ScanModule.DEFAULT_WIDTH, scanModule.getY()
					+ ScanModule.DEFAULT_HEIGHT));
		}
		Polygon convexHullOfPasteSMs = Geometry.convexHull(smPastePoints);
		
		// remember which pasted SM represents which "original" SM
		// (necessary to paste connections between them)
		Map<ScanModule, ScanModule> smRelations = 
				new HashMap<ScanModule, ScanModule>(
						clipboardContent.getScanModules().size());
		
		// determine the part that is currently visible in the editor
		Viewport viewPort = (((FigureCanvas) editor
				.getViewer().getControl())).getViewport();
		
		// determine paste origin (depends whether paste was triggered 
		// through main menu, context menu or shortcut
		Point pasteOrigin = new Point(0, 0);
		Point contextMenuPosition = ((ScanDescriptionEditor)editor).
				getContextMenuPosition();
		if (contextMenuPosition == null) {
			Point mousePosition = editor.getCursorPosition();
			if (!viewPort.containsPoint(new org.eclipse.draw2d.geometry.Point(
					mousePosition.x, mousePosition.y))) {
				LOGGER.debug("Paste through main menu");
				// origin should be a reasonable position (e.g. not overlapping 
				// existing modules)
				pasteOrigin = new Point(convexHullOfPasteSMs.getBounds().x,
						convexHullOfSMs.getBounds().y + 10 + 
						convexHullOfSMs.getBounds().height);
				// viewPort.getBounds().x + viewPort.getBounds().width / 2 - ScanModule.DEFAULT_WIDTH / 2
			} else {
				LOGGER.debug("Paste through shortcut (CTRL+V)");
				// origin is the current mouse position
				pasteOrigin = mousePosition;
			}
		} else {
			LOGGER.debug("Paste through context menu");
			// origin is the top left point of the context menu
			pasteOrigin = contextMenuPosition;
		}
		
		// determine the upper left point of the bounding box of paste SMs
		Point relativeZero = new Point(convexHullOfPasteSMs.getBounds()
				.getLocation().x, convexHullOfPasteSMs.getBounds()
				.getLocation().y);
		
		// creating create scan module commands
		List<ScanModule> pasteModules = new ArrayList<ScanModule>();
		
		for (ScanModule sm : clipboardContent.getScanModules()) {
			CreateScanModule createCommand = new CreateScanModule(chain,
					null, ScanModuleTypes.CLASSIC);
			
			pasteModules.add(createCommand.getScanModule());
			
			// adjusting coordinates
			createCommand.getScanModule().setX(
					pasteOrigin.x + sm.getX() - relativeZero.x);
			createCommand.getScanModule().setY(
					pasteOrigin.y + sm.getY() - relativeZero.y);
			
			// save relation between "original" and paste scan module
			smRelations.put(sm, createCommand.getScanModule());
			
			// creating "atomic" commands necessary to "clone" all attributes
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
			
			// add chained command to compound command
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
		
		// creating connection create commands
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
		pasteCommand.add(new SelectScanModules((ScanDescriptionEditor) editor,
				pasteModules));
		
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