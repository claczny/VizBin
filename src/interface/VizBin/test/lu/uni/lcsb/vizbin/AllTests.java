package lu.uni.lcsb.vizbin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import lu.uni.lcsb.vizbin.service.utils.pca.AllPcaTests;

@RunWith(Suite.class)
@SuiteClasses({
    AllPcaTests.class,
    MainCliTest.class,
    ZipProjectTest.class })
public class AllTests {

}
