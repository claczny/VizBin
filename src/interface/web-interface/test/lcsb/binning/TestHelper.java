package lcsb.binning;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;

public class TestHelper {
	protected double	EPSILON	= 1e-6;

	@Before
	public void commonSetUp() {
		PropertyConfigurator.configure("web/WEB-INF/resources/log4j.properties");
	}
}
