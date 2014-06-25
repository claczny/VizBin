package lcsb.binning.tsne.vptree;

import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

import lcsb.binning.InvalidArgumentException;
import lcsb.binning.tsne.CollectionUtils;

public class VpTree<T extends DistanceInterface> {
	List<T>	_items;
	double	_tau;
	Node		_root;

	// Default constructor
	public VpTree() {
		_root = null;
	}

	// Function to create a new VpTree from data
	public void create(List<T> items) throws InvalidArgumentException {
		_items = items;
		_root = buildFromPoints(0, items.size());
	}

	// Function that uses the tree to find the k nearest neighbors of target
	public void search(T target, int k, List<T> results, List<Double> distances) throws InvalidArgumentException {

		// Use a priority queue to store intermediate results on
		PriorityQueue<HeapItem> heap = new PriorityQueue<HeapItem>();

		// Variable that tracks the distance to the farthest point in our results
		_tau = Double.MAX_VALUE;

		// Perform the searcg
		search(_root, target, k, heap);

		// Gather final results
		results.clear();
		distances.clear();
		while (heap.size() != 0) {

			results.add(_items.get(heap.peek().index));
			distances.add(heap.peek().dist);
			heap.remove();
		}

		// Results are in reverse order
		Collections.reverse(results);
		Collections.reverse(distances);
	}

	Node buildFromPoints(int lower, int upper) throws InvalidArgumentException {
		if (upper == lower) { // indicates that we're done here!
			return null;
		}

		// Lower index is center of current node
		Node node = new Node();
		node.index = lower;

		if (upper - lower > 1) { // if we did not arrive at leaf yet

			// Choose an arbitrary point and move it to the start
			int i = (int) (Math.random() * (upper - lower - 1)) + lower;
			Collections.swap(_items, lower, i);

			// Partition around the median distance
			int median = (upper + lower) / 2;
			CollectionUtils.nthElement(lower + 1, median, upper, _items, new DistanceComparator<T>(_items.get(lower)));

			// Threshold of the new node will be the distance to the median
			node.threshold = _items.get(lower).distanceTo(_items.get(median));

			// Recursively build tree
			node.index = lower;
			node.left = buildFromPoints(lower + 1, median);
			node.right = buildFromPoints(median, upper);
		}

		// Return result
		return node;
	}

	// Helper function that searches the tree
	void search(Node node, T target, int k, PriorityQueue<HeapItem> heap) throws InvalidArgumentException {
		if (node == null)
			return; // indicates that we're done here

		// Compute distance between target and current node
		double dist = _items.get(node.index).distanceTo(target);

		// If current node within radius tau
		if (dist < _tau) {
			if (heap.size() == k)
				heap.remove(); // remove furthest node from result list (if we already
												// have k results)
			heap.add(new HeapItem(node.index, dist)); // add current node to result
																								// list
			if (heap.size() == k)
				_tau = heap.peek().dist; // update value of tau (farthest point in
																	// result list)
		}

		// Return if we arrived at a leaf
		if (node.left == null && node.right == null) {
			return;
		}

		// If the target lies within the radius of ball
		if (dist < node.threshold) {
			if (dist - _tau <= node.threshold) { // if there can still be neighbors
																						// inside the ball, recursively
																						// search left child first
				search(node.left, target, k, heap);
			}

			if (dist + _tau >= node.threshold) { // if there can still be neighbors
																						// outside the ball, recursively
																						// search right child
				search(node.right, target, k, heap);
			}

			// If the target lies outsize the radius of the ball
		} else {
			if (dist + _tau >= node.threshold) { // if there can still be neighbors
																						// outside the ball, recursively
																						// search right child first
				search(node.right, target, k, heap);
			}

			if (dist - _tau <= node.threshold) { // if there can still be neighbors
																						// inside the ball, recursively
																						// search left child
				search(node.left, target, k, heap);
			}
		}
	}
}
