package de.ptb.epics.eve.editor.gef;

import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;

/**
 * 
 * @author mmichals
 *
 */
public class ScanDescriptionEditorPaletteFactory {
	
	/**
	 * 
	 * @return
	 */
	public static PaletteRoot createPalette() {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette));
		palette.add(createElementsDrawer());
		return palette;
	}
	
	private static PaletteEntry createToolsGroup(PaletteRoot palette) {
		PaletteToolbar toolbar = new PaletteToolbar("Tools");
		
		ToolEntry tool = new PanningSelectionToolEntry();
		toolbar.add(tool);
		palette.setDefaultEntry(tool);
		
		toolbar.add(new MarqueeToolEntry());
		
		return toolbar;
	}
	
	private static PaletteEntry createElementsDrawer() {
		PaletteDrawer componentsDrawer = new PaletteDrawer("Elements");
		return componentsDrawer;
	}
}
