package lcsb.binning.tsne;

import java.util.ArrayList;
import java.util.List;

import lcsb.binning.InvalidArgumentException;
import lcsb.binning.tsne.vptree.DataPoint;
import lcsb.binning.tsne.vptree.VpTree;

import org.apache.log4j.Logger;

public class TSNE {
	static Logger	logger	= Logger.getLogger(TSNE.class);

	double[]			data;
	int						n;
	int						d;
	double				theta;
	double				perplexity;

	public void run(double[] X, int N, int D, double[] Y, int no_dims, double perplexity, double theta) throws InvalidArgumentException {
		// Determine whether we are using an exact algorithm
		if (N - 1 < 3 * perplexity) {
			throw new InvalidArgumentException("Perplexity too large for the number of data points!\n");
		}
		System.out.print("Using no_dims = " + no_dims + ", perplexity = " + perplexity + ", and theta = " + theta + "\n");
		boolean exact = (theta == .0) ? true : false;

		// Set learning parameters
		float total_time = 0;
		int max_iter = 1000, stop_lying_iter = 250, mom_switch_iter = 250;
		double momentum = .5, final_momentum = .8;
		double eta = 200.0;

		// Allocate some memory
		double dY[] = new double[N * no_dims];
		double uY[] = new double[N * no_dims];
		double gains[] = new double[N * no_dims];
		for (int i = 0; i < N * no_dims; i++)
			uY[i] = .0;
		for (int i = 0; i < N * no_dims; i++)
			gains[i] = 1.0;

		// Normalize input data (to prevent numerical problems)
		logger.debug("Computing input similarities...\n");
		zeroMean(X, N, D);
		double max_X = .0;
		for (int i = 0; i < N * D; i++) {
			if (X[i] > max_X)
				max_X = X[i];
		}
		for (int i = 0; i < N * D; i++)
			X[i] /= max_X;

		// Compute input similarities for exact t-SNE
		double P[] = null;
		List<Integer> row_P = new ArrayList<Integer>();
		List<Integer> col_P = new ArrayList<Integer>();
		List<Double> val_P = new ArrayList<Double>();
		if (exact) {

			// Compute similarities
			P = new double[N * N];
			computeGaussianPerplexity(X, N, D, P, perplexity);

			// Symmetrize input similarities
			logger.debug("Symmetrizing...\n");
			for (int n = 0; n < N; n++) {
				for (int m = n + 1; m < N; m++) {
					P[n * N + m] += P[m * N + n];
					P[m * N + n] = P[n * N + m];
				}
			}
			double sum_P = .0;
			for (int i = 0; i < N * N; i++)
				sum_P += P[i];
			for (int i = 0; i < N * N; i++)
				P[i] /= sum_P;
		}

		// Compute input similarities for approximate t-SNE
		else {

			// Compute asymmetric pairwise input similarities
			computeGaussianPerplexity(X, N, D, row_P, col_P, val_P, perplexity, (int) (3 * perplexity));

			// Symmetrize input similarities
			symmetrizeMatrix(row_P, col_P, val_P, N);
			double sum_P = .0;
			for (int i = 0; i < row_P.get(N); i++)
				sum_P += val_P.get(i);
			for (int i = 0; i < row_P.get(N); i++)
				val_P.set(i, val_P.get(i) / sum_P);
		}

		// Lie about the P-values
		if (exact) {
			for (int i = 0; i < N * N; i++)
				P[i] *= 12.0;
		} else {
			for (int i = 0; i < row_P.get(N); i++)
				val_P.set(i, val_P.get(i) * 12.0);
		}

		// Initialize solution (randomly)
		for (int i = 0; i < N * no_dims; i++)
			Y[i] = randn() * .0001;

		// Perform main training loop
		if (exact)
			logger.debug("Done in %4.2f seconds!\nLearning embedding...\n");
		else
			logger.debug("Done in %4.2f seconds (sparsity = %f)!\nLearning embedding...\n");
		for (int iter = 0; iter < max_iter; iter++) {

			// Compute (approximate) gradient
			if (exact)
				computeExactGradient(P, Y, N, no_dims, dY);
			else
				computeGradient(P, row_P, col_P, val_P, Y, N, no_dims, dY, theta);

			// Update gains
			for (int i = 0; i < N * no_dims; i++)
				gains[i] = (Math.signum(dY[i]) != Math.signum(uY[i])) ? (gains[i] + .2) : (gains[i] * .8);
			for (int i = 0; i < N * no_dims; i++)
				if (gains[i] < .01)
					gains[i] = .01;

			// Perform gradient update (with momentum and gains)
			for (int i = 0; i < N * no_dims; i++)
				uY[i] = momentum * uY[i] - eta * gains[i] * dY[i];
			for (int i = 0; i < N * no_dims; i++)
				Y[i] = Y[i] + uY[i];

			// Make solution zero-mean
			zeroMean(Y, N, no_dims);

			// Stop lying about the P-values after a while, and switch momentum
			if (iter == stop_lying_iter) {
				if (exact) {
					for (int i = 0; i < N * N; i++)
						P[i] /= 12.0;
				} else {
					for (int i = 0; i < row_P.get(N); i++)
						val_P.set(i, val_P.get(i) / 12.0);
				}
			}
			if (iter == mom_switch_iter)
				momentum = final_momentum;

			// Print out progress
			if (iter > 0 && iter % 50 == 0 || iter == max_iter - 1) {
				double C = .0;
				if (exact)
					C = evaluateError(P, Y, N);
				else
					// doing approximate computation here!
					C = evaluateError(row_P, col_P, val_P, Y, N, theta);
				if (iter == 0)
					logger.debug("Iteration %d: error is %f\n");
				else {
					logger.debug("Iteration %d: error is %f (50 iterations in %4.2f seconds)\n");
				}
			}
		}

		logger.debug("Fitting performed in %4.2f seconds.\n");
	}

	public void load_data(double data[], int n, int d, double theta, double perplexity) {
		this.data = data;
		this.n = n;
		this.d = d;
		this.theta = theta;
		this.perplexity = perplexity;
	}

	public void save_data(double[] data, int[] landmarks, double[] costs, int n, int d) {
		logger.debug(n);
		logger.debug(d);
		for (int i = 0; i < n; i++) {
			String res = "";
			for (int j = 0; j < d; j++)
				res += data[i * d + j] + ", ";
			logger.debug(res);
		}
		String res = "";
		for (int i = 0; i < n; i++)
			res += landmarks[i] + ", ";
		logger.debug(res);
		res = "";
		for (int i = 0; i < n; i++)
			res += costs[i] + ", ";
		logger.debug(res);
		logger.debug("Wrote the " + n + " x " + d + " data matrix successfully!\n");
	}

	public void symmetrizeMatrix(List<Integer> _row_P, List<Integer> _col_P, List<Double> _val_P, int N) { // should

		// Get sparse matrix
		int row_P[] = new int[_row_P.size()];
		for (int i = 0; i < row_P.length; i++)
			row_P[i] = _row_P.get(i);

		int col_P[] = new int[_col_P.size()];
		for (int i = 0; i < col_P.length; i++)
			col_P[i] = _col_P.get(i);

		double val_P[] = new double[_val_P.size()];
		for (int i = 0; i < val_P.length; i++)
			val_P[i] = _val_P.get(i);

		// Count number of elements and row counts of symmetric matrix
		int row_counts[] = new int[N];
		for (int n = 0; n < N; n++) {
			for (int i = row_P[n]; i < row_P[n + 1]; i++) {

				// Check whether element (col_P[i], n) is present
				boolean present = false;
				for (int m = row_P[col_P[i]]; m < row_P[col_P[i] + 1]; m++) {
					if (col_P[m] == n)
						present = true;
				}
				if (present)
					row_counts[n]++;
				else {
					row_counts[n]++;
					row_counts[col_P[i]]++;
				}
			}
		}
		int no_elem = 0;
		for (int n = 0; n < N; n++)
			no_elem += row_counts[n];

		// Allocate memory for symmetrized matrix
		int sym_row_P[] = new int[N + 1];
		int sym_col_P[] = new int[no_elem];
		double sym_val_P[] = new double[no_elem];

		// Construct new row indices for symmetric matrix
		sym_row_P[0] = 0;
		for (int n = 0; n < N; n++)
			sym_row_P[n + 1] = sym_row_P[n] + row_counts[n];

		// Fill the result matrix
		int offset[] = new int[N];
		for (int n = 0; n < N; n++) {
			for (int i = row_P[n]; i < row_P[n + 1]; i++) { // considering element(n,
																											// col_P[i])

				// Check whether element (col_P[i], n) is present
				boolean present = false;
				for (int m = row_P[col_P[i]]; m < row_P[col_P[i] + 1]; m++) {
					if (col_P[m] == n) {
						present = true;
						if (n <= col_P[i]) { // make sure we do not add elements twice
							sym_col_P[sym_row_P[n] + offset[n]] = col_P[i];
							sym_col_P[sym_row_P[col_P[i]] + offset[col_P[i]]] = n;
							sym_val_P[sym_row_P[n] + offset[n]] = val_P[i] + val_P[m];
							sym_val_P[sym_row_P[col_P[i]] + offset[col_P[i]]] = val_P[i] + val_P[m];
						}
					}
				}

				// If (col_P[i], n) is not present, there is no addition involved
				if (!present) {
					sym_col_P[sym_row_P[n] + offset[n]] = col_P[i];
					sym_col_P[sym_row_P[col_P[i]] + offset[col_P[i]]] = n;
					sym_val_P[sym_row_P[n] + offset[n]] = val_P[i];
					sym_val_P[sym_row_P[col_P[i]] + offset[col_P[i]]] = val_P[i];
				}

				// Update offsets
				if (!present || (present && n <= col_P[i])) {
					offset[n]++;
					if (col_P[i] != n)
						offset[col_P[i]]++;
				}
			}
		}

		// Divide the result by two
		for (int i = 0; i < no_elem; i++)
			sym_val_P[i] /= 2.0;

		// Return symmetrized matrices
		_row_P.clear();
		for (int i = 0; i < sym_row_P.length; i++)
			_row_P.add(sym_row_P[i]);

		_col_P.clear();
		for (int i = 0; i < sym_col_P.length; i++)
			_col_P.add(sym_col_P[i]);

		_val_P.clear();
		for (int i = 0; i < sym_val_P.length; i++)
			_val_P.add(sym_val_P[i]);
	}

	private void computeGradient(double[] P, List<Integer> inp_row_P, List<Integer> inp_col_P, List<Double> inp_val_P, double[] Y, int N, int D, double[] dC,
			double theta) {

		// Construct quadtree on current map
		QuadTree tree = new QuadTree(Y, N);

		// Compute all terms required for t-SNE gradient
		double sum_Q = .0;
		double pos_f[] = new double[N * D];
		double neg_f[] = new double[N * D];
		tree.computeEdgeForces(inp_row_P, inp_col_P, inp_val_P, N, pos_f);
		for (int n = 0; n < N; n++) {
			double tmp[] = new double[D];
			sum_Q += tree.computeNonEdgeForces(n, theta, tmp);
			for (int i = 0; i < D; i++)
				neg_f[n * D + i] = tmp[i];
		}

		// Compute final t-SNE gradient
		for (int i = 0; i < N * D; i++) {
			dC[i] = pos_f[i] - (neg_f[i] / sum_Q);
		}
	}

	private void computeExactGradient(double[] P, double[] Y, int N, int D, double[] dC) {

		// Make sure the current gradient contains zeros
		for (int i = 0; i < N * D; i++)
			dC[i] = 0.0;

		// Compute the squared Euclidean distance matrix
		double DD[] = new double[N * N];
		computeSquaredEuclideanDistance(Y, N, D, DD);

		// Compute Q-matrix and normalization sum
		double Q[] = new double[N * N];
		double sum_Q = .0;
		for (int n = 0; n < N; n++) {
			for (int m = 0; m < N; m++) {
				if (n != m) {
					Q[n * N + m] = 1 / (1 + DD[n * N + m]);
					sum_Q += Q[n * N + m];
				}
			}
		}

		// Perform the computation of the gradient
		for (int n = 0; n < N; n++) {
			for (int m = 0; m < N; m++) {
				if (n != m) {
					double mult = (P[n * N + m] - (Q[n * N + m] / sum_Q)) * Q[n * N + m];
					for (int d = 0; d < D; d++) {
						dC[n * D + d] += (Y[n * D + d] - Y[m * D + d]) * mult;
					}
				}
			}
		}

	}

	private double evaluateError(double[] P, double[] Y, int N) {
		// Compute the squared Euclidean distance matrix
		double DD[] = new double[N * N];
		double Q[] = new double[N * N];
		computeSquaredEuclideanDistance(Y, N, 2, DD);

		// Compute Q-matrix and normalization sum
		double sum_Q = Double.MIN_VALUE;
		for (int n = 0; n < N; n++) {
			for (int m = 0; m < N; m++) {
				if (n != m) {
					Q[n * N + m] = 1 / (1 + DD[n * N + m]);
					sum_Q += Q[n * N + m];
				} else
					Q[n * N + m] = Double.MIN_VALUE;
			}
		}
		for (int i = 0; i < N * N; i++)
			Q[i] /= sum_Q;

		// Sum t-SNE error
		double C = .0;
		for (int n = 0; n < N; n++) {
			for (int m = 0; m < N; m++) {
				C += P[n * N + m] * Math.log((P[n * N + m] + 1e-9) / (Q[n * N + m] + 1e-9));
			}
		}

		// Clean up memory
		return C;
	}

	private double evaluateError(List<Integer> row_P, List<Integer> col_P, List<Double> val_P, double[] Y, int N, double theta) {
		// Get estimate of normalization term
		int QT_NO_DIMS = 2;
		QuadTree tree = new QuadTree(Y, N);
		double buff[] = new double[QT_NO_DIMS];
		for (int i = 0; i < QT_NO_DIMS; i++)
			buff[i] = 0;
		double sum_Q = .0;
		for (int n = 0; n < N; n++)
			sum_Q += tree.computeNonEdgeForces(n, theta, buff);

		// Loop over all edges to compute t-SNE error
		int ind1, ind2;
		double C = .0, Q;
		for (int n = 0; n < N; n++) {
			ind1 = n * QT_NO_DIMS;
			for (int i = row_P.get(n); i < row_P.get(n + 1); i++) {
				Q = .0;
				ind2 = col_P.get(i) * QT_NO_DIMS;
				for (int d = 0; d < QT_NO_DIMS; d++)
					buff[d] = Y[ind1 + d];
				for (int d = 0; d < QT_NO_DIMS; d++)
					buff[d] -= Y[ind2 + d];
				for (int d = 0; d < QT_NO_DIMS; d++)
					Q += buff[d] * buff[d];
				Q = (1.0 / (1.0 + Q)) / sum_Q;
				C += val_P.get(i) * Math.log((val_P.get(i) + Float.MIN_VALUE) / (Q + Float.MIN_VALUE));
			}
		}
		return C;
	}

	private void zeroMean(double[] X, int N, int D) {
		// Compute data mean
		double mean[] = new double[D];
		for (int n = 0; n < N; n++) {
			for (int d = 0; d < D; d++) {
				mean[d] += X[n * D + d];
			}
		}
		for (int d = 0; d < D; d++) {
			mean[d] /= (double) N;
		}

		// Subtract data mean
		for (int n = 0; n < N; n++) {
			for (int d = 0; d < D; d++) {
				X[n * D + d] -= mean[d];
			}
		}
	}

	private void computeGaussianPerplexity(double[] X, int N, int D, double[] P, double perplexity) {

		// Compute the squared Euclidean distance matrix
		double DD[] = new double[N * N];
		computeSquaredEuclideanDistance(X, N, D, DD);

		// Compute the Gaussian kernel row by row
		for (int n = 0; n < N; n++) {

			// Initialize some variables
			boolean found = false;
			double beta = 1.0;
			double min_beta = -Double.MAX_VALUE;
			double max_beta = Double.MAX_VALUE;
			double tol = 1e-5;
			double sum_P = Double.MIN_VALUE;

			// Iterate until we found a good perplexity
			int iter = 0;
			while (!found && iter < 200) {

				// Compute Gaussian kernel row
				for (int m = 0; m < N; m++)
					P[n * N + m] = Math.exp(-beta * DD[n * N + m]);
				P[n * N + n] = Double.MIN_VALUE;

				// Compute entropy of current row
				sum_P = Double.MIN_VALUE;
				for (int m = 0; m < N; m++)
					sum_P += P[n * N + m];
				double H = 0.0;
				for (int m = 0; m < N; m++)
					H += beta * (DD[n * N + m] * P[n * N + m]);
				H = (H / sum_P) + Math.log(sum_P);

				// Evaluate whether the entropy is within the tolerance level
				double Hdiff = H - Math.log(perplexity);
				if (Hdiff < tol && -Hdiff < tol) {
					found = true;
				} else {
					if (Hdiff > 0) {
						min_beta = beta;
						if (max_beta == Double.MAX_VALUE || max_beta == -Double.MAX_VALUE)
							beta *= 2.0;
						else
							beta = (beta + max_beta) / 2.0;
					} else {
						max_beta = beta;
						if (min_beta == -Double.MAX_VALUE || min_beta == Double.MAX_VALUE)
							beta /= 2.0;
						else
							beta = (beta + min_beta) / 2.0;
					}
				}

				// Update iteration counter
				iter++;
			}

			// Row normalize P
			for (int m = 0; m < N; m++)
				P[n * N + m] /= sum_P;
		}

	}

	private void computeGaussianPerplexity(double[] X, int N, int D, List<Integer> _row_P, List<Integer> _col_P, List<Double> _val_P, double perplexity, int K)
			throws InvalidArgumentException {

		if (perplexity > K)
			logger.debug("Perplexity should be lower than K!\n");

		// Allocate the memory we need
		int row_P[] = new int[N + 1];
		int col_P[] = new int[N * K];
		double val_P[] = new double[N * K];

		double cur_P[] = new double[N - 1];
		row_P[0] = 0;
		for (int n = 0; n < N; n++)
			row_P[n + 1] = row_P[n] + K;

		// Build ball tree on data set
		VpTree<DataPoint> tree = new VpTree<DataPoint>();
		List<DataPoint> obj_X = new ArrayList<DataPoint>();
		for (int n = 0; n < N; n++)
			obj_X.add(new DataPoint(D, n, n * D, X));
		tree.create(obj_X);

		// Loop over all points to find nearest neighbors
		logger.debug("Building tree...\n");
		List<DataPoint> indices = new ArrayList<DataPoint>();
		List<Double> distances = new ArrayList<Double>();
		for (int n = 0; n < N; n++) {

			if (n % 10000 == 0)
				logger.debug(" - point " + n + " of " + N);

			// Find nearest neighbors
			indices.clear();
			distances.clear();
			tree.search(obj_X.get(n), K + 1, indices, distances);

			// Initialize some variables for binary search
			boolean found = false;
			double beta = 1.0;
			double min_beta = -Double.MAX_VALUE;
			double max_beta = Double.MAX_VALUE;
			double tol = 1e-5;

			// Iterate until we found a good perplexity
			int iter = 0;
			double sum_P = 0;
			while (!found && iter < 200) {

				// Compute Gaussian kernel row
				for (int m = 0; m < K; m++)
					cur_P[m] = Math.exp(-beta * distances.get(m + 1));

				// Compute entropy of current row
				sum_P = Double.MIN_VALUE;
				for (int m = 0; m < K; m++)
					sum_P += cur_P[m];
				double H = .0;
				for (int m = 0; m < K; m++)
					H += beta * (distances.get(m + 1) * cur_P[m]);
				H = (H / sum_P) + Math.log(sum_P);

				// Evaluate whether the entropy is within the tolerance level
				double Hdiff = H - Math.log(perplexity);
				if (Hdiff < tol && -Hdiff < tol) {
					found = true;
				} else {
					if (Hdiff > 0) {
						min_beta = beta;
						if (max_beta == Double.MAX_VALUE || max_beta == -Double.MAX_VALUE)
							beta *= 2.0;
						else
							beta = (beta + max_beta) / 2.0;
					} else {
						max_beta = beta;
						if (min_beta == -Double.MAX_VALUE || min_beta == Double.MAX_VALUE)
							beta /= 2.0;
						else
							beta = (beta + min_beta) / 2.0;
					}
				}

				// Update iteration counter
				iter++;
			}

			// Row-normalize current row of P and store in matrix
			for (int m = 0; m < K; m++)
				cur_P[m] /= sum_P;
			for (int m = 0; m < K; m++) {
				col_P[row_P[n] + m] = indices.get(m + 1).index();
				val_P[row_P[n] + m] = cur_P[m];
			}
		}

		// Clean up memory
		obj_X.clear();

		_row_P.clear();
		for (int i = 0; i < row_P.length; i++)
			_row_P.add(row_P[i]);
		_col_P.clear();
		for (int i = 0; i < col_P.length; i++)
			_col_P.add(col_P[i]);
		_val_P.clear();
		for (int i = 0; i < val_P.length; i++)
			_val_P.add(val_P[i]);

	}

	private void computeGaussianPerplexity(double[] X, int N, int D, List<Integer> _row_P, List<Integer> _col_P, List<Double> _val_P, double perplexity,
			double threshold) {

		// Allocate some memory we need for computations
		double buff[] = new double[D];
		double DD[] = new double[N];
		double cur_P[] = new double[N];

		// Compute the Gaussian kernel row by row (to find number of elements in
		// sparse P)
		int total_count = 0;
		for (int n = 0; n < N; n++) {

			// Compute the squared Euclidean distance matrix
			for (int m = 0; m < N; m++) {
				for (int d = 0; d < D; d++)
					buff[d] = X[n * D + d];
				for (int d = 0; d < D; d++)
					buff[d] -= X[m * D + d];
				DD[m] = .0;
				for (int d = 0; d < D; d++)
					DD[m] += buff[d] * buff[d];
			}

			// Initialize some variables
			boolean found = false;
			double beta = 1.0;
			double min_beta = -Double.MAX_VALUE;
			double max_beta = Double.MAX_VALUE;
			double tol = 1e-5;

			// Iterate until we found a good perplexity
			int iter = 0;
			double sum_P = 0;
			while (!found && iter < 200) {

				// Compute Gaussian kernel row
				for (int m = 0; m < N; m++)
					cur_P[m] = Math.exp(-beta * DD[m]);
				cur_P[n] = Double.MIN_VALUE;

				// Compute entropy of current row
				sum_P = Double.MIN_VALUE;
				for (int m = 0; m < N; m++)
					sum_P += cur_P[m];
				double H = 0.0;
				for (int m = 0; m < N; m++)
					H += beta * (DD[m] * cur_P[m]);
				H = (H / sum_P) + Math.log(sum_P);

				// Evaluate whether the entropy is within the tolerance level
				double Hdiff = H - Math.log(perplexity);
				if (Hdiff < tol && -Hdiff < tol) {
					found = true;
				} else {
					if (Hdiff > 0) {
						min_beta = beta;
						if (max_beta == Double.MAX_VALUE || max_beta == -Double.MAX_VALUE)
							beta *= 2.0;
						else
							beta = (beta + max_beta) / 2.0;
					} else {
						max_beta = beta;
						if (min_beta == -Double.MAX_VALUE || min_beta == Double.MAX_VALUE)
							beta /= 2.0;
						else
							beta = (beta + min_beta) / 2.0;
					}
				}

				// Update iteration counter
				iter++;
			}

			// Row-normalize and threshold current row of P
			for (int m = 0; m < N; m++)
				cur_P[m] /= sum_P;
			for (int m = 0; m < N; m++) {
				if (cur_P[m] > threshold / (double) N)
					total_count++;
			}
		}

		// Allocate the memory we need
		int row_P[] = new int[N + 1];
		int col_P[] = new int[total_count];
		double val_P[] = new double[total_count];
		row_P[0] = 0;

		// Compute the Gaussian kernel row by row (this time, store the results)
		int count = 0;
		for (int n = 0; n < N; n++) {

			// Compute the squared Euclidean distance matrix
			for (int m = 0; m < N; m++) {
				for (int d = 0; d < D; d++)
					buff[d] = X[n * D + d];
				for (int d = 0; d < D; d++)
					buff[d] -= X[m * D + d];
				DD[m] = .0;
				for (int d = 0; d < D; d++)
					DD[m] += buff[d] * buff[d];
			}

			// Initialize some variables
			boolean found = false;
			double beta = 1.0;
			double min_beta = -Double.MAX_VALUE;
			double max_beta = Double.MAX_VALUE;
			double tol = 1e-5;

			// Iterate until we found a good perplexity
			int iter = 0;
			double sum_P = 0;
			while (!found && iter < 200) {

				// Compute Gaussian kernel row
				for (int m = 0; m < N; m++)
					cur_P[m] = Math.exp(-beta * DD[m]);
				cur_P[n] = Double.MIN_VALUE;

				// Compute entropy of current row
				sum_P = Double.MIN_VALUE;
				for (int m = 0; m < N; m++)
					sum_P += cur_P[m];
				double H = 0.0;
				for (int m = 0; m < N; m++)
					H += beta * (DD[m] * cur_P[m]);
				H = (H / sum_P) + Math.log(sum_P);

				// Evaluate whether the entropy is within the tolerance level
				double Hdiff = H - Math.log(perplexity);
				if (Hdiff < tol && -Hdiff < tol) {
					found = true;
				} else {
					if (Hdiff > 0) {
						min_beta = beta;
						if (max_beta == Double.MAX_VALUE || max_beta == -Double.MAX_VALUE)
							beta *= 2.0;
						else
							beta = (beta + max_beta) / 2.0;
					} else {
						max_beta = beta;
						if (min_beta == -Double.MAX_VALUE || min_beta == Double.MAX_VALUE)
							beta /= 2.0;
						else
							beta = (beta + min_beta) / 2.0;
					}
				}

				// Update iteration counter
				iter++;
			}

			// Row-normalize and threshold current row of P
			for (int m = 0; m < N; m++)
				cur_P[m] /= sum_P;
			for (int m = 0; m < N; m++) {
				if (cur_P[m] > threshold / (double) N) {
					col_P[count] = m;
					val_P[count] = cur_P[m];
					count++;
				}
			}
			row_P[n + 1] = count;
		}

		// Clean up memory
		_row_P.clear();
		for (int i = 0; i < row_P.length; i++)
			_row_P.add(row_P[i]);
		_col_P.clear();
		for (int i = 0; i < col_P.length; i++)
			_col_P.add(col_P[i]);
		_val_P.clear();
		for (int i = 0; i < val_P.length; i++)
			_val_P.add(val_P[i]);
	}

	private void computeSquaredEuclideanDistance(double[] X, int N, int D, double[] DD) {
		double dataSums[] = new double[N];
		for (int n = 0; n < N; n++) {
			for (int d = 0; d < D; d++) {
				dataSums[n] += (X[n * D + d] * X[n * D + d]);
			}
		}
		for (int n = 0; n < N; n++) {
			for (int m = 0; m < N; m++) {
				DD[n * N + m] = dataSums[n] + dataSums[m];
			}
		}
		
		//cblas_dgemm(CblasColMajor, CblasTrans, CblasNoTrans, N, N, D, -2.0, X, D, X, D, 1.0, DD, N);
	}

	private double randn() {
		double x, y, radius;
		do {
			x = 2 * (Math.random() + 1) - 1;
			y = 2 * (Math.random() + 1) - 1;
			radius = (x * x) + (y * y);
		} while ((radius >= 1.0) || (radius == 0.0));
		radius = Math.sqrt(-2 * Math.log(radius) / radius);
		x *= radius;
		y *= radius;
		return x;
	}
}