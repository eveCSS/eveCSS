package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.axesdialog;

import org.eclipse.swt.graphics.RGB;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public enum PredefinedColors {
	BLACK("black", new RGB(0,0,0)),
	RED("red", new RGB(255,0,0)),
	GREEN("green", new RGB(0,128,0)),
	BLUE("blue", new RGB(0,0,255)),
	PINK("pink", new RGB(255,0,255)),
	PURPLE("purple", new RGB(128,0,128)),
	CUSTOM("custom...", new RGB(255,255,255));
	
	private final String label;
	private final RGB color;
	
	private PredefinedColors(String label, RGB color) {
		this.label = label;
		this.color = color;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.label;
	}
	
	public RGB getColor() {
		return this.color;
	}
}
