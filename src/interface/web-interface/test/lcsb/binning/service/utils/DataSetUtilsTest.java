package lcsb.binning.service.utils;

import static org.junit.Assert.*;

import java.util.Map;

import lcsb.binning.TestHelper;
import lcsb.binning.data.DataSet;
import lcsb.binning.data.Sequence;
import lcsb.binning.service.DataSetFactory;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataSetUtilsTest extends TestHelper {
	static Logger	logger	= Logger.getLogger(DataSetUtilsTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateKmers() {
		try {
			int kMerLen = 3;
			String dna = "ACGTTTGACGAAA";
			Sequence sequence = new Sequence();
			sequence.setDna(dna);
			Integer[] map = DataSetUtils.createKmers(sequence, kMerLen, false);
			assertEquals((int)(Math.pow(4, kMerLen) ), map.length);

			int sum = (int) (dna.length() - kMerLen + 1 + Math.pow(4, kMerLen));
			int sum2 = 0;
			for (int i = 0; i < map.length; i++)
				sum2 += map[i];
			assertEquals(sum, sum2);

			map = DataSetUtils.createKmers(sequence, kMerLen, true);
			assertEquals((int)(Math.pow(4, kMerLen) / 2), map.length);

			sum = (int) (dna.length() - kMerLen + 1 + Math.pow(4, kMerLen) / 2);
			sum2 = 0;
			for (int i = 0; i < map.length; i++)
				sum2 += map[i];
			assertEquals(sum, sum2);

			assertEquals(3, (int) map[0]);
			assertEquals(2, (int) map[1]);
			assertEquals(1, (int) map[2]);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown excepiton");
		}

	}

	@Test
	public void testNormalizeDescVectorreateKmers() {
		try {
			int kMerLen = 3;
			String dna = "ACGTTTGACG";
			Sequence sequence = new Sequence();
			sequence.setDna(dna);
			sequence.setKmers(3, DataSetUtils.createKmers(sequence, kMerLen, false));
			DataSetUtils.normalizeDescVector(sequence, kMerLen);
			double counter = dna.length() - kMerLen + 1 + Math.pow(4, kMerLen);
			assertEquals(1 / counter, sequence.getDescVector()[0 * 16 + 0 * 4 + 0], EPSILON);
			assertEquals(3 / counter, sequence.getDescVector()[0 * 16 + 1 * 4 + 2], EPSILON);
			assertEquals(2 / counter, sequence.getDescVector()[1 * 16 + 2 * 4 + 3], EPSILON);
			assertEquals(2 / counter, sequence.getDescVector()[2 * 16 + 3 * 4 + 3], EPSILON);
			assertEquals(2 / counter, sequence.getDescVector()[3 * 16 + 3 * 4 + 3], EPSILON);
			assertEquals(2 / counter, sequence.getDescVector()[3 * 16 + 3 * 4 + 2], EPSILON);
			assertEquals(2 / counter, sequence.getDescVector()[3 * 16 + 2 * 4 + 0], EPSILON);
			assertEquals(2 / counter, sequence.getDescVector()[2 * 16 + 0 * 4 + 1], EPSILON);

			sequence.setKmers(3, DataSetUtils.createKmers(sequence, kMerLen, true));
			DataSetUtils.normalizeDescVector(sequence, kMerLen);
			counter = 0;
			for (double d: sequence.getDescVector()) {
				counter+=d;
			}
			assertEquals(1, counter,EPSILON);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown excepiton");
		}

	}

	@Test
	public void testClrNormalization() {
		try {
			DataSet dataSet = new DataSet();

			Sequence sequence1 = new Sequence();
			sequence1.setDescVector(new double[] { 1, 2, 3 });
			dataSet.addSequence(sequence1);

			Sequence sequence2 = new Sequence();
			sequence2.setDescVector(new double[] { 1, 2, 4 });
			dataSet.addSequence(sequence2);

			Sequence sequence3 = new Sequence();
			sequence3.setDescVector(new double[] { 1, 3, 5 });
			dataSet.addSequence(sequence3);

			DataSetUtils.createClrData(dataSet);

			double avg1 = (Math.log(1) + Math.log(1) + Math.log(1)) / 3;
			double avg2 = (Math.log(2) + Math.log(2) + Math.log(3)) / 3;
			double avg3 = (Math.log(3) + Math.log(4) + Math.log(5)) / 3;

			assertEquals(0, sequence1.getClrVector()[0], EPSILON);
			assertEquals(Math.log(2) - avg2, sequence1.getClrVector()[1], EPSILON);
			assertEquals(Math.log(3) - avg3, sequence1.getClrVector()[2], EPSILON);

			assertEquals(Math.log(1) - avg1, sequence2.getClrVector()[0], EPSILON);
			assertEquals(Math.log(2) - avg2, sequence2.getClrVector()[1], EPSILON);
			assertEquals(Math.log(4) - avg3, sequence2.getClrVector()[2], EPSILON);

			assertEquals(Math.log(1) - avg1, sequence3.getClrVector()[0], EPSILON);
			assertEquals(Math.log(3) - avg2, sequence3.getClrVector()[1], EPSILON);
			assertEquals(Math.log(5) - avg3, sequence3.getClrVector()[2], EPSILON);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown excepiton");
		}

	}

	@Test
	public void testPca() {
		try {
			DataSet dataSet = new DataSet();

			Sequence sequence1 = new Sequence();
			sequence1.setDescVector(new double[] { 1, 2, 3 });
			dataSet.addSequence(sequence1);

			Sequence sequence2 = new Sequence();
			sequence2.setDescVector(new double[] { 1, 2, 4 });
			dataSet.addSequence(sequence2);

			Sequence sequence3 = new Sequence();
			sequence3.setDescVector(new double[] { 1, 3, 5 });
			dataSet.addSequence(sequence3);

			DataSetUtils.createClrData(dataSet);

			DataSetUtils.computePca(dataSet, 2);

			assertEquals(2, sequence1.getPcaVector().length);
			assertEquals(2, sequence2.getPcaVector().length);
			assertEquals(2, sequence3.getPcaVector().length);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown excepiton");
		}
	}

	// @Test
	public void testCase() {
		try {
			logger.debug("A");
			DataSet dataSet = DataSetFactory.createDataSetFromFastaFile("testFiles/grinder-reads.fa");
			logger.debug("B");
			DataSetUtils.createKmers(dataSet, 5, false);
			logger.debug("C");
			DataSetUtils.normalizeDescVectors(dataSet, 5);
			logger.debug("D");
			DataSetUtils.createClrData(dataSet);
			logger.debug("E");
			DataSetUtils.computePca(dataSet, 50);
			logger.debug("F");
			DataSetUtils.runTsneAndPutResultsToDir(dataSet, "tmp", 0.5, 30);
			DataSetFactory.saveToPcaFile(dataSet, "data.dat");
			logger.debug("G");
		} catch (Exception e) {
			e.printStackTrace();
			fail();

		}
	}
}
