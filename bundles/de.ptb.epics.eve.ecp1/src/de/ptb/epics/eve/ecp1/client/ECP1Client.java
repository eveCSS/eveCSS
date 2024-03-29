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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IChainProgressListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IConnectionStateListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineVersionListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IPauseStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayController;
import de.ptb.epics.eve.ecp1.client.interfaces.IPlayListController;
import de.ptb.epics.eve.ecp1.client.interfaces.IRequestListener;
import de.ptb.epics.eve.ecp1.client.interfaces.ISimulationStatusListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.client.model.Request;
import de.ptb.epics.eve.ecp1.commands.CancelRequestCommand;
import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.commands.ChainProgressCommand;
import de.ptb.epics.eve.ecp1.commands.CurrentXMLCommand;
import de.ptb.epics.eve.ecp1.commands.EngineStatusCommand;
import de.ptb.epics.eve.ecp1.commands.EngineVersionCommand;
import de.ptb.epics.eve.ecp1.commands.ErrorCommand;
import de.ptb.epics.eve.ecp1.commands.GenericRequestCommand;
import de.ptb.epics.eve.ecp1.commands.IECP1Command;
import de.ptb.epics.eve.ecp1.commands.MeasurementDataCommand;
import de.ptb.epics.eve.ecp1.commands.PauseStatusCommand;
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
 * 	<li>{@link #addEngineVersionListener(IEngineVersionListener)} : engine version</li>
 * 	<li>{@link #addEngineStatusListener(IEngineStatusListener)} : engine status 
 * 		updates as in {@link de.ptb.epics.eve.ecp1.types.EngineStatus}</li>
 * 	<li>{@link #addChainStatusListener(IChainOldStatusListener)} : chain status
 * 		updates as in {@link de.ptb.epics.eve.ecp1.types.ChainStatus}</li>
 * 	<li>{@link #addChainProgressListener(IChainProgressListener)} : chain progress
 * 		updates as in {@link de.ptb.epics.eve.ecp1.types.ChainProgress}</li>
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
	
	private static final Logger LOGGER = Logger.getLogger(
			ECP1Client.class.getName());

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
	private final Queue<IEngineVersionListener> engineVersionListener;
	private final Queue<IChainStatusListener> chainStatusListener;
	private final Queue<IChainProgressListener> chainProgressListener;
	private final Queue<IPauseStatusListener> pauseStatusListener;
	private final Queue<IErrorListener> errorListener;
	private final Queue<IMeasurementDataListener> measurementDataListener;
	private final Queue<IRequestListener> requestListener;
	private final Queue<ISimulationStatusListener> simulationStatusListener;

	private final Queue<IConnectionStateListener> connectionStateListener;

	private final Map<Integer, Request> requestMap;
	private List<String> classNames;
	private Map<Character, Constructor<? extends IECP1Command>> commands;

	private boolean running;
	private boolean simulation;

	/**
	 * Constructor.
	 */
	@SuppressWarnings("unchecked")
	public ECP1Client() {
		this.ecp1Client = this;

		this.inQueue = new ConcurrentLinkedQueue<>();
		this.outQueue = new ConcurrentLinkedQueue<>();

		this.playController = new PlayController(this);
		this.playListController = new PlayListController(this);

		this.engineStatusListener = new ConcurrentLinkedQueue<>();
		this.engineVersionListener = new ConcurrentLinkedQueue<>();
		this.chainStatusListener = 	new ConcurrentLinkedQueue<>();
		this.chainProgressListener = new ConcurrentLinkedQueue<>();
		this.pauseStatusListener = new ConcurrentLinkedQueue<>();
		this.errorListener = new ConcurrentLinkedQueue<>();
		this.measurementDataListener = new ConcurrentLinkedQueue<>();
		this.requestListener = new ConcurrentLinkedQueue<>();
		this.simulationStatusListener = new ConcurrentLinkedQueue<>();
		this.connectionStateListener = new ConcurrentLinkedQueue<>();

		/* ****************************************************** */
		// TODO following code has to be documented/explained
		this.requestMap = new HashMap<>();

		this.classNames = new ArrayList<>();
		final String packageName = "de.ptb.epics.eve.ecp1.commands.";

		this.classNames.add(packageName + "AddToPlayListCommand");
		this.classNames.add(packageName + "AnswerRequestCommand");
		this.classNames.add(packageName + "AutoPlayCommand");
		this.classNames.add(packageName + "BreakCommand");
		this.classNames.add(packageName + "CancelRequestCommand");
		this.classNames.add(packageName + "ChainProgressCommand");
		this.classNames.add(packageName + "ChainStatusCommand");
		this.classNames.add(packageName + "CurrentXMLCommand");
		this.classNames.add(packageName + "EndProgramCommand");
		this.classNames.add(packageName + "EngineStatusCommand");
		this.classNames.add(packageName + "EngineVersionCommand");
		this.classNames.add(packageName + "ErrorCommand");
		this.classNames.add(packageName + "GenericRequestCommand");
		this.classNames.add(packageName + "HaltCommand");
		this.classNames.add(packageName + "LiveDescriptionCommand");
		this.classNames.add(packageName + "MeasurementDataCommand");
		this.classNames.add(packageName + "PauseCommand");
		this.classNames.add(packageName + "PauseStatusCommand");;
		this.classNames.add(packageName + "PlayListCommand");
		this.classNames.add(packageName + "RemoveFromPlayListCommand");
		this.classNames.add(packageName + "ReorderPlayListCommand");
		this.classNames.add(packageName + "RepeatCountCommand");
		this.classNames.add(packageName + "SimulationCommand");
		this.classNames.add(packageName + "StartCommand");
		this.classNames.add(packageName + "StopCommand");

		this.commands = new HashMap<>();
		
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
					LOGGER.error(className + 
							" is not implementing IECP1Command!");
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
					LOGGER.error(className
							+ " and "
							+ this.commands.get(id).getDeclaringClass()
									.getName() + "does have the same id = "
							+ Integer.toHexString(id) + "!");
				}
			} catch (final ClassNotFoundException exception) {
				LOGGER.error("Error: Can't find Class " + className + "!");
			} catch (final SecurityException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (final NoSuchFieldException e) {
				LOGGER.error("Error: Can't find static Field COMMAND_TYPE_ID in "
								+ className + "!");
			} catch (final IllegalArgumentException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (final IllegalAccessException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (final NoSuchMethodException e) {
				LOGGER.error("Error: Can't find Constructor for byte Array as Parameter in "
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
		} catch (IOException e) {
			LOGGER.warn(e.getMessage(), e);
			throw e;
		}

		this.inQueue.clear();
		this.outQueue.clear();

		this.running = true;
		this.simulation = false;

		this.inHandler = new InHandler(this, this.socket.getInputStream(),
				this.inQueue);
		this.outHandler = new OutHandler(this.socket.getOutputStream(),
				this.outQueue);
		this.dispatchHandler = new ECP1Client.InDispatcher();
		
		this.inThread = new Thread(this.inHandler);
		this.inThread.setName("InHandler");
		this.inThread.setUncaughtExceptionHandler(new ThreadExceptionHandler());
		
		this.outThread = new Thread(this.outHandler);
		this.outThread.setName("OutHandler");
		this.outThread.setUncaughtExceptionHandler(new ThreadExceptionHandler());
		
		this.dispatchThread = new Thread(this.dispatchHandler);
		this.dispatchThread.setName("Dispatcher");
		this.dispatchThread.setUncaughtExceptionHandler(
				new ThreadExceptionHandler());

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
			this.simulation = false;
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
	 * @since 1.37
	 */
	public boolean isSimulation() {
		return this.simulation;
	}
	
	/**
	 * Returns the number of elements in the in queue
	 * @return the number of elements in the in queue
	 */
	public int getInQueueSize() {
		return this.inQueue.size();
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
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IEngineVersionListener}.
	 * 
	 * @param engineVersionListener the listener that should be added
	 * @return <code>true</code> if the listener was added, 
	 * 			<code>false</code> otherwise
	 */
	public boolean addEngineVersionListener(
			final IEngineVersionListener engineVersionListener) {
		return this.engineVersionListener.add(engineVersionListener);
	}

	/**
	 * Removes the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IEngineVersionListener}.
	 * 
	 * @param engineVersionListener the listener that should be removed
	 * @return <code>true</code> if the listener was removed, 
	 * 			<code>false</code> otherwise
	 */
	public boolean removeEngineVersionListener(
			final IEngineVersionListener engineVersionListener) {
		return this.engineVersionListener.remove(engineVersionListener);
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
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IChainProgressListener}.
	 * 
	 * @param chainProgressListener the listener that should be added
	 * @return <code>true</code> if the listener was added, 
	 * 			<code>false</code> otherwise
	 */
	public boolean addChainProgressListener(
			final IChainProgressListener chainProgressListener) {
		return this.chainProgressListener.add(chainProgressListener);
	}

	/**
	 * Removes the given 
	 * {@link de.ptb.epics.eve.ecp1.client.interfaces.IChainProgressListener}.
	 * 
	 * @param chainProgressListener the listener that should be removed
	 * @return <code>true</code> if the listener was removed, 
	 * 			<code>false</code> otherwise
	 */
	public boolean removeChainProgressListener(
			final IChainProgressListener chainProgressListener) {
		return this.chainProgressListener.remove(chainProgressListener);
	}

	/**
	 * Adds the given listener
	 * @param pauseStatusListener the listener to be removed
	 * @return <code>true</code> if listener added, <code>false</code> otherwise
	 * @since 1.36
	 */
	public boolean addPauseStatusListener(
			final IPauseStatusListener pauseStatusListener) {
		return this.pauseStatusListener.add(pauseStatusListener);
	}
	
	/**
	 * Removes the given listener
	 * @param pauseStatusListener the listener to be removed
	 * @return <code>true</code> if listener removed, <code>false</code> otherwise
	 * @since 1.36
	 */
	public boolean removePauseStatusListener(
			final IPauseStatusListener pauseStatusListener) {
		return this.pauseStatusListener.remove(pauseStatusListener);
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

	/**
	 * @since 1.37
	 */
	public boolean addSimulationStatusListener(
			final ISimulationStatusListener listener) {
		return this.simulationStatusListener.add(listener);
	}
	
	/**
	 * @since 1.37
	 */
	public boolean removeSimulationStatusListener(
			final ISimulationStatusListener listener) {
		return this.simulationStatusListener.remove(listener);
	}
	
	/**
	 * @author ?
	 * @since 1.0
	 */
	private class InDispatcher implements Runnable {
		@Override public void run() {
			while (running) {
				if (!inQueue.isEmpty()) {
					// long before = System.nanoTime();
					final byte[] packageArray = inQueue.poll();
					// long after = System.nanoTime();
					// LOGGER.debug("Poll Time: " + (after - before));
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(Arrays.toString(packageArray));
					}
					final ByteArrayInputStream byteArrayInputStream = 
							new ByteArrayInputStream(packageArray);
					final DataInputStream dataInputStream = new DataInputStream(
							byteArrayInputStream);
					try {
						dataInputStream.skip(6);
						final char commandId = dataInputStream.readChar();
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
								simulation = engineStatusCommand.isSimulation();
								for (IEngineStatusListener esl : engineStatusListener) {
									esl.engineStatusChanged(
										engineStatusCommand.getEngineStatus(), 
										engineStatusCommand.getXmlName(), 
										engineStatusCommand.getRepeatCount());
								}
								for (ISimulationStatusListener isl : simulationStatusListener) {
									isl.simulationStatusChanged(
										engineStatusCommand.isSimulationButtonEnabled(), 
										engineStatusCommand.isSimulation());
								}
								playListController.reportAutoplay(
										engineStatusCommand.isAutoplay());
							} else if (command instanceof ChainStatusCommand) {
								final ChainStatusCommand chainStatusCommand = 
										(ChainStatusCommand) command;
								for (IChainStatusListener csl : chainStatusListener) {
									csl.chainStatusChanged(chainStatusCommand);
								}
							} else if (command instanceof PauseStatusCommand) {
								final PauseStatusCommand pauseStatus = 
										(PauseStatusCommand) command;
								for (IPauseStatusListener psl : pauseStatusListener) {
									psl.pauseStatusChanged(pauseStatus);
								}
							} else if (command instanceof EngineVersionCommand) {
								final EngineVersionCommand engineVersionCommand = 
										(EngineVersionCommand) command;
								for (IEngineVersionListener evl : engineVersionListener) {
									evl.engineVersionChanged(engineVersionCommand.getVersion(),
											engineVersionCommand.getRevision(),
											engineVersionCommand.getPatchlevel());
								}
							} else if (command instanceof ChainProgressCommand) {
								final ChainProgressCommand chainProgressCommand = 
										(ChainProgressCommand) command;
								for (IChainProgressListener csl : chainProgressListener) {
									csl.chainProgressChanged(chainProgressCommand);
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
								LOGGER.error(
										"Undispatchable Package with the Type "
										+ command.getClass().getName());
							}
						} else {
							LOGGER.error("Unknown package type with the id: "
									+ Integer.toHexString(commandId));
						}
					} catch (final IOException e) {
						LOGGER.error(e.getMessage(), e);
					} catch (final IllegalArgumentException e) {
						LOGGER.error(e.getMessage(), e);
					} catch (final InstantiationException e) {
						LOGGER.error(e.getMessage(), e);
					} catch (final IllegalAccessException e) {
						LOGGER.error(e.getMessage(), e);
					} catch (final InvocationTargetException e) {
						LOGGER.error(e.getMessage(), e);
					}
				} else {
					try {
						Thread.sleep(10);
					} catch (final InterruptedException e) {
						LOGGER.warn(e.getMessage(), e);
					}
				}
			}
		}
	}
}