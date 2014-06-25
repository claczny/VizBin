package lcsb.binning.tsne.vptree;

public class HeapItem {
	HeapItem(int index, double dist) {
		this.index = index;
		this.dist = dist;
	}

	int			index;
	double	dist;

	boolean lessThan(HeapItem o) {
		return dist < o.dist;
	}
}
