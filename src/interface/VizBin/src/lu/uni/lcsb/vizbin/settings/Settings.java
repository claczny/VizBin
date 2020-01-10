package lu.uni.lcsb.vizbin.settings;

import java.awt.GraphicsEnvironment;
import java.io.*;
import java.util.Properties;

import javax.swing.JOptionPane;

import lu.uni.lcsb.vizbin.PluginUtils;

/**
 *
 *
 * @author <a href="mailto:valentin.plugaru.001@student.uni.lu">Valentin
 *         Plugaru</a>
 */
public class Settings implements ISettings {
  
	/**
	 * How big should be the buffer when reading file.
	 */
	private static final int	READ_BUFFER_SIZE	= 1024;
	private String						settingsDirName		= ".vizbin";
	private String						settingsFileName	= "config";
	private String						pluginDirName			= "plugins";
	private File							settingsPath;
	private File							settingsFile;
	private File							binFile;
	private File							pluginPath;

	private PluginUtils				pluginUtils;

	private Properties				prop							= null;

	public Settings() {
		settingsPath = new File(System.getProperty("user.home"), settingsDirName);
		settingsFile = new File(settingsPath, settingsFileName);
		pluginPath = new File(settingsPath, pluginDirName);
		System.setProperty("settingsPath", System.getProperty("user.home") + "/" + settingsDirName);
	}

	@Override
  public boolean settingsExist() {
		return settingsPath.isDirectory() && settingsFile.isFile();
	}

	@Override
  public String getTSNEBinName() {
		String os = System.getProperty("os.name");
		if (os.toUpperCase().contains("WINDOWS")) {
			return "pbh_tsne.exe";
		}
		if (os.toUpperCase().contains("LINUX")) {
			return "pbh_tsne";
		}
		if (os.toUpperCase().contains("OS X")) {
			return "pbh_tsne_osx";
		}
		return "";
	}

	@Override
  public void createSettings() {

		try {
			settingsPath.mkdir();
			pluginPath.mkdir();
			prop = new Properties();
			prop.setProperty("TSNECommand", new File(settingsPath, getTSNEBinName()).toString());
			prop.setProperty("PluginDir", pluginPath.toString());
			prop.store(new FileOutputStream(settingsFile), null);
		} catch (Exception e) {
			if(!GraphicsEnvironment.isHeadless()){
				JOptionPane.showMessageDialog(null, "Error creating settings!");
			} else {
				System.err.println("Error creating settings!");
			}
		}
	}

	@Override
  public void extractTSNEBin() throws IOException {
		String binName = getTSNEBinName();

		if (binName != "") {
			binFile = new File(settingsPath, binName);
			InputStream instream = null;
			OutputStream outstream = null;
			byte[] buf = new byte[READ_BUFFER_SIZE];
			int count = 0;
			try {
				instream = this.getClass().getClassLoader().getResourceAsStream("tsne/" + binName);
				if (instream==null) {
				  throw new FileNotFoundException("There is no pre-compiled version of tsne: " + binName);
				}
				outstream = new FileOutputStream(binFile);
				while ((count = instream.read(buf)) >= 0) {
					outstream.write(buf, 0, count);
				}
			} finally {
				try {
					if (instream != null) {
						instream.close();
					}
					if (outstream != null) {
						outstream.flush();
						outstream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			binFile.setExecutable(true);
		}
	}

    @Override
	public void loadSettings() {
		prop = new Properties();
		try {
			prop.load(new FileInputStream(settingsFile));
			binFile = new File(prop.getProperty("TSNECommand"));
			pluginPath = new File(prop.getProperty("PluginDir"));
			pluginUtils = new PluginUtils(pluginPath);
		} catch (Exception e) {
			e.printStackTrace();
			createSettings();
		}
	}

	@Override
  public String getSettingsDirName() {
		return settingsDirName;
	}

	/**
	 * @param settingsDirName
	 *          the settingsDirName to set
	 * @see #settingsDirName
	 */
	public void setSettingsDirName(String settingsDirName) {
		this.settingsDirName = settingsDirName;
	}

	@Override
  public String getSettingsFileName() {
		return settingsFileName;
	}

	/**
	 * @param settingsFileName
	 *          the settingsFileName to set
	 * @see #settingsFileName
	 */
	public void setSettingsFileName(String settingsFileName) {
		this.settingsFileName = settingsFileName;
	}

	@Override
  public String getPluginDirName() {
		return pluginDirName;
	}

	/**
	 * @param pluginDirName
	 *          the pluginDirName to set
	 * @see #pluginDirName
	 */
	public void setPluginDirName(String pluginDirName) {
		this.pluginDirName = pluginDirName;
	}

	@Override
  public File getSettingsPath() {
		return settingsPath;
	}

	/**
	 * @param settingsPath
	 *          the settingsPath to set
	 * @see #settingsPath
	 */
	public void setSettingsPath(File settingsPath) {
		this.settingsPath = settingsPath;
	}

	@Override
  public File getSettingsFile() {
		return settingsFile;
	}

	/**
	 * @param settingsFile
	 *          the settingsFile to set
	 * @see #settingsFile
	 */
	public void setSettingsFile(File settingsFile) {
		this.settingsFile = settingsFile;
	}

	@Override
  public File getBinFile() {
		return binFile;
	}

	/**
	 * @param binFile
	 *          the binFile to set
	 * @see #binFile
	 */
	public void setBinFile(File binFile) {
		this.binFile = binFile;
	}

	@Override
  public PluginUtils getPluginUtils() {
		return pluginUtils;
	}

	/**
	 * @param pluginUtils
	 *          the pluginUtils to set
	 * @see #pluginUtils
	 */
	public void setPluginUtils(PluginUtils pluginUtils) {
		this.pluginUtils = pluginUtils;
	}

}
