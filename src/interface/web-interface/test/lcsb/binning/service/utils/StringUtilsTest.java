package lcsb.binning.service.utils;

import static org.junit.Assert.*;
import lcsb.binning.TestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringUtilsTest extends TestHelper {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNextStringOK() {
		try {
			String next;
			next = StringUtils.nextString("A");
			assertEquals("B", next);

			next = StringUtils.nextString("AA");
			assertEquals("AB", next);

			next = StringUtils.nextString("ADB");
			assertEquals("ADC", next);

			next = StringUtils.nextString("QWEZZ");
			assertEquals("QWFAA", next);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown excepiton");
		}
	}

	@Test
	public void testNextStringNull() {
		try {
			String next;
			next = StringUtils.nextString("Z");
			assertNull(next);

			next = StringUtils.nextString("ZZ");
			assertNull(next);

			next = StringUtils.nextString("");
			assertNull(next);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown excepiton");
		}
	}

	@Test
	public void testNextStringOverAlphabet() {
		try {
			String next;
			next = StringUtils.nextOverAlphabet("A", "ACGT");
			assertEquals("C", next);

			next = StringUtils.nextOverAlphabet("AA", "ACGT");
			assertEquals("AC", next);

			next = StringUtils.nextOverAlphabet("AGC", "ACGT");
			assertEquals("AGG", next);

			next = StringUtils.nextOverAlphabet("CGATT", "ACGT");
			assertEquals("CGCAA", next);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown excepiton");
		}
	}

	@Test
	public void testNextStringOverAlphabetNull() {
		try {
			String next;
			next = StringUtils.nextOverAlphabet("T", "ACGT");
			assertNull(next);

			next = StringUtils.nextOverAlphabet("TT", "ACGT");
			assertNull(next);

			next = StringUtils.nextOverAlphabet("", "ACGT");
			assertNull(next);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown excepiton");
		}
	}

	@Test
	public void testReverseComplementarySequence() {
		try {
			String next;
			next = StringUtils.reverseDnaSequence("A");
			assertEquals("T", next);
			next = StringUtils.reverseDnaSequence("AC");
			assertEquals("GT", next);
			next = StringUtils.reverseDnaSequence("AG");
			assertEquals("CT", next);
			next = StringUtils.reverseDnaSequence("GGC");
			assertEquals("GCC", next);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unknown excepiton");
		}

	}

}
