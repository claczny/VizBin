package lu.uni.lcsb.vizbin;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

/**
 *
 * @author <a href="mailto:valentin.plugaru.001@student.uni.lu">Valentin
 *         Plugaru</a>
 *
 */
public class Main {
	static Logger logger;
    static Settings settings = null;
    
    public static void main(String[] args) {
    	settings = new Settings();
    	logger = Logger.getLogger(Main.class);
        
    	MainFrame mframe = new MainFrame();
        mframe.setVisible(true);
        mframe.setSettings(settings);
        
        if (!settings.settingsExist()) {
            JOptionPane.showMessageDialog(mframe, "Application settings not found, they will be created for you now.\n"
                    + "Configuration file: "+settings.settingsFile.toString());
            settings.createSettings();
            settings.extractTSNEBin();
        }
        else settings.loadSettings();
    }
    
}
