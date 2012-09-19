package de.ptb.epics.eve.editor.gef.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class MoveScanModule extends Command {

	private final ScanModule scanModule;
	private final Rectangle box;
	private Rectangle oldBox;
	
	private ChangeBoundsRequest request;
	
	/**
	 * Constructor.
	 * 
	 * @param box the current bounding box
	 */
	public MoveScanModule(ScanModule scanModule, Rectangle contraint,
			ChangeBoundsRequest request) {
		this.scanModule = scanModule;
		this.box = contraint;
		this.request = request;
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
	public void redo() {
		this.scanModule.setX(this.box.x);
		this.scanModule.setY(this.box.y);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		Object type = this.request.getType();
		return (type.equals(RequestConstants.REQ_MOVE) ||
				type.equals(RequestConstants.REQ_MOVE_CHILDREN));
	}
}