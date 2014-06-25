package lcsb.binning.graphics;

import static org.junit.Assert.*;

import java.awt.Desktop;
import java.io.File;

import lcsb.binning.TestHelper;
import lcsb.binning.data.DataSet;
import lcsb.binning.service.DataSetFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PngGraphicsConverterTest extends TestHelper{

	private DataSet	dataSet;

	@Before
	public void setUp() throws Exception {
		dataSet = DataSetFactory.createDataSetFromPointFile("testFiles/samplePointList.txt","testFiles/sampleLabels.txt",10);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try {
			PngGraphicsConverter converter = new PngGraphicsConverter(dataSet);
			converter.createPngFile(-400,-400, 4, 800, "tmp.png");
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown exception");
		}
	}

	@Test
	public void test2() {
		try {
			PngGraphicsConverter converter = new PngGraphicsConverter(dataSet);
			converter.createPngDirectory("tmp",2);			
			Desktop.getDesktop().open(new File("tmp/legend.png"));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown exception");
		}
	}

}
