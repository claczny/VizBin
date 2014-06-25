package lcsb.binning.service.utils;

import java.io.File;
import java.io.IOException;

import lcsb.binning.InvalidArgumentException;
import lcsb.binning.UnhandledOSException;
import lcsb.binning.data.DataSet;
import lcsb.binning.data.Sequence;
import lcsb.binning.service.DataSetFactory;

import org.apache.log4j.Logger;

public class DataSetUtils {
	static Logger		logger				= Logger.getLogger(DataSetUtils.class);

	static String		tsneComand		= "./bh_tsne";

	static Integer	usedVal[][]		= new Integer[256][];

	static int			alphabetSize	= 4;

	public static void createKmers(DataSet dataSet, int k, boolean mergeRevComp) throws InvalidArgumentException {
		for (Sequence sequence : dataSet.getSequences()) {
			if (sequence.getKmers(k) == null) {
				Integer[] kmers = createKmers(sequence, k, mergeRevComp);
				sequence.setKmers(k, kmers);
			}
		}
	}

	protected static void createMergedKmerTabl(int k) {
		logger.debug("Creating usedVal for k=" + k);
		int maxVal = (int) Math.pow(alphabetSize, k);

		Integer tmpUsedVal[] = new Integer[maxVal];

		for (int i = 0; i < maxVal; i++) {
			int val = i;
			int revVal = getRevVal(val, k);
			tmpUsedVal[i] = Math.min(val, revVal);
		}
		usedVal[k] = tmpUsedVal;
	}

	private static int getRevVal(int val, int k) {
		int result = 0;
		for (int i = 0; i < k; i++) {
			result = result * alphabetSize + (alphabetSize - 1 - val % alphabetSize);
			val = val / alphabetSize;
		}
		return result;
	}

	public static Integer[] createKmers(Sequence sequence, int k, boolean mergeRevComp) throws InvalidArgumentException {
		if (usedVal[k] == null) {
			createMergedKmerTabl(k);
		}

		int maxVal = (int) Math.pow(alphabetSize, k);

		Integer[] result = new Integer[maxVal];
		for (int i = 0; i < maxVal; i++) {
			if (mergeRevComp) {
				result[usedVal[k][i]] = 1;
			} else
				result[i] = 1;
		}
		String dnaSequence = sequence.getDna();
		int val = 0;
		for (int i = 0; i < dnaSequence.length(); i++) {
			if (i >= k) {
				if (mergeRevComp)
					result[usedVal[k][val]]++;
				else
					result[val]++;
			}
			try {
				val = (val * alphabetSize + nucleotideVal(dnaSequence.charAt(i))) % maxVal;
			} catch (InvalidArgumentException e) {
				logger.warn("Unknown letter: " + dnaSequence.charAt(i));
				if (mergeRevComp)
					result[usedVal[k][val]]--;
				else
					result[val]--;
			}
		}
		if (k <= dnaSequence.length()) {
			if (mergeRevComp)
				result[usedVal[k][val]]++;
			else
				result[val]++;
		}
		if (mergeRevComp) {
			int counter=0;
			for (int j = 0; j < maxVal; j++) {
				if (result[j] != null) {
					counter++;
				}
			}
			Integer[] mergedResult = new Integer[counter];
			int i = 0;
			for (int j = 0; j < maxVal; j++) {
				if (result[j] != null)
					mergedResult[i++] = result[j];
			}
			return mergedResult;
		} else
			return result;
	}

	private static int nucleotideVal(char charAt) throws InvalidArgumentException {
		switch (charAt) {
			case ('A'):
				return 0;
			case ('C'):
				return 1;
			case ('G'):
				return 2;
			case ('T'):
				return 3;
			case ('a'):
				return 0;
			case ('c'):
				return 1;
			case ('g'):
				return 2;
			case ('t'):
				return 3;
			default:
				throw new InvalidArgumentException("Invalid nucleotide: " + charAt);
		}
	}

	public static void normalizeDescVectors(DataSet dataSet, int k) {
		for (Sequence sequence : dataSet.getSequences()) {
			normalizeDescVector(sequence, k);
		}
	}

	public static void normalizeDescVector(Sequence sequence, int k) {
		Integer[] map = sequence.getKmers(k);

		int maxVal = map.length;

		double descVector[] = new double[maxVal];
		sequence.setDescVector(descVector);

		double counter = 0;
		for (int i = 0; i < maxVal; i++) {
			counter += map[i];
		}

		for (int i = 0; i < maxVal; i++) {
			descVector[i] = map[i] / counter;
		}
	}

	public static void createClrData(DataSet dataSet) {
		int vectorLength = dataSet.getSequences().get(0).getDescVector().length;
		double meanLn[] = new double[vectorLength];
		for (Sequence sequence : dataSet.getSequences()) {
			for (int i = 0; i < vectorLength; i++)
				meanLn[i] += Math.log(sequence.getDescVector()[i]);
		}
		for (int i = 0; i < vectorLength; i++)
			meanLn[i] /= dataSet.getSequences().size();

		for (Sequence sequence : dataSet.getSequences()) {
			double clrVector[] = new double[vectorLength];
			for (int i = 0; i < vectorLength; i++)
				clrVector[i] = Math.log(sequence.getDescVector()[i]) - meanLn[i];
			sequence.setClrVector(clrVector);
		}

	}

	public static void computePca(DataSet dataSet, int columns) {
		PrincipleComponentAnalysis pca = new PrincipleComponentAnalysis();
		pca.setup(dataSet.getSequences().size(), dataSet.getSequences().get(0).getClrVector().length);
		for (Sequence sequence : dataSet.getSequences()) {
			pca.addSample(sequence.getClrVector());
		}
		pca.computeBasis(columns);
		for (Sequence sequence : dataSet.getSequences()) {
			sequence.setPcaVector(pca.sampleToEigenSpace(sequence.getClrVector()));
		}
	}

	public static void runTsneAndPutResultsToDir(DataSet dataSet, String dir, double theta, double perplexity) throws UnhandledOSException, IOException,
			InterruptedException {
		String os = System.getProperty("os.name");
		if (os.toUpperCase().contains("WINDOWS"))
			throw new UnhandledOSException("This method doesn't work on windows");
		else
			logger.debug("OS: " + os);
		DataSetFactory.saveToPcaFile(dataSet, dir + "/data.dat", theta, perplexity);

		String command = dir + "/../" + tsneComand;
		logger.debug("Running command: \"" + command + "\" in directory: " + dir);
		Runtime rt = Runtime.getRuntime();

		Process process = rt.exec(command, new String[] {}, new File(dir));
		process.waitFor();

	}

}
