package lu.uni.lcsb.vizbin.tsne;

import java.io.File;

import lu.uni.lcsb.vizbin.ProcessGuiParameters;

public interface TSNERunnerFactory {
  TSNERunner createRunner(File command, String dir, ProcessGuiParameters guiParameters);
}
