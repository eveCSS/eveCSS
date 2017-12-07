package de.ptb.epics.eve.util.net.tests;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import de.ptb.epics.eve.util.net.NetUtil;

/**
 * @author Marcus Michalsky
 * @since 1.29
 */
public class NetUtilTests {
	@Test
	public void testIsItMe() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			String hostName = localHost.getHostName();
			assertTrue(NetUtil.isItMe(hostName));
			assertTrue(NetUtil.isItMe("127.0.0.1"));
			assertTrue(NetUtil.isItMe("localhost"));
			assertFalse(NetUtil.isItMe("::1"));
		} catch (UnknownHostException e) {
			
		}
	}
}