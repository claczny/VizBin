package lcsb.binning.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lcsb.binning.data.DataSet;

public class PngGraphicsConverter extends GraphicsConverter {
	private static final int	MIN_ZOOM_LEVEL	= 6;
	protected BufferedImage		bi;
	protected Graphics2D			graphics2d;

	public PngGraphicsConverter(DataSet dataSet) {
		super(dataSet);
	}

	public void createPngFile(double x, double y, double scale, int size, String fileName) throws IOException {
		bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		graphics2d = bi.createGraphics();
		super.drawDataSet(x, y, scale, size);
		ImageIO.write(bi, "PNG", new File(fileName));
	}

	public void createPngLegend(int width, int height, String fileName) throws IOException {
		bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics2d = bi.createGraphics();
		super.drawLegend(width, height);
		ImageIO.write(bi, "PNG", new File(fileName));
	}

	public void createPngDirectory(String directory, int levels) throws IOException {
		int startX = -400;
		int startY = -400;
		int endX = 400;
		int endY = 400;

		int size = Math.max(endX - startX, endY - startY);

		int tileSize = 256;
		double zoomFactor = 1;
		for (int i = 0; i < levels; i++) {

			String zoomDirStr = directory + System.getProperty("file.separator") + (i + MIN_ZOOM_LEVEL) + System.getProperty("file.separator");
			File zoomDir = new File(zoomDirStr);
			zoomDir.mkdirs();
			int tiles = (int) (Math.pow(2, i) * size / tileSize);
			for (int j = 0; j <= tiles; j++)
				for (int k = 0; k <= tiles; k++) {
					createPngFile(startX + tileSize * j / zoomFactor, startY + tileSize * k / zoomFactor, zoomFactor, tileSize, zoomDirStr + (j) + "," + (k) + ".PNG");
				}
			zoomFactor *= 2;
		}
		logger.debug(dataSet.getLabelsCount());
		createPngLegend(200,dataSet.getLabelsCount()*15+10,directory + System.getProperty("file.separator")+"legend.PNG");
	}

	@Override
	protected Graphics2D getGraphics() {
		return graphics2d;
	}
}
