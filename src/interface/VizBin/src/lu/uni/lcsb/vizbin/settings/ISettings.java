package lu.uni.lcsb.vizbin.settings;

import java.io.File;
import java.io.IOException;

import lu.uni.lcsb.vizbin.PluginUtils;

public interface ISettings {

  boolean settingsExist();

  String getTSNEBinName();

  void createSettings();

  void extractTSNEBin() throws IOException;
  
  void loadSettings();

  /**
   * @return the settingsDirName
   * @see #settingsDirName
   */
  String getSettingsDirName();

  /**
   * @return the settingsFileName
   * @see #settingsFileName
   */
  String getSettingsFileName();

  /**
   * @return the pluginDirName
   * @see #pluginDirName
   */
  String getPluginDirName();

  /**
   * @return the settingsPath
   * @see #settingsPath
   */
  File getSettingsPath();

  /**
   * @return the settingsFile
   * @see #settingsFile
   */
  File getSettingsFile();

  /**
   * @return the binFile
   * @see #binFile
   */
  File getBinFile();

  /**
   * @return the pluginUtils
   * @see #pluginUtils
   */
  PluginUtils getPluginUtils();

}