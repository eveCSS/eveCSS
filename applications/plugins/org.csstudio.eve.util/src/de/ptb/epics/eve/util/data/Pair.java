package de.ptb.epics.eve.util.data;

/**
 * @author Mark Elliot
 * @see <a href="http://stackoverflow.com/questions/4777622/creating-a-list-of-pairs-in-java">stackoverflow<a/>
 * @since 1.13
 */
public class Pair<L, R> {
	private L l;
	private R r;

	public Pair(L l, R r) {
		this.l = l;
		this.r = r;
	}

	public L getL() {
		return l;
	}

	public R getR() {
		return r;
	}

	public void setL(L l) {
		this.l = l;
	}

	public void setR(R r) {
		this.r = r;
	}
	
	/**
	 * {@inheritDoc}
	 * @author Marcus Michalsky
	 * @since 1.13
	 */
	@Override
	public int hashCode() {
		return l.hashCode() + r.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * @author Marcus Michalsky
	 * @since 1.13
	 */
	@Override
	public boolean equals(Object obj) {
		// check for self comparison
		if (this == obj) {
			return true;
		}
		
		// check type
		if (!(obj instanceof Pair)) {
			return false;
		}
		
		// cast is now safe
		Pair<?,?> other = (Pair<?,?>)obj;
		
		// field-by-field evaluation
		return this.l.equals(other.getL()) && this.r.equals(other.getR());
	}
}