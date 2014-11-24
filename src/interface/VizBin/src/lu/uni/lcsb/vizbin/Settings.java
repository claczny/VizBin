package lu.uni.lcsb.vizbin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 *
 * @author <a href="mailto:valentin.plugaru.001@student.uni.lu">Valentin
 *         Plugaru</a>
 */
public class Settings {
    String settingsDirName = ".vizbin";
    String settingsFileName = "config";
    String pluginDirName = "plugins";
    File settingsPath;
    File settingsFile;
    File binFile;
    File pluginPath;
    
    PluginUtils pluginUtils;
    
    Properties prop = null;
    
    public Settings(){
        settingsPath = new File(System.getProperty("user.home"),settingsDirName);
        settingsFile = new File(settingsPath,settingsFileName);
        pluginPath = new File(settingsPath,pluginDirName);
        System.setProperty("settingsPath", System.getProperty("user.home")+ "/" + settingsDirName);
    }
    
    public boolean settingsExist(){
        return settingsPath.isDirectory() && settingsFile.isFile();
    }
    
    public String getTSNEBinName(){
        String os = System.getProperty("os.name");
        if (os.toUpperCase().contains("WINDOWS")) return "pbh_tsne.exe";
        if (os.toUpperCase().contains("LINUX")) return "pbh_tsne";
        if (os.toUpperCase().contains("OS X")) return "pbh_tsne_osx";
        return "";
    }
    public void createSettings(){
    
        try { 
            settingsPath.mkdir();
            pluginPath.mkdir();
            prop = new Properties();
            prop.setProperty("TSNECommand", new File(settingsPath, getTSNEBinName()).toString());
            prop.setProperty("PluginDir", pluginPath.toString());
            prop.store(new FileOutputStream(settingsFile), null);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error creating settings!");
        }
    }
    
    public void extractTSNEBin(){
        String os = System.getProperty("os.name");
        String binName = getTSNEBinName();
        
        if (binName != "") {
            binFile = new File( settingsPath , binName);
            InputStream instream = null;
            OutputStream outstream = null;
            byte[] buf = new byte[1024];
            int count = 0;
            try {
                instream = this.getClass().getClassLoader().getResourceAsStream("tsne/"+binName);
                outstream = new FileOutputStream(binFile);
                while((count = instream.read(buf)) >= 0) outstream.write( buf, 0, count );
            }
            catch (IOException e) { e.printStackTrace(); }
            finally {
                try {
                    if( instream != null ) instream.close();
                    if( outstream != null ) {
                        outstream.flush();
                        outstream.close();
                    }
                }
                catch (IOException e) { e.printStackTrace(); }
            }
            binFile.setExecutable(true);
        }   
    }

    void loadSettings() {
       prop = new Properties();
        try { 
            prop.load(new FileInputStream(settingsFile)); 
            binFile = new File(prop.getProperty("TSNECommand"));
            pluginPath = new File(prop.getProperty("PluginDir"));
            pluginUtils = new PluginUtils(pluginPath);
        }
        catch (Exception e) {
            e.printStackTrace();
            createSettings();
        }
    }
    
    
}
