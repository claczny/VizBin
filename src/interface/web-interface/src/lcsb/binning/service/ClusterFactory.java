package lcsb.binning.service;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import lcsb.binning.data.Cluster;
import lcsb.binning.data.DataSet;
import lcsb.binning.data.Sequence;

import org.apache.log4j.Logger;

public class ClusterFactory {
	static Logger	logger	= Logger.getLogger(ClusterFactory.class);

	public static Cluster createClusterFromPolygon(DataSet dataSet, List<Point2D> polygon) {
		List<Sequence> elements = new ArrayList<Sequence>();

		Polygon awtPolygon = new Polygon();

		for (Point2D point2d : polygon) {
			awtPolygon.addPoint((int) point2d.getX(), (int) point2d.getY());
		}

		for (Sequence sequence : dataSet.getSequences()) {
			if (awtPolygon.contains(sequence.getLocation())) {
				elements.add(sequence);
			} 
		}

		Cluster result = new Cluster(elements);
		return result;
	}

	public static Cluster createClusterFromStartPoint(DataSet dataSet, Point2D startPoint) {
		return null;
	}
}
