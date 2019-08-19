package lu.uni.lcsb.vizbin;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;

import java.io.File;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.junit.*;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import lu.uni.lcsb.vizbin.settings.ISettings;
import lu.uni.lcsb.vizbin.tsne.*;

public class MainCliTest {

  Logger logger = Logger.getLogger(MainCliTest.class);

  @Before
  public void setUp() {
    ISettings settings = Mockito.mock(ISettings.class);
    Main.settings = settings;
    TSNERunnerFactory tsneRunnerFactory = Mockito.mock(TSNERunnerFactory.class);
    Mockito
        .when(tsneRunnerFactory.createRunner(nullable(File.class), nullable(String.class),
            nullable(ProcessGuiParameters.class)))
        .thenAnswer(new Answer<TSNERunner>() {
          @Override
          public TSNERunner answer(InvocationOnMock arg0) throws Throwable {
            // create mock response file
            PrintWriter writer = new PrintWriter(arg0.getArgument(1).toString() + "/points.txt", "UTF-8");
            writer.println("The first line");
            writer.println("The second line");
            writer.close();
            return null;
          }
        });
    DataSetUtils.tsneRunnerFactory = tsneRunnerFactory;
  }

  @After
  public void tearDown() {
    Main.settings = null;
    DataSetUtils.tsneRunnerFactory = new TSNERunnerFactoryImpl();
  }

  @Test
  public void testBasicProcessing() {
    Main.main(new String[] { "-i", "testFiles/smallInput/EqualSet02.fa", "-o", "output.txt" });
    File f = new File("output.txt");
    assertTrue(f.exists());
    f.delete();
  }

}
