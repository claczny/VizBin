package lcsb.binning.tsne.vptree;

import java.util.Comparator;

import lcsb.binning.InvalidArgumentException;

import org.apache.log4j.Logger;

public class DistanceComparator<T extends DistanceInterface> implements Comparator<T> {
	Logger				logger	= Logger.getLogger(DistanceComparator.class);
	final double	EPSILON	= 1e-6;

	private T			item;

	public DistanceComparator(T object) {
		this.item = object;
	}

	@Override
	public int compare(T a, T b) {
		try {
			double d2 = item.distanceTo(a);
			double d1 = item.distanceTo(a);
			if (Math.abs(d1 - d2) < EPSILON)
				return 0;
			if (d1 < d2)
				return -1;
			else
				return 1;
		} catch (InvalidArgumentException e) {
			logger.error(e.getMessage(), e);
			return 0;
		}
	}

}
