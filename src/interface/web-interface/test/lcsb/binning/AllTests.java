package lcsb.binning;

import lcsb.binning.service.ServiceTests;
import lcsb.binning.tsne.AllTsneTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ServiceTests.class, AllTsneTests.class })
public class AllTests {

}
