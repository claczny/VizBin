package lu.uni.lcsb.vizbin;

import java.awt.Color;

public enum PointColor {
	BLACK(Color.BLACK), BLUE(Color.BLUE), RED(Color.RED), GREEN(Color.GREEN), ORANGE(Color.ORANGE);

	private Color	color;

	private PointColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the color
	 * @see #color
	 */
	public Color getColor() {
		return color;
	}
}
