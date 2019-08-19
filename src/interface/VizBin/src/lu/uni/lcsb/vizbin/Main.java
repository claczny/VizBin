package lu.uni.lcsb.vizbin;

import java.awt.GraphicsEnvironment;

// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.IOException;
// import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import lu.uni.lcsb.vizbin.settings.ISettings;
import lu.uni.lcsb.vizbin.settings.Settings;

// import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author <a href="mailto:valentin.plugaru.001@student.uni.lu">Valentin
 *         Plugaru</a>
 *
 */
public class Main {

	/**
	 * Default constructor for utility class. Prevents instatiation.
	 */
	private Main() {

	}

	/**
	 * Default class logger.
	 */
	private static Logger		logger		= null;
	
	static ISettings	settings	= null;

	public static void main(String[] args) {
	  if (settings == null) {
		settings = new Settings();
	  }
		logger = Logger.getLogger(Main.class);

		try {
			CommandLineOptions clo = new CommandLineOptions(args);

			if (clo.isValid()) {
				logger.debug("Running command line...");
				if (!settings.settingsExist()) {
					settings.createSettings();
					settings.extractTSNEBin();
				} else {
					settings.loadSettings();
				}
				ProcessParameters params = clo.getParameters();
				ProcessInput process = new ProcessInput(params, null, settings.getBinFile());
				Thread thread = process.doProcess();
				thread.join();

			} else {
				clo.printHelp();
				if (!clo.isHelp()) {
				  if(!GraphicsEnvironment.isHeadless()){
					MainFrame mframe = new MainFrame();
					mframe.setVisible(true);
					mframe.setSettings(settings);

					if (!settings.settingsExist()) {
						JOptionPane.showMessageDialog(mframe, "Application settings not found, they will be created for you now.\n" + "Configuration file: "
								+ settings.getSettingsFile().toString());
						settings.createSettings();
						settings.extractTSNEBin();
					} else {
						settings.loadSettings();
					}
				  }
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

}
