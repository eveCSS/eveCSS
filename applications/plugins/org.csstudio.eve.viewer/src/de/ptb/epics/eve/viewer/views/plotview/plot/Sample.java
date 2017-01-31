package de.ptb.epics.eve.viewer.views.plotview.plot;

import org.csstudio.swt.xygraph.dataprovider.ISample;

/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public class Sample implements ISample {

	private double x;
	private double y;
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public Sample(Number x, Number y) {
		this.x = x.doubleValue();
		this.y = y.doubleValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getXMinusError() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getXPlusError() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getYMinusError() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getYPlusError() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInfo() {
		return "(" + this.x + ", " + this.y + ")";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getXValue() {
		return this.x;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getYValue() {
		return this.y;
	}
	
	protected void invertYValue() {
		this.y = -this.y;//Math.copySign(this.y, -1.0);
	}
}