package de.ptb.epics.eve.util.graph;

/**
 * (Generic) Vertex implementation.
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class VertexImpl<T> implements Vertex<T> {
	private T content;
	private int discoveryTime;
	private int finishTime;
	private VisitState state;
	private Vertex<T> predecessor;
	
	/**
	 * Constructs a vertex implementation.
	 * @param content the domain specific content
	 */
	public VertexImpl(T content) {
		this.content = content;
		this.discoveryTime = 0;
		this.finishTime = 0;
		this.state = VisitState.UNEXPLORED;
		this.predecessor = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getContent() {
		return this.content;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void discovered(int time) {
		this.discoveryTime = time;
		this.state = VisitState.DISCOVERED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDiscoveryTime() {
		return this.discoveryTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finished(int time) {
		this.finishTime = time;
		this.state = VisitState.FINISHED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getFinishTime() {
		return this.finishTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setState(VisitState state) {
		this.state = state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisitState getState() {
		return this.state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Vertex<T> getPredecessor() {
		return this.predecessor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPredecessor(Vertex<T> v) {
		this.predecessor = v;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Vertex<T> other) {
		return this.finishTime - other.getFinishTime();
	}
}