package lu.uni.lcsb.vizbin.tsne;

import java.io.*;

import org.apache.log4j.Logger;

import lu.uni.lcsb.vizbin.ProcessGuiParameters;

class TSNERunnerImpl implements TSNERunner {
  private static Logger logger = Logger.getLogger(TSNERunnerImpl.class);
  
	private File									command;
	private String								dir;
	private ProcessGuiParameters	guiParameters;

	public TSNERunnerImpl(File command, String dir, ProcessGuiParameters guiParameters) {
		this.command = command;
		this.dir = dir;
		this.guiParameters = guiParameters;
	}

	@Override
  public void run() {
		Process process;
		try {
			process = Runtime.getRuntime().exec(new String[] { command.getAbsolutePath() }, new String[] {}, new File(dir));
			/*
			 * Tomasz Sternal - removed this line, it was not updating progress bar
			 * status in mainFrame in real time
			 *
			 * SwingUtilities.invokeAndWait(new
			 * ProgressReader(process.getInputStream(), label_status, progBar));
			 */
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = reader.readLine();
			Integer newProgress = 0;
			while (line != null) {
				logger.debug("TSNE: " + line);

				// other operations take us to 35% completion, split
				// remaining 65% between TSNE phases
				if (line.startsWith("Building tree"))
					newProgress = 5; // 40%
				if (line.startsWith("Learning embedding"))
					newProgress = 5; // 45%
				if (line.startsWith("Iteration")) { // there are 20
					// iterations
					newProgress = 2; // add 2% progress per iteration
					String logEntry = "T-SNE: " + line.substring(0, line.indexOf(':')) + "/1000";
					if (guiParameters != null) {
						guiParameters.getStatusLabel().setText(logEntry);
					} else {
						logger.debug("[PROGRESS BAR] " + logEntry);
					}
				}
				if (line.contains("Wrote the")) {
					newProgress = 5; // 90%
				}
				if (guiParameters != null) {
					guiParameters.getProgessBar().setValue(guiParameters.getProgessBar().getValue() + newProgress);
				} else {
					// logger.debug("[PROGRESS BAR] " + "next " + newProgress + "% ");

				}
				line = reader.readLine();
			}
			reader.close();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}