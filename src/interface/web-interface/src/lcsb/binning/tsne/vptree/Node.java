package lcsb.binning.tsne.vptree;

public class Node {
	int			index;			// index of point in node
	double	threshold;	// radius(?)
	Node		left;			// points closer by than threshold
	Node		right;			// points farther away than threshold

	public Node() {
		index = 0;
		threshold = 0;
		left = null;
		right = null;
	}

}
