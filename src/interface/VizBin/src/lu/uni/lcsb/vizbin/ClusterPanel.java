package lu.uni.lcsb.vizbin;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import lcsb.vizbin.data.Cluster;
import lcsb.vizbin.data.DataSet;
import lcsb.vizbin.data.Sequence;
import lcsb.vizbin.service.ClusterFactory;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.ui.RectangleEdge;

public class ClusterPanel {
	Logger							logger	= Logger.getLogger(ClusterPanel.class);
	int									COUNT		= -1;
	float[][]						data;
	int[]								sizes;
	Color[]							colors;
	PointShape[]				shapes;
	private ChartPanel	panel;
	List<Point2D>				polygon	= new ArrayList<Point2D>();

	DataSet							dataSet;

	JFrame							frame;
	String							filename;
	private LegendFrame	legendFrame;

	private final class MouseMarker extends MouseAdapter {
		private final JFreeChart	chart;
		private final ChartPanel	panel;
		List<Point2D>							polygon;

		public MouseMarker(ChartPanel panel, List<Point2D> polygon) {
			this.panel = panel;
			this.chart = panel.getChart();
			this.polygon = polygon;
			// this.plot.setDomainGridlinesVisible(false);
			// this.plot.setRangeGridlinesVisible(false);

		}

		public void mouseClicked(MouseEvent e) {
			// Put edges of the polygon for selecting a set of points in 2D
			if (SwingUtilities.isLeftMouseButton(e)) {
				// Motivated by http://www.jfree.org/phpBB2/viewtopic.php?p=54140
				int mouseX = e.getX();
				int mouseY = e.getY();
				Point2D p = panel.translateScreenToJava2D(new Point(mouseX, mouseY));
				FastScatterPlot plot = (FastScatterPlot) chart.getPlot();
				ChartRenderingInfo info = panel.getChartRenderingInfo();
				Rectangle2D dataArea = info.getPlotInfo().getDataArea();

				ValueAxis domainAxis = plot.getDomainAxis();
				// RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
				ValueAxis rangeAxis = plot.getRangeAxis();
				// RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
				double chartX = domainAxis.java2DToValue(p.getX(), dataArea, RectangleEdge.BOTTOM);
				double chartY = rangeAxis.java2DToValue(p.getY(), dataArea, RectangleEdge.LEFT);
				// DEBUG
				polygon.add(new Point2D.Double(chartX, chartY));

			}

		}
	}

	public ClusterPanel(DataSet ds, String _inFileName, JFrame _parentFrame, boolean _drawAxes) {

		this.dataSet = ds;
		this.frame = _parentFrame;
		this.filename = _inFileName;
		ArrayList<Sequence> pointList = (ArrayList<Sequence>) ds.getSequences();// seqs;//points;
		COUNT = pointList.size();

		int min_size = 4;
		// If there is additional length information provided use a smaller default
		// minimum size
		if ((double) 0 < ds.getMinSequenceLength()) {
			min_size = 2;
		}
		data = new float[2][COUNT];
		sizes = new int[COUNT];
		colors = new Color[COUNT];
		shapes = new PointShape[COUNT];

		int counter = 0;
		float alpha = 0f;
		legendFrame = new LegendFrame();

		for (Sequence sequence : pointList) {
			data[0][counter] = (float) sequence.getLocation().getX();
			data[1][counter] = (float) sequence.getLocation().getY();
			// DEBUG
			// logger.debug(sequence.getLocation());
			colors[counter] = ShapePrinter.getColor(sequence.getLabelId());
			shapes[counter] = ShapePrinter.getShape(sequence.getLabelId());

			legendFrame.setLabel(sequence.getLabelId(), sequence.getLabelName());

			if (sequence.getCoverage() != null) {
				alpha = (sequence.getCoverage()).floatValue();
				alpha = alpha * alpha;
				colors[counter] = new Color((int) colors[counter].getRed(), (int) colors[counter].getGreen(), (int) colors[counter].getBlue(), (int) (alpha * 255));
			}
			else // Either coverage OR GC content
				if (sequence.getGc() != null) {
					alpha = (sequence.getGc()).floatValue();
					alpha = alpha * alpha;
					colors[counter] = new Color((int) colors[counter].getRed(), (int) colors[counter].getGreen(), (int) colors[counter].getBlue(), (int) (alpha * 255));
				}

			if (sequence.getMarker() != null && sequence.getMarker()) {
				shapes[counter] = PointShape.STAR;
			}
			// Use a minimum size
			sizes[counter] = min_size;
			// Adjust the size if additional length information is given
			if (sequence.getLength() != null) {
				double magnification = (sequence.getLength()).doubleValue() - ds.getMinSequenceLength();
				//magnification = magnification * magnification;
				sizes[counter] = (int) (sizes[counter] + (sizes[counter] * magnification));
				
			}
			counter++;
		}

		final NumberAxis domainAxis = new NumberAxis("");
		domainAxis.setAutoRangeIncludesZero(false);
		domainAxis.setTickMarksVisible(false);
		domainAxis.setTickLabelsVisible(false);

		final NumberAxis rangeAxis = new NumberAxis("");
		rangeAxis.setAutoRangeIncludesZero(false);
		rangeAxis.setTickMarksVisible(false);
		rangeAxis.setTickLabelsVisible(false);

		ExtendedFastScatterPlot plot = new ExtendedFastScatterPlot(data, domainAxis, rangeAxis, sizes, colors, shapes, polygon);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		final JFreeChart chart = new JFreeChart("", plot);
		panel = new ChartPanel(chart);
		panel.addMouseListener(new MouseMarker(panel, polygon));
		panel.setMouseWheelEnabled(true); // Enable scroll-wheel support for zooming

		JPopupMenu popup = panel.getPopupMenu();

		JMenu selectionMenu = new JMenu("Selection");
		JMenuItem exportMenu = new JMenuItem("Export");
		exportMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Cluster selectedCluster = ClusterFactory.createClusterFromPolygon(dataSet, polygon);
				if (selectedCluster.getElements().size() > 0) {
					int option;
					option = JOptionPane
							.showConfirmDialog(
									null, selectedCluster.getElements().size() + " sequences selected, export to file? \n\nPress Cancel to remove your selection.",
									"Export selected cluster", JOptionPane.YES_NO_CANCEL_OPTION);
					if (option == JOptionPane.YES_OPTION)
						DataExporter.exportCluster(frame, filename, selectedCluster.getElements());
					else if (option == JOptionPane.CANCEL_OPTION) {
						polygon.clear();

						panel.repaint();
					}
				}
			}
		});
		JMenuItem clearMenu = new JMenuItem("Clear selection");
		clearMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				polygon.clear();
				chart.fireChartChanged();
			}
		});

		selectionMenu.add(exportMenu);
		selectionMenu.add(clearMenu);

		popup.add(selectionMenu);

		JMenuItem legendMenu = new JMenuItem("Legend");

		legendMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LegendFrame legendFrame = getLegendFrame();
				legendFrame.setVisible(!legendFrame.isVisible());
			}
		});
		popup.add(legendMenu);

	}

	public ChartPanel getChartPanel() {
		return panel;
	}

	/**
	 * @return the legendFrame
	 * @see #legendFrame
	 */
	public LegendFrame getLegendFrame() {
		return legendFrame;
	}

	/**
	 * @param legendFrame
	 *          the legendFrame to set
	 * @see #legendFrame
	 */
	public void setLegendFrame(LegendFrame legendFrame) {
		this.legendFrame = legendFrame;
	}
}
