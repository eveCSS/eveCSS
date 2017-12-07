package de.ptb.epics.eve.util.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Andre Mittelstädt
 * @since 1.29
 */
public class NetUtil {
	private NetUtil() {
	}
	
	/**
	 * Returns whether the given hostname is the machine the method was called from
	 * @param hostname the hostname to test
	 * @return <code>true</code> if the given hostname is the machine the method was called from,
	 * 	<code>false</code> otherwise
	 */
	public static boolean isItMe(String hostname) {
		try {
			if (InetAddress.getByName(hostname).isLoopbackAddress() && 
					InetAddress.getByName(hostname) instanceof Inet4Address) {
				return true;
			}
			if (InetAddress.getLocalHost().getHostAddress().equals(
					InetAddress.getByName(hostname).getHostAddress())) {
				return true;
			}
		} catch (UnknownHostException e) {
			return false;
		}
		return false;
	}
}