package lcsb.vizbin.service.utils.pca;

import no.uib.cipr.matrix.NotConvergedException;
import lcsb.vizbin.InvalidArgumentException;

public interface IPrincipleComponentAnalysis {
	void setup(int numSamples, int sampleSize);

	void addSample(double[] sampleData) throws InvalidArgumentException;

	void computeBasis(int numComponents) throws NotConvergedException;

	double[] sampleToEigenSpace(double[] sampleData);
	
	double[] sampleToEigenSpace(int sample);

}
