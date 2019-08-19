package lu.uni.lcsb.vizbin;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class MainCliTest {

  @Test
  public void testBasicProcessing() {
    Main.main(new String[] { "-i", "testFiles/smallInput/EqualSet02.fa", "-o", "output.txt" });
    File f = new File("output.txt");
    assertTrue(f.exists());
    f.delete();
  }

}
