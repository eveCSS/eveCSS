package de.ptb.epics.eve.ecp1.client.test;

import java.io.IOException;
import java.net.InetSocketAddress;


import de.ptb.epics.eve.ecp1.client.ECP1Client;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final ECP1Client client = new ECP1Client();
		try {
			client.connect( new InetSocketAddress( "localhost", 12345 ) , "srehfeld" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//client.getPlayController().start();
		/*client.getPlayController().stop();
		client.getPlayController().halt();
		client.getPlayController().breakExecution();*/
		/*try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		System.out.println( "fdsfsdfs" );
	}

}
