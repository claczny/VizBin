package lu.uni.lcsb.vizbin.tsne;

import java.io.File;

import lu.uni.lcsb.vizbin.ProcessGuiParameters;

public class TSNERunnerFactoryImpl implements TSNERunnerFactory {

  @Override
  public TSNERunner createRunner(File command, String dir, ProcessGuiParameters guiParameters) {
    return new TSNERunnerImpl(command, dir, guiParameters);
  }

}
