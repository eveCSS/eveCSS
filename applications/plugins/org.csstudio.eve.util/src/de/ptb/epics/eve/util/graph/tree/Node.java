package de.ptb.epics.eve.util.graph.tree;

import java.util.List;

/**
 * Binary Tree Implementation.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class Node<T> {
	private final Node<T> left;
	private final T root;
	private final Node<T> right;
	
	/**
	 * Constructs a new Node.
	 * 
	 * @param root the root element
	 * @param left the left subtree
	 * @param right the right subtree
	 */
	public Node(final T root, final Node<T> left, final Node<T> right) {
		this.root = root;
		this.left = left;
		this.right = right;
	}
	
	/**
	 * Returns the left subtree.
	 * 
	 * @return the left subtree
	 */
	public Node<T> getLeft() {
		return left;
	}

	/**
	 * Returns the root node.
	 * 
	 * @return the root node
	 */
	public T getRoot() {
		return root;
	}

	/**
	 * Returns the right subtree.
	 * 
	 * @return the right subtree
	 */
	public Node<T> getRight() {
		return right;
	}
	
	/**
	 * Returns whether this node is a leaf.
	 * 
	 * @return <code>true</code> if this node is a leaf, <code>false</code> otherwise
	 */
	public boolean isLeaf() {
		return this.left == null && this.right == null;
	}
	
	/**
	 * Adds the root nodes in pre-order to the given list.
	 *  
	 * @param list the list the root nodes should be added to (in pre-order)
	 */
	public void preOrder(List<T> list) {
		list.add(root);
		if (left != null) left.preOrder(list);
		if (right != null) right.preOrder(list);
	}
}