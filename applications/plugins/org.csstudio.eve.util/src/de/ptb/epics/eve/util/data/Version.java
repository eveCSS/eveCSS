package de.ptb.epics.eve.util.data;

/**
 * A comparable Version number consisting of a major 
 * and minor number.
 * 
 * @author Marcus Michalsky
 * @since 1.18
 */
public class Version implements Comparable<Version> {
	private final int major;
	private final int minor;
	
	/**
	 * Constructor.
	 * 
	 * @param major the major version number
	 * @param minor the minor version number
	 */
	public Version(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}
	
	/**
	 * @return the major
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * @return the minor
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Version other) {
		if (this.major == other.major) {
			if (this.minor == other.minor) {
				return 0;
			} else {
				return this.minor - other.minor;
			}
		} else {
			return this.major - other.major;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.major + "." + this.minor;
	}
}