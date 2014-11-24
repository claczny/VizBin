package lu.uni.lcsb.vizbin;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * 
 * @author <a href="mailto:valentin.plugaru.001@student.uni.lu">Valentin
 *         Plugaru</a>
 */
public interface ClusteringPlugin {
	public ArrayList<ArrayList<Point2D>> getClusters(ArrayList<Point2D> pointList);

	public void drawOptionsPanel(JPanel optionsPanel);

	public int getPluginModelVersion();
}
