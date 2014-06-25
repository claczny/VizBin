package lcsb.binning.tsne.vptree;

import lcsb.binning.InvalidArgumentException;

public class DataPoint implements DistanceInterface {

	protected int			_D;
	protected int			_ind;
	protected double	_x[];

	public DataPoint() {
		_D = 1;
		_ind = -1;
		_x = null;
	}

	public DataPoint(int D, int ind, int startPoint, double[] x) {
		_D = D;
		_ind = ind;
		_x = new double[_D];
		for (int d = 0; d < _D; d++)
			_x[d] = x[startPoint+d];
	}

	public DataPoint(DataPoint other) { // this makes a deep copy -- should not
																			// free anything
		if (this != other) {
			_D = other.dimensionality();
			_ind = other.index();
			_x = new double[_D];
			for (int d = 0; d < _D; d++)
				_x[d] = other.x(d);
		}
	}

	public int index() {
		return _ind;
	}

	int dimensionality() {
		return _D;
	}

	double x(int d) {
		return _x[d];
	}

	double euclidean_distance(DataPoint t2) {
		double dd = .0;
		for (int d = 0; d < dimensionality(); d++)
			dd += (x(d) - t2.x(d)) * (x(d) - t2.x(d));
		return dd;
	}

	@Override
	public double distanceTo(DistanceInterface other) throws InvalidArgumentException {
		if (other instanceof DataPoint) {
			return euclidean_distance((DataPoint)other);
		} else
			throw new InvalidArgumentException("Unknown class type: " + other.getClass());
	}
}
