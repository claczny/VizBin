package lcsb.binning.tsne.vptree;

import lcsb.binning.InvalidArgumentException;

public interface DistanceInterface {
	double distanceTo(DistanceInterface other) throws InvalidArgumentException;
}
