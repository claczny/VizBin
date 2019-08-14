package lu.uni.lcsb.vizbin.service.utils.pca;

import lu.uni.lcsb.vizbin.InvalidArgumentException;
import no.uib.cipr.matrix.NotConvergedException;

public interface IPrincipleComponentAnalysis {
	void setup(int numSamples, int sampleSize);

	void addSample(double[] sampleData) throws InvalidArgumentException;

	void computeBasis(int numComponents) throws NotConvergedException;

	double[] sampleToEigenSpace(double[] sampleData);
	
	//double[] sampleToEigenSpace(int sample);

}
