package lu.uni.lcsb.vizbin;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.*;
import org.mockito.Mockito;

import lu.uni.lcsb.vizbin.settings.ISettings;

public class MainCliTest {
  @Before
  public void setUp() {
    ISettings settings = Mockito.mock(ISettings.class);
    Main.settings = settings;
  }

  @After
  public void tearDown() {
    Main.settings = null;
  }

  @Test
  public void testBasicProcessing() {
    Main.main(new String[] { "-i", "testFiles/smallInput/EqualSet02.fa", "-o", "output.txt" });
    File f = new File("output.txt");
    assertTrue(f.exists());
    f.delete();
  }

}
