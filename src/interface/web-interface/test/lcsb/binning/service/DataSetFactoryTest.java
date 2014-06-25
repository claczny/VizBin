package lcsb.binning.service;

import static org.junit.Assert.*;
import lcsb.binning.TestHelper;
import lcsb.binning.data.DataSet;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataSetFactoryTest extends TestHelper {
	static Logger logger = Logger.getLogger(DataSetFactoryTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateFromPointList() {
		try {
			DataSet dataSet = DataSetFactory.createDataSetFromPointFile("testFiles/samplePointList.txt",null,1);
			assertNotNull(dataSet);
			assertNotNull(dataSet.getSequences());
			assertEquals(29212, dataSet.getSequences().size());
			assertEquals(0, dataSet.getSequences().get(0).getLocation().distance(25.301, 10.394), EPSILON);
		} catch (Exception e) {
			e.printStackTrace();
			fail("unknown exception");
		}

	}
	
	@Test
	public void testCreateFromFastaFile(){
		try {
			DataSet dataSet = DataSetFactory.createDataSetFromFastaFile("testFiles/sample.fasta");
			assertNotNull(dataSet);
			assertNotNull(dataSet.getSequences());
			assertEquals(3, dataSet.getSequences().size());
			assertEquals("CCCCGCTTTTGCTGTTACGAAGTTGCTATTCTAGGGAAATAAATCTTATC", dataSet.getSequences().get(0).getDna());
			assertEquals("TGCCCCGAGGAAGATCCACATTGCCAGCCAGTTGCTTTTCGGCAGTTTTTTATCCAGCAG", dataSet.getSequences().get(2).getDna());
		} catch (Exception e) {
			e.printStackTrace();
			fail("unknown exception");
		}
		
		

	}

}
