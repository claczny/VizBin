package lu.uni.lcsb.vizbin.clustering;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * 
 * @author <a href="mailto:valentin.plugaru.001@student.uni.lu">Valentin
 *         Plugaru</a>
 */
public interface ClusteringPlugin {
	ArrayList<ArrayList<Point2D>> getClusters(ArrayList<Point2D> pointList);

	void drawOptionsPanel(JPanel optionsPanel);

	int getPluginModelVersion();
}
