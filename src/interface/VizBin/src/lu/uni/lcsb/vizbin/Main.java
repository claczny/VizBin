package lu.uni.lcsb.vizbin;

// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.IOException;
// import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

// import org.apache.log4j.PropertyConfigurator;

/**
 * 
 * @author <a href="mailto:valentin.plugaru.001@student.uni.lu">Valentin
 *         Plugaru</a>
 * 
 */
public class Main {
	static Logger		logger		= null;
	static Settings	settings	= null;

	public static void main(String[] args) {
		settings = new Settings();
		logger = Logger.getLogger(Main.class);

		logger.debug(settings.binFile);
		try {
			CommandLineOptions clo = new CommandLineOptions(args);

			if (clo.isValid()) {
				logger.debug("Running command line...");
				settings.loadSettings();
				ProcessParameters params = clo.getParameters();
				ProcessInput process = new ProcessInput(params, null, settings.binFile);
				process.doProcess();
				
			} else {
				clo.printHelp();
				MainFrame mframe = new MainFrame();
				mframe.setVisible(true);
				mframe.setSettings(settings);

				if (!settings.settingsExist()) {
					JOptionPane.showMessageDialog(mframe, "Application settings not found, they will be created for you now.\n" + "Configuration file: "
							+ settings.settingsFile.toString());
					settings.createSettings();
					settings.extractTSNEBin();
				} else
					settings.loadSettings();
			}
		} catch (Exception e) {
			logger.error(e, e);
			e.printStackTrace();
		}
	}

}
