package lcsb.binning.tsne;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import lcsb.binning.TestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CollectionUtilsTest extends TestHelper {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetNthElement() {
		try {
			List<Double> list = new ArrayList<Double>();
			list.add(Double.valueOf(2));
			list.add(Double.valueOf(21));
			list.add(Double.valueOf(5));
			list.add(Double.valueOf(17));
			list.add(Double.valueOf(30));
			list.add(Double.valueOf(3));
			list.add(Double.valueOf(1));

			assertEquals((double) 1, CollectionUtils.getNthElement(0, list), EPSILON);
			assertEquals((double) 2, CollectionUtils.getNthElement(1, list), EPSILON);
			assertEquals((double) 3, CollectionUtils.getNthElement(2, list), EPSILON);
			assertEquals((double) 5, CollectionUtils.getNthElement(3, list), EPSILON);
			assertEquals((double) 17, CollectionUtils.getNthElement(4, list), EPSILON);
			assertEquals((double) 21, CollectionUtils.getNthElement(5, list), EPSILON);
			assertEquals((double) 30, CollectionUtils.getNthElement(6, list), EPSILON);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown exception");
		}
	}

	@Test
	public void testNthElement() {
		try {
			List<Double> list = new ArrayList<Double>();
			list.add(Double.valueOf(2));
			list.add(Double.valueOf(21));
			list.add(Double.valueOf(5));
			list.add(Double.valueOf(17));
			list.add(Double.valueOf(30));
			list.add(Double.valueOf(3));
			list.add(Double.valueOf(1));

			int beg = 0;
			int nth = 2;
			int end = 6;
			CollectionUtils.nthElement(beg, nth, end, list);
			for (int i = 0; i < nth; i++)
				assertTrue(list.get(i) <= list.get(nth));
			assertEquals((double) 5, list.get(nth), EPSILON);
			for (int i = nth + 1; i < end; i++)
				assertTrue(list.get(i) >= list.get(nth));

		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown exception");
		}
	}

}
