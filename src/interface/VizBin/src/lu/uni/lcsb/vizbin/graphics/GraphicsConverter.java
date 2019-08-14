package lu.uni.lcsb.vizbin.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import lu.uni.lcsb.vizbin.data.DataSet;
import lu.uni.lcsb.vizbin.data.Sequence;

public abstract class GraphicsConverter {
	public enum Shape {
		DOT, PLUS, ASTERISK, CROSS
	}

	Logger							logger		= Logger.getLogger(GraphicsConverter.class);
	static final double	pointSize	= 1;
	static final double	lineSize	= 2;
	DataSet							dataSet;

	public GraphicsConverter(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	protected void drawDataSet(double x, double y, double scale, int size) {
		Graphics2D graphics = getGraphics();

		// turn on antialiasing
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// set scale
		graphics.scale(scale, scale);

		// move the upper left corner to coordinates
		graphics.translate(-x, -y);

		graphics.fillRect((int) (x), (int) (y), (int) (size / scale), (int) (size / scale));

		Rectangle rect = new Rectangle((int) x - 10, (int) y - 10, (int) (size / scale) + 20, (int) (size / scale) + 20);

		for (Sequence sequence : dataSet.getSequences()) {
			if (rect.contains(sequence.getLocation()))
				drawSequence(sequence);
		}
	}

	private void drawSequence(Sequence sequence) {
		Graphics2D graphics = getGraphics();
		Integer id = sequence.getLabelId();
		Point2D location = sequence.getLocation();

		drawPoint(graphics, id, location);

	}

	protected void drawPoint(Graphics2D graphics, Integer id, Point2D location) {
		Color color = getColor(id);
		graphics.setColor(color);
		Shape shape = getShape(id);

		switch (shape) {
			case DOT:
				graphics.drawOval((int) (location.getX() - pointSize), (int) (location.getY() - pointSize), (int) (2 * pointSize), (int) (2 * pointSize));
				graphics.fillOval((int) (location.getX() - pointSize), (int) (location.getY() - pointSize), (int) (2 * pointSize), (int) (2 * pointSize));
				break;
			case ASTERISK:
				graphics.drawLine((int) (location.getX() - lineSize), (int) (location.getY() - lineSize), (int) (location.getX() + lineSize),
						(int) (location.getY() + lineSize));
				graphics.drawLine((int) (location.getX() + lineSize), (int) (location.getY() - lineSize), (int) (location.getX() - lineSize),
						(int) (location.getY() + lineSize));
				graphics.drawLine((int) (location.getX() - lineSize), (int) (location.getY()), (int) (location.getX() + lineSize), (int) (location.getY()));
				graphics.drawLine((int) (location.getX()), (int) (location.getY() - lineSize), (int) (location.getX()), (int) (location.getY() + lineSize));
				break;
			case CROSS:
				graphics.drawLine((int) (location.getX() - lineSize), (int) (location.getY() - lineSize), (int) (location.getX() + lineSize),
						(int) (location.getY() + lineSize));
				graphics.drawLine((int) (location.getX() + lineSize), (int) (location.getY() - lineSize), (int) (location.getX() - lineSize),
						(int) (location.getY() + lineSize));
				break;
			case PLUS:
				graphics.drawLine((int) (location.getX() - lineSize), (int) (location.getY()), (int) (location.getX() + lineSize), (int) (location.getY()));
				graphics.drawLine((int) (location.getX()), (int) (location.getY() - lineSize), (int) (location.getX()), (int) (location.getY() + lineSize));
				break;
		}
	}

	private Shape getShape(Integer labelId) {
		Integer id = labelId % 4;
		switch (id) {
			case 0:
				return Shape.DOT;
			case 1:
				return Shape.CROSS;
			case 2:
				return Shape.PLUS;
			case 3:
				return Shape.ASTERISK;
		}
		return null;
	}

	private Color getColor(Integer labelId) {
		switch (labelId % 11) {
			case (0):
				return Color.BLUE;
			case (1):
				return Color.RED;
			case (2):
				return Color.GREEN;
			case (3):
				return Color.CYAN;
			case (4):
				return Color.LIGHT_GRAY;
			case (5):
				return Color.MAGENTA;
			case (6):
				return Color.ORANGE;
			case (7):
				return Color.BLACK;
			case (8):
				return Color.PINK;
			case (9):
				return Color.GRAY;
			case (10):
				return Color.DARK_GRAY;
		}
		return null;
	}

	protected abstract Graphics2D getGraphics();

	protected void drawLegend(int width, int height) {
		Graphics2D graphics = getGraphics();

		// turn on antialiasing
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		graphics.fillRect(0, 0, (int) width, (int) height);

		Set<Integer> labels = new HashSet<Integer>();
		int posy = 10;
		for (Sequence sequence : dataSet.getSequences()) {
			if (!labels.contains(sequence.getLabelId())) {
				drawPoint(graphics, sequence.getLabelId(), new Point2D.Double(10, posy));
				posy += 5;
				if (!sequence.getLabelName().equals("")) {
					graphics.drawString(sequence.getLabelName(), 20, posy);
				} else {
					graphics.drawString("GENERAL SET", 20, posy);
				}
				posy += 10;
				labels.add(sequence.getLabelId());
			}
		}
	}

}
