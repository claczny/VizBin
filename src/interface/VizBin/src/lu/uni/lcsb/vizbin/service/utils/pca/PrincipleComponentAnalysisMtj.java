package lu.uni.lcsb.vizbin.service.utils.pca;

import org.apache.log4j.Logger;

import lu.uni.lcsb.vizbin.InvalidArgumentException;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.SVD;

public class PrincipleComponentAnalysisMtj implements IPrincipleComponentAnalysis {
	Logger			logger				= Logger.getLogger(PrincipleComponentAnalysisMtj.class);

	private SVD	svd						= null;

	int					sample				= 0;

	int					sampleSize		= -1;
	int					numSamples		= -1;
	int					numComponents	= -1;

	SVD					s;
	DenseMatrix	A;
	DenseMatrix	Y;
	Matrix			Vred;
	double			normFactor;

	double[]		mean;
	Matrix			SCORES				= null;

	public PrincipleComponentAnalysisMtj() {
	}

	@Override
	public void setup(int numSamples, int sampleSize) {
		svd = new SVD(numSamples, sampleSize, true);
		A = new DenseMatrix(numSamples, sampleSize);
		sample = 0;
		this.sampleSize = sampleSize;
		this.numSamples = numSamples;
	}

	@Override
	public void addSample(double[] sampleData) throws InvalidArgumentException {
		if (sampleData.length != sampleSize)
			throw new InvalidArgumentException("Invalid row length. Expected " + sampleSize + ", but " + sampleData.length + " appeard.");
		for (int i = 0; i < sampleSize; i++)
			A.set(sample, i, sampleData[i]);
		sample++;
	}

	public void computeBasis(int numComponents) throws NotConvergedException {
		if (numComponents > sampleSize)
			throw new IllegalArgumentException("More components requested that the data's length.");
		if (sample != numSamples)
			throw new IllegalArgumentException("Not all the data has been added");
		if (numComponents > sample)
			throw new IllegalArgumentException("More data needed to compute the desired number of components");

		this.numComponents = numComponents;

		mean = new double[sampleSize];
		// compute the mean of all the samples
		for (int i = 0; i < numSamples; i++) {
			for (int j = 0; j < mean.length; j++) {
				mean[j] += A.get(i,j);
			}
		}
		for (int j = 0; j < mean.length; j++) {
			mean[j] /= numSamples;
		}

		normFactor = Math.sqrt(sampleSize - 1);

		// subtract the mean from the original data
		double centered_a_ij = 0.0;
		for (int i = 0; i < numSamples; i++) {
			for (int j = 0; j < sampleSize; j++) {
				centered_a_ij = A.get(i,  j) - mean[j];
				centered_a_ij /= normFactor;
				A.set(i, j, centered_a_ij);
			}
		}
		
		//Y = new DenseMatrix(A);
		//logger.debug("Done creating a copy of A and storing it as Y.");

		s = svd.factor(A);

		DenseMatrix Vt = s.getVt();
		DenseMatrix V = new DenseMatrix(Vt);
		V.transpose(); // Possible as V is square matrix

		// Get submatrix
		int[] chosenRows = Matrices.index(0, V.numRows());
		int[] chosenColumns = Matrices.index(0, numComponents);

		Vred = Matrices.getSubMatrix(V, chosenRows, chosenColumns);
	}

	@Override
	public double[] sampleToEigenSpace(double[] sampleData) {
		double[][] m = new double[1][sampleData.length];
		for (int i = 0; i < sampleData.length; i++)
			m[0][i] = (sampleData[i] - mean[i]) / Math.sqrt(sampleSize - 1);
		DenseMatrix matrix = new DenseMatrix(m);

		Matrix SCORES = new DenseMatrix(1, Vred.numColumns());
		matrix.mult(Math.sqrt(sampleSize - 1), Vred, SCORES);

		double result[] = new double[numComponents];
		for (int i = 0; i < numComponents; i++) {
			result[i] = SCORES.get(0, i);
		}
		return result;
	}

	/*
	@Override
	public double[] sampleToEigenSpace(int sample) {
		if (SCORES == null) {
			SCORES = new DenseMatrix(A.numRows(), Vred.numColumns());
			Y.mult(normFactor, Vred, SCORES);
		}
		double result[] = new double[numComponents];
		for (int i = 0; i < numComponents; i++) {
			result[i] = SCORES.get(sample, i);
		}
		return result;
	}
	*/

}