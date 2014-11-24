package lu.uni.lcsb.vizbin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.ui.RectangleEdge;

public class ExtendedFastScatterPlot extends FastScatterPlot {
	Logger										logger						= Logger.getLogger(ExtendedFastScatterPlot.class);

	/**
    *
    */
	private static final long	serialVersionUID	= 1L;

	int[]											sizes;
	Paint[]										colors;
	PointShape[]							shapes;
	Double[]									alpha;
	List<Point2D>							polygon;

	public ExtendedFastScatterPlot(float[][] data, NumberAxis domainAxis, NumberAxis rangeAxis, int[] sizes, Paint[] colors, PointShape[] shapes,
			List<Point2D> polygon) {
		super(data, domainAxis, rangeAxis);
		this.sizes = sizes;
		this.colors = colors;
		this.shapes = shapes;
		this.polygon = polygon;

	}

	@Override
	public void render(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, CrosshairState crosshairState) {
		// g2.setPaint(Color.BLUE);

		if (this.getData() != null) {
			for (int i = 0; i < this.getData()[0].length; i++) {
				float x = this.getData()[0][i];
				float y = this.getData()[1][i];
				int size = this.sizes[i];
				int transX = (int) this.getDomainAxis().valueToJava2D(x, dataArea, RectangleEdge.BOTTOM);
				int transY = (int) this.getRangeAxis().valueToJava2D(y, dataArea, RectangleEdge.LEFT);
				g2.setPaint(this.colors[i]);
				switch (this.shapes[i]) {
					case RECTANGLE:
						g2.fillRect(transX, transY, size, size);
						break;
					case CIRCLE:
						g2.fillOval(transX, transY, size, size);
						break;
					case TRIANGLE_DOWN:
						g2.fillPolygon(new int[] { transX - size, transX, transX + size }, new int[] { transY, transY - size, transY }, 3);
						break;
					case TRIANGLE_UP:
						g2.fillPolygon(new int[] { transX - size, transX, transX + size }, new int[] { transY, transY + size, transY }, 3);
						break;
					case CROSS:
						g2.drawLine((int) transX - size, (int) transY - size, (int) transX + size, (int) transY + size);
						g2.drawLine((int) transX + size, (int) transY - size, (int) transX - size, (int) transY + size);
						break;
					case PLUS:
						g2.drawLine((int) transX - size, (int) transY, (int) transX + size, (int) transY);
						g2.drawLine((int) transX, (int) transY - size, (int) transX, (int) transY + size);
						break;
					case STAR:
						g2.drawLine((int) transX - size, (int) transY - size, (int) transX + size, (int) transY + size);
						g2.drawLine((int) transX + size, (int) transY - size, (int) transX - size, (int) transY + size);
						g2.drawLine((int) transX - size, (int) transY, (int) transX + size, (int) transY);
						g2.drawLine((int) transX, (int) transY - size, (int) transX, (int) transY + size);
						break;
				}
			}
		}
		g2.setPaint(Color.BLACK);
		for (int i = 0; i < polygon.size() - 1; i++) {
			int x1 = (int) this.getDomainAxis().valueToJava2D(polygon.get(i).getX(), dataArea, RectangleEdge.BOTTOM);
			int y1 = (int) this.getRangeAxis().valueToJava2D(polygon.get(i).getY(), dataArea, RectangleEdge.LEFT);

			int x2 = (int) this.getDomainAxis().valueToJava2D(polygon.get(i + 1).getX(), dataArea, RectangleEdge.BOTTOM);
			int y2 = (int) this.getRangeAxis().valueToJava2D(polygon.get(i + 1).getY(), dataArea, RectangleEdge.LEFT);

			g2.drawLine(x1, y1, x2, y2);
		}
		if (polygon.size() > 1) {
			int x1 = (int) this.getDomainAxis().valueToJava2D(polygon.get(0).getX(), dataArea, RectangleEdge.BOTTOM);
			int y1 = (int) this.getRangeAxis().valueToJava2D(polygon.get(0).getY(), dataArea, RectangleEdge.LEFT);

			int x2 = (int) this.getDomainAxis().valueToJava2D(polygon.get(polygon.size() - 1).getX(), dataArea, RectangleEdge.BOTTOM);
			int y2 = (int) this.getRangeAxis().valueToJava2D(polygon.get(polygon.size() - 1).getY(), dataArea, RectangleEdge.LEFT);

			g2.drawLine(x1, y1, x2, y2);
		}
	}
}
