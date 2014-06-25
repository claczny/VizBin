package lcsb.binning.service;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import lcsb.binning.TestHelper;
import lcsb.binning.data.Cluster;
import lcsb.binning.data.DataSet;
import lcsb.binning.data.Sequence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClusterFactoryTest extends TestHelper {

	private DataSet	dataSet;

	@Before
	public void setUp() throws Exception {
		dataSet = new DataSet();
		Sequence sequence = new Sequence();
		sequence.setId("id1");
		sequence.setLocation(new Point2D.Double(10, 10));
		dataSet.addSequence(sequence);
		sequence = new Sequence();
		sequence.setId("id2");
		sequence.setLocation(new Point2D.Double(20, 20));
		dataSet.addSequence(sequence);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateFromPolygon1() {
		try {
			List<Point2D> polygon = new ArrayList<Point2D>();
			polygon.add(new Point2D.Double(0, 0));
			polygon.add(new Point2D.Double(0, 15));
			polygon.add(new Point2D.Double(15, 15));
			polygon.add(new Point2D.Double(15, 0));
			Cluster cluster = ClusterFactory.createClusterFromPolygon(dataSet, polygon);
			assertNotNull(cluster);
			assertEquals(1, cluster.getElements().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown exception");
		}
	}

	@Test
	public void testCreateFromPolygon2() {
		try {
			List<Point2D> polygon = new ArrayList<Point2D>();
			polygon.add(new Point2D.Double(0, 0));
			polygon.add(new Point2D.Double(0, 30));
			polygon.add(new Point2D.Double(30, 30));
			polygon.add(new Point2D.Double(30, 0));
			Cluster cluster = ClusterFactory.createClusterFromPolygon(dataSet, polygon);
			assertNotNull(cluster);
			assertEquals(2, cluster.getElements().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown exception");
		}
	}

	@Test
	public void testCreateFromPolygon3() {
		try {
			List<Point2D> polygon = new ArrayList<Point2D>();
			polygon.add(new Point2D.Double(0, 0));
			polygon.add(new Point2D.Double(0, -15));
			polygon.add(new Point2D.Double(-15, -15));
			polygon.add(new Point2D.Double(-15, 0));
			Cluster cluster = ClusterFactory.createClusterFromPolygon(dataSet, polygon);
			assertNotNull(cluster);
			assertEquals(0, cluster.getElements().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown exception");
		}
	}

}
