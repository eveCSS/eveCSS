package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class MoveScanModule extends Command {

	private final ScanModule scanModule;
	private final Rectangle box;
	private Rectangle oldBox;
	
	/**
	 * Constructor.
	 * 
	 * @param box the current bounding box
	 */
	public MoveScanModule(ScanModule scanModule, Rectangle contraint) {
		this.scanModule = scanModule;
		this.box = contraint;
		/*this.box = new Rectangle(this.scanModule.getX(),
				this.scanModule.getY(), this.scanModule.getWidth(),
				this.scanModule.getHeight());*/
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		this.oldBox = new Rectangle(this.scanModule.getX(),
				this.scanModule.getY(), this.scanModule.getWidth(),
				this.scanModule.getHeight());
		this.scanModule.setX(this.box.x);
		this.scanModule.setY(this.box.y);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		this.scanModule.setX(this.oldBox.x);
		this.scanModule.setY(this.oldBox.y);
	}
	
	@Override
	public boolean canExecute() {
		// TODO Auto-generated method stub
		return true;
	}
}