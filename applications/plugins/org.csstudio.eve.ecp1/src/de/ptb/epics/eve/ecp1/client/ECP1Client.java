package de.ptb.epics.eve.ecp1.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayController;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayListController;
import de.ptb.epics.eve.ecp1.client.interfaces.IRequestListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.client.model.Request;
import de.ptb.epics.eve.ecp1.commands.CancelRequestCommand;
import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.commands.CurrentXMLCommand;
import de.ptb.epics.eve.ecp1.commands.EngineStatusCommand;
import de.ptb.epics.eve.ecp1.commands.ErrorCommand;
import de.ptb.epics.eve.ecp1.commands.GenericRequestCommand;
import de.ptb.epics.eve.ecp1.commands.IECP1Command;
import de.ptb.epics.eve.ecp1.commands.MeasurementDataCommand;
import de.ptb.epics.eve.ecp1.commands.PlayListCommand;

/**
 * Client which connects to the engine. Use as followed:
 * <ul>
 * 	<li>create object</li>
 *  <li>connect to the (running) engine process via 
 *  	{@link #connect(SocketAddress, String)}</li>
 * </ul>
 * To get information about the engine several listeners can be added:
 * <ul>
 * 	<li>{@link #addEngineStatusListener(IEngineStatusListener)} : engine status 
 * 		updates as in {@link de.ptb.epics.eve.ecp1.types.EngineStatus}</li>
 * 	<li>{@link #addChainStatusListener(IChainStatusListener)} : chain status
 * 		updates as in {@link de.ptb.epics.eve.ecp1.types.ChainStatus}</li>
 *  <li>{@link #addErrorListener(IErrorListener)} : errors as in 
 *  	{@link de.ptb.epics.eve.ecp1.client.model.Error}</li>
 *  <li>{@link #addMeasurementDataListener(IMeasurementDataListener)} : 
 *  	{@link de.ptb.epics.eve.ecp1.client.model.MeasurementData}</li>
 *  <li>{@link #addRequestListener(IRequestListener)} :  
 *  	{@link de.ptb.epics.eve.ecp1.client.model.Request}s of different 
 *  	{@link de.ptb.epics.eve.ecp1.types.RequestType}s</li>
 *  <li> {@link #addConnectionStateListener(IConnectionStateListener)} : 
 *  	connection state (i.e. connected or disconnected)</li>
 * </ul>
 * 
 * @author ?
 * @author Marcus Michalsky
 * @since 1.0
 */
public class ECP1Client {
	
	private static Logger logger = Logger.getLogger(ECP1Client.class.getName());

	private ECP1Client ecp1Client;

	private Socket socket;
	private Queue<byte[]> inQueue;
	private Queue<IECP1Command> outQueue;
	private InHandler inHandler;
	private OutHandler outHandler;
	private Thread inThread;
	private Thread outThread;
	private Thread dispatchThread;
	private ECP1Client.InDispatcher dispatchHandler;

	private String loginName;

	private PlayController playController;
	private PlayListController playListController;

	private final Queue<IEngineStatusListener> engineStatusListener;
	private final Queue<IChainStatusListener> chainStatusListener;
	private final Queue<IErrorListener> errorListener;
	private final Queue<IMeasurementDataListener> measurementDataListener;
	private final Queue<IRequestListener> requestListener;

	private final Queue<IConnectionStateListener> connectionStateListener;

	private final Map<Integer, Request> requestMap;
	private List<String> classNames;
	private Map<Character, Constructor<? extends IECP1Command>> commands;

	private boolean running;

	/**
	 * Constructor.
	 */
	@SuppressWarnings("unchecked")
	public ECP1Client() {
		this.ecp1Client = this;

		this.inQueue = new ConcurrentLinkedQueue<byte[]>();
		this.outQueue = new ConcurrentLinkedQueue<IECP1Command>();

		this.playController = new PlayController(this);
		this.playListController = new PlayListController(this);

		this.engineStatusListener = 
				new ConcurrentLinkedQueue<IEngineStatusListener>();
		this.chainStatusListener = 
				new ConcurrentLinkedQueue<IChainStatusListener>();
		this.errorListener = new ConcurrentLinkedQueue<IErrorListener>();
		this.measurementDataListener = 
				new ConcurrentLinkedQueue<IMeasurementDataListener>();
		this.requestListener = new ConcurrentLinkedQueue<IRequestListener>();
		this.connectionStateListener = 
				new ConcurrentLinkedQueue<IConnectionStateListener>();

		/* ****************************************************** */
		// TODO following code has to be documented/explained
		this.requestMap = new HashMap<Integer, Request>();

		this.classNames = new ArrayList<String>();
		final String packageName = "de.ptb.epics.eve.ecp1.commands.";

		this.classNames.add(packageName + "AddToPlayListCommand");
		this.classNames.add(packageName + "AnswerRequestCommand");
		this.classNames.add(packageName + "AutoPlayCommand");
		this.classNames.add(packageName + "BreakCommand");
		this.classNames.add(packageName + "CancelRequestCommand");

		this.classNames.add(packageName + "ChainStatusCommand");
		this.classNames.add(packageName + "CurrentXMLCommand");
		this.classNames.add(packageName + "EndProgramCommand");
		this.classNames.add(packageName + "EngineStatusCommand");
		this.classNames.add(packageName + "ErrorCommand");

		this.classNames.add(packageName + "GenericRequestCommand");
		this.classNames.add(packageName + "HaltCommand");
		this.classNames.add(packageName + "LiveDescriptionCommand");
		this.classNames.add(packageName + "MeasurementDataCommand");
		this.classNames.add(packageName + "PauseCommand");
		this.classNames.add(packageName + "PlayListCommand");
		this.classNames.add(packageName + "RemoveFromPlayListCommand");
		this.classNames.add(packageName + "ReorderPlayListCommand");
		this.classNames.add(packageName + "StartCommand");
		this.classNames.add(packageName + "StopCommand");

		this.commands = new HashMap<Character, Constructor<? extends IECP1Command>>();
		
		final Iterator<String> it = this.classNames.iterator();
		final Class<IECP1Command> ecp1CommandInterface = IECP1Command.class;

		final Class<byte[]> byteArrayClass = byte[].class;
		final Class<?>[] constructorParameterArray = new Class<?>[1];
		constructorParameterArray[0] = byteArrayClass;
		while (it.hasNext()) {
			final String className = it.next();
			try {
				Class<? extends IECP1Command> classObject = (Class<? extends IECP1Command>) Class
						.forName(className);
				if (!ecp1CommandInterface.isAssignableFrom(classObject)) {
					System.err.println("Error: " + className
							+ " is not implementing IECP1Command!");
					continue;
				}
				final Field commandTypeIDField = classObject
						.getField("COMMAND_TYPE_ID");
				final char id = commandTypeIDField.getChar(null);
				final Constructor<? extends IECP1Command> constructor = classObject
						.getConstructor(constructorParameterArray);
				if (this.commands.get(id) == null) {
					this.commands.put(id, constructor);
				} else {
					System.err.println("Error: "
							+ className
							+ " and "
							+ this.commands.get(id).getDeclaringClass()
									.getName() + "does have the same id = "
							+ Integer.toHexString(id) + "!");
				}
				// System.out.println( "The ID for " + className + " is " +
				// Integer.toHexString(id) + "!" );
			} catch (final ClassNotFoundException exception) {
				System.err
						.println("Error: Can't find Class " + className + "!");
			} catch (final SecurityException exception) {
				exception.printStackTrace();
			} catch (final NoSuchFieldException exception) {
				System.err
						.println("Error: Can't find static Field COMMAND_TYPE_ID in "
								+ className + "!");
			} catch (final IllegalArgumentException exception) {
				// TODO Auto-generated catch block
				exception.printStackTrace();
			} catch (final IllegalAccessException exception) {
				// TODO Auto-generated catch block
				exception.printStackTrace();
			} catch (final NoSuchMethodException exception) {
				System.err
						.println("Error: Can't find Constructor for byte Array as Parameter in "
								+ className + "!");
			}
		}
		
		// end of TODO
		/* *************************************************************** */
		
	}

	/**
	 * Connects to the engine at the given address.
	 * 
	 * @param socketAddress the address to connect to
	 * @param loginName the login name
	 * @throws IOException if an error occurs during socket connection
	 */
	public void connect(SocketAddress socketAddress, final String loginName)
			throws IOException {
		this.loginName = loginName;
		this.socket = new Socket();
		try {
			this.socket.connect(socketAddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.inQueue.clear();
		this.outQueue.clear();

		this.running = true;

		this.inHandler = new InHandler(this, this.socket.getInputStream(),
				this.inQueue);
		this.outHandler = new OutHandler(this.socket.getOutputStream(),
				this.outQueue);
		this.dispatchHandler = new ECP1Client.InDispatcher();
		this.inThread = new Thread(this.inHandler);
		this.outThread = new Thread(this.outHandler);
		this.dispatchThread = new Thread(this.dispatchHandler);

		this.inThread.start();
		this.outThread.start();
		this.dispatchThread.start();

		for (IConnectionStateListener icsl : this.connectionStateListener) {
			icsl.stackConnected();
		}
	}

	/**
	 * Disconnects the engine.
	 * 
	 * @throws IOException if an I/O error occurs during socket shutdown
	 */
	public void disconnect() throws IOException {
		if (this.running) {
			this.inHandler.quit();
			this.outHandler.quit();
			this.running = false;
			this.socket.shutdownInput();
			this.socket.shutdownOutput();
			this.socket.close();
			for (IConnectionStateListener icsl : this.connectionStateListener) {
				icsl.stackDisconnected();
			}
		}
	}

	/**
	 * Adds an {@link de.ptb.epics.eve.ecp1.commands.IECP1Command} to the out 
	 * queue.
	 * 
	 * @param ecp1Command the command that should be added
	 */
	public void addToOutQueue(final IECP1Command ecp1Command) {
		this.outQueue.add(ecp1Command);
	}

	/**
	 * Returns the login name.
	 * 
	 * @return the login name
	 */
	public String getLoginName() {
		return this.loginName;
	}

	/**
	 * Checks whether the engine is running.
	 * 
	 * @return <code>true</code> if the engine is running, 
	 * 			<code>false</code> otherwise
	 */
	public boolean isRunning() {
		return this.running;
	}
	
	/**
	 * Returns the play controller.
	 * 
	 * @return the play controller
	 */
	public IPlayController getPlayController() {
		return this.playController;
	}
	
	/**
	 * Returns the play list controller.
	 * 
	 * @return the play list controller
	 */
	public IPlayListController getPlayListController() {
		return this.playListController;
	}

	/**
	 * Adds the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener}.
	 * 
	 * @param engineStatusListener the listener that should be added
	 * @return <code>true</code> if the listener was added, 
	 * 			<code>false</code> otherwise
	 */
	public boolean addEngineStatusListener(
			final IEngineStatusListener engineStatusListener) {
		return this.engineStatusListener.add(engineStatusListener);
	}

	/**
	 * Removes the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener}.
	 * 
	 * @param engineStatusListener the listener that should be removed
	 * @return <code>true</code> if the listener was removed, 
	 * 			<code>false</code> otherwise
	 */
	public boolean removeEngineStatusListener(
			final IEngineStatusListener engineStatusListener) {
		return this.engineStatusListener.remove(engineStatusListener);
	}

	/**
	 * Adds the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener}.
	 * 
	 * @param chainStatusListener the listener that should be added
	 * @return <code>true</code> if the listener was added, 
	 * 			<code>false</code> otherwise
	 */
	public boolean addChainStatusListener(
			final IChainStatusListener chainStatusListener) {
		return this.chainStatusListener.add(chainStatusListener);
	}

	/**
	 * Removes the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener}.
	 * 
	 * @param chainStatusListener the listener that should be removed
	 * @return <code>true</code> if the listener was removed, 
	 * 			<code>false</code> otherwise
	 */
	public boolean removeChainStatusListener(
			final IChainStatusListener chainStatusListener) {
		return this.chainStatusListener.remove(chainStatusListener);
	}

	/**
	 * Adds the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener}.
	 * 
	 * @param errorListener the listener that should be added
	 * @return <code>true</code> if the listener was added, 
	 * 			<code>false</code> otherwise
	 */
	public boolean addErrorListener(final IErrorListener errorListener) {
		return this.errorListener.add(errorListener);
	}

	/**
	 * Removes the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener}.
	 * 
	 * @param errorListener the listener that should be removed
	 * @return <code>true</code> if the listener was removed, 
	 * 			<code>false</code> otherwise
	 */
	public boolean removeErrorListener(final IErrorListener errorListener) {
		return this.errorListener.remove(errorListener);
	}

	/**
	 * Adds the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener}.
	 * 
	 * @param measurementDataListener the listener that should be added
	 * @return <code>true</code> if the listener was added, 
	 * 			<code>false</code> otherwise
	 */
	public boolean addMeasurementDataListener(
			final IMeasurementDataListener measurementDataListener) {
		return this.measurementDataListener.add(measurementDataListener);
	}

	/**
	 * Removes the given
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener}.
	 * 
	 * @param measurementDataListener the listener that should be removed
	 * @return <code>true</code> if the listener was removed, 
	 * 			<code>false</code> otherwise
	 */
	public boolean removeMeasurementDataListener(
			final IMeasurementDataListener measurementDataListener) {
		return this.measurementDataListener.remove(measurementDataListener);
	}

	/**
	 * Adds the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IRequestListener}.
	 * 
	 * @param requestListener the listener that should be added
	 * @return <code>true</code> if the listener was added, 
	 * 			<code>false</code> otherwise
	 */
	public boolean addRequestListener(final IRequestListener requestListener) {
		return this.requestListener.add(requestListener);
	}

	/**
	 * Removes the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IRequestListener}.
	 * 
	 * @param requestListener the listener that should be removed
	 * @return <code>true</code> if the listener was removed, 
	 * 			<code>false</code> otherwise
	 */
	public boolean removeRequestListener(final IRequestListener requestListener) {
		return this.requestListener.remove(requestListener);
	}

	/**
	 * Adds the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener}.
	 * 
	 * @param requestListener the listener that should be added
	 * @return <code>true</code> if the listener was added, 
	 * 			<code>false</code> otherwise
	 */
	public boolean addConnectionStateListener(
			final IConnectionStateListener requestListener) {
		return this.connectionStateListener.add(requestListener);
	}

	/**
	 * Removes the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener}.
	 * 
	 * @param requestListener the listener that should be removed
	 * @return <code>true</code> if the listener was removed, 
	 * 			<code>false</code> otherwise
	 */
	public boolean removeConnectionStateListener(
			final IConnectionStateListener requestListener) {
		return this.connectionStateListener.remove(requestListener);
	}

	/* ********************************************************************* */
	/* ********************************************************************* */
	/* ********************************************************************* */

	/**
	 * @author ?
	 * @since 1.0
	 */
	private class InDispatcher implements Runnable {
		@Override public void run() {
			while (running) {
				if (!inQueue.isEmpty()) {
					final byte[] packageArray = inQueue.poll();
					final ByteArrayInputStream byteArrayInputStream = 
							new ByteArrayInputStream(packageArray);
					final DataInputStream dataInputStream = new DataInputStream(
							byteArrayInputStream);
					try {
						dataInputStream.skip(6);
						final char commandId = dataInputStream.readChar();
						if (logger.isDebugEnabled()) {
							logger.debug("command id: " + 
									Integer.toHexString(commandId));
						}
						Constructor<? extends IECP1Command> commandConstructor = 
								commands.get(commandId);
						if (commandConstructor != null) {
							final Object[] parameters = new Object[1];
							parameters[0] = packageArray;
							IECP1Command command = commandConstructor
									.newInstance(packageArray);
							if (command instanceof EngineStatusCommand) {
								final EngineStatusCommand engineStatusCommand = 
										(EngineStatusCommand) command;
								for (IEngineStatusListener esl : engineStatusListener) {
									esl.engineStatusChanged(
										engineStatusCommand.getEngineStatus(), 
										engineStatusCommand.getXmlName(), 
										engineStatusCommand.getRepeatCount());
								}
								playListController.reportAutoplay(
										engineStatusCommand.isAutoplay());
							} else if (command instanceof ChainStatusCommand) {
								final ChainStatusCommand chainStatusCommand = 
										(ChainStatusCommand) command;
								for (IChainStatusListener csl : chainStatusListener) {
									csl.chainStatusChanged(chainStatusCommand);
								}
							} else if (command instanceof ErrorCommand) {
								final ErrorCommand errorCommand = 
										(ErrorCommand) command;
								final de.ptb.epics.eve.ecp1.client.model.Error error = 
										new de.ptb.epics.eve.ecp1.client.model.Error(
										errorCommand);
								for (IErrorListener el : errorListener) {
									el.errorOccured(error);
								}
							} else if (command instanceof MeasurementDataCommand) {
								final MeasurementDataCommand measurementDataCommand = 
										(MeasurementDataCommand) command;
								final MeasurementData measurementData = 
										new MeasurementData(measurementDataCommand);
								for (IMeasurementDataListener mdl : measurementDataListener) {
									mdl.measurementDataTransmitted(measurementData);
								}
							} else if (command instanceof CurrentXMLCommand) {
								final CurrentXMLCommand currentXMLCommand = 
										(CurrentXMLCommand) command;
								playListController.reportCurrentXMLCommand(
										currentXMLCommand);
							} else if (command instanceof PlayListCommand) {
								final PlayListCommand playListCommand = 
										(PlayListCommand) command;
								playListController.reportPlayListCommand(
										playListCommand);
							} else if (command instanceof GenericRequestCommand) {
								final GenericRequestCommand genericRequestCommand = 
										(GenericRequestCommand) command;
								final Request request = new Request(
										genericRequestCommand, ecp1Client);
								requestMap.put(request.getRequestId(), request);
								for (IRequestListener rl : requestListener) {
									rl.request(request);
								}
							} else if (command instanceof CancelRequestCommand) {
								final CancelRequestCommand cancelRequestCommand = 
										(CancelRequestCommand) command;
								final Request request = requestMap.get(
										cancelRequestCommand.getRequestId());
								if (request != null) {
									for (IRequestListener rl : requestListener) {
										rl.cancelRequest(request);
									}
								}
							} else {
								logger.error(
										"Undispatchable Package with the Type "
										+ command.getClass().getName());
							}
						} else {
							logger.error("Unknown package type with the id: "
									+ Integer.toHexString(commandId));
						}
					} catch (final IOException e) {
						logger.error(e.getMessage(), e);
					} catch (final IllegalArgumentException e) {
						logger.error(e.getMessage(), e);
					} catch (final InstantiationException e) {
						logger.error(e.getMessage(), e);
					} catch (final IllegalAccessException e) {
						logger.error(e.getMessage(), e);
					} catch (final InvocationTargetException e) {
						logger.error(e.getMessage(), e);
					}
				} else {
					try {
						Thread.sleep(10);
					} catch (final InterruptedException e) {
						logger.warn(e.getMessage(), e);
					}
				}
			}
		}
	}
}