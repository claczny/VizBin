/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.lcsb.vizbin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import lcsb.vizbin.data.Cluster;
import lcsb.vizbin.data.DataSet;
import lcsb.vizbin.data.Sequence;
import lcsb.vizbin.service.ClusterFactory;
import lcsb.vizbin.service.utils.DataSetUtils;

public class ClusterPanel extends JPanel implements NotificationListener {
	private final int SIZE = 2; // radius of each dot

	private ArrayList<Sequence> pointList;// ArrayList<Point> pointList;
	private List<Point2D> polygonPoints;
	private ClusterFactory CSy;
	private Cluster selectedCluster;

	private DataSet dataSet;
	private String inFileName;
	private boolean drawAxes;

	private JFrame parentFrame;
	
	//Tomasz Sternal - removed static initialization to +-400. It caused some points were not displayed
	private int startX;
	private int startY;
	private int endX;
	private int endY;

	private int maxUnitIncrement = 1;

	private Point startDragPoint;

	// -----------------------------------------------------------------
	// Takes in as input the points to be displayed
	// -----------------------------------------------------------------
	public ClusterPanel(DataSet ds, String _inFileName, JFrame _parentFrame,
			boolean _drawAxes)// (ArrayList<Sequence> seqs)//(ArrayList<Point>
								// points)
	{
		// pointList = new ArrayList<Point>();
		CSy = new ClusterFactory();
		polygonPoints = new ArrayList<Point2D>();
		pointList = (ArrayList<Sequence>) ds.getSequences();// seqs;//points;
		dataSet = ds;// new DataSet();
		inFileName = _inFileName;
		parentFrame = _parentFrame;
		drawAxes = _drawAxes;
		
		//Tomasz Sternal - we find minimum and maximum in every direction to scale data so that it fits window
		double minX = 0;
		double maxX = 0;
		double minY = 0;
		double maxY = 0;
		
		for(Sequence seq : pointList ){
			if(seq.getLocation().getX() < minX) minX=seq.getLocation().getX();
			if(seq.getLocation().getX() > maxX) maxX=seq.getLocation().getX();
			if(seq.getLocation().getY() < minY) minY=seq.getLocation().getY();
			if(seq.getLocation().getY() > maxY) maxY=seq.getLocation().getY();
		}
		
		//Tomasz Sternal - initialization and setting margins
		int margin = 50;		
		startX = (int) minX - margin;
		startY = (int) minY - margin;
		endX = (int) maxX + margin;
		endY = (int) maxY + margin;
		
		// Tomasz Sternal - communication between DataSetUtils and ClusterPannel about pointList
		// and polygonPoints is completely unnecessary 
		//DataSetUtils.setPointList(pointList);
		//DataSetUtils.setPolygonPoints(new ArrayList<Point2D>());
		// dataSet.setSequences(seqs);
		// randomized_points();
		addMouseListener(new DotsListener());

		setBackground(Color.black); // Sets the background color
		// setPreferredSize (new Dimension(1366, 705));
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1000, 1000));
	}

	// -----------------------------------------------------------------
	// Draws all of the dots stored in the list.
	// -----------------------------------------------------------------
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Tomasz Sternal - communication between DataSetUtils and ClusterPannel about pointList
		// and polygonPoints is completely unnecessary 
		//polygonPoints = DataSetUtils.getPolygonPoints();
		//pointList = DataSetUtils.getPointList();
		
		Graphics2D page = (Graphics2D) g;
		page.setColor(Color.white);
		// drawLegend(1366, 705, page); if want to try this method comment the
		// ones labeled with drawLegend comment
		// --drawLegendComment
		// turn on antialiasing
		page.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		page.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// --drawLegendComment

		// --page.setColor (Color.green);
		// if (pointList.size()>10 && pointList.size()<20)
		// page.setColor (Color.red);

		// for (Point spot : pointList)
		// page.fillOval (spot.x-SIZE, spot.y-SIZE, SIZE*2, SIZE*2);

		// uncomment this to go to older version
		/*
		 * for (int i=0; i<pointList.size();i++){ if (i>=1100) page.setColor
		 * (Color.red); page.fillOval(pointList.get(i).x-SIZE,
		 * pointList.get(i).y-SIZE, SIZE*2, SIZE*2); // can be replaced by
		 * drawPoint in their code or just call draw sequence }
		 */

		// --drawLegendComment
		// recomment this to get older version
		// --for(int i=0;i<pointList.size();i++)
		// --drawSequence(pointList.get(i), page);
		// --drawLegendComment
		// drawLegend(1366, 705, page);

		// --drawDataSet
		/*
		 * Made the following variables global to enable dragging int startX =
		 * -400; int startY = -400; int endX = 400; int endY = 400;
		 */
		
		//Tomasz Sternal - this code looks weird... j,k are always equal zero, zoom factor is always equal 1 ??
		int j = 0;
		int k = 0;

		int size = Math.max(endX - startX, endY - startY);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		double wh = width * height;
		int tileSize = (int) wh;// 941880;
		double zoomFactor = 1;
		
		drawDataSet(startX + tileSize * j / zoomFactor, startY + tileSize * k
				/ zoomFactor, zoomFactor, tileSize, page);// zoomDirStr + (j) +
															// "," + (k) +
															// ".PNG");
		
		/*
		//Tomasz Sternal - new version
		double scaleX = width/(endX-startX);
		double scaleY = height/(endY-startY);
		drawDataSet(startX, startY, scaleX, scaleY, tileSize, page);
		*/
		
		// drawDataSet(1336, 705, zoomFactor, 941880, page);
		// --drawDaraSet
		// --page.setColor(Color.black);
		// --page.drawString ("Count: " + pointList.size(), 5, 15);
		page.translate(startX + tileSize * j / zoomFactor, startY + tileSize
				* k / zoomFactor);
		// page.setColor(Color.BLACK);
		// Disabled axes painting until we make it into a configurable option
		if (drawAxes)
			paintAxes(page);
		drawPolygonPoints(page);
		drawConnection(page);
		drawCluster(page);

	}

	// ---------------------------------------------
	// Draws the points clicked by the user
	// ---------------------------------------------
	private void drawPolygonPoints(Graphics2D page) {
		page.setColor(Color.red);
		for (int i = 0; i < polygonPoints.size(); i++) {
			int x = (int) polygonPoints.get(i).getX() - SIZE;
			int y = (int) polygonPoints.get(i).getY() - SIZE;
			page.fillOval(x, y, SIZE * 2, SIZE * 2);

		}
	}

	// -------------------------------------------------------
	// Draws the linesconnecting the points drawn by the user
	// -------------------------------------------------------
	private void drawConnection(Graphics2D page) {
		page.setColor(Color.red);
		int ssize = polygonPoints.size();
		for (int i = 0; i < ssize; i++) {
			if (i + 1 < ssize) {
				int x_p1 = (int) polygonPoints.get(i).getX() - SIZE;
				int y_p1 = (int) polygonPoints.get(i).getY() - SIZE;
				int x_p2 = (int) polygonPoints.get(i + 1).getX() - SIZE;
				int y_p2 = (int) polygonPoints.get(i + 1).getY() - SIZE;
				// int width = x_p2 - x_p1;
				// int height = 1;
				// page.fillRect(x_p1, y_p1, width, height);
				Graphics g = page;
				g.drawLine(x_p1, y_p1, x_p2, y_p2);
			} else {
				int x_p1 = (int) polygonPoints.get(i).getX() - SIZE;
				int y_p1 = (int) polygonPoints.get(i).getY() - SIZE;
				int x_p2 = (int) polygonPoints.get(0).getX() - SIZE;
				int y_p2 = (int) polygonPoints.get(0).getY() - SIZE;
				// int width = x_p2 - x_p1;
				// int height = 1;
				// page.fillRect(x_p1, y_p1, width, height);
				Graphics g = page;
				g.drawLine(x_p1, y_p1, x_p2, y_p2);
			}

			// Graphics g = page;
			// g.drawLine(x_p1, y_p1, x_p2, y_p2);
		}

	}

	// ---------------------------------------------
	// Draws the cluster inside the polygon
	// ---------------------------------------------
	private void drawCluster(Graphics2D page) {
		List<Point2D> polygonPnts = new ArrayList<Point2D>();
		for (int i = 0; i < polygonPoints.size(); i++) {
			double x = polygonPoints.get(i).getX() + startX;
			double y = polygonPoints.get(i).getY() + startY;
			Point2D point = new Point2D.Double(x, y);
			polygonPnts.add(point);
		}
		if (!polygonPnts.isEmpty())
			polygonPnts.add(polygonPnts.get(0));
		/*
		 * for(int i=0; i< dataSet.getSequences().size();i++){
		 * System.out.println(dataSet.getSequences().get(i).getLocation()); }
		 */
		selectedCluster = CSy.createClusterFromPolygon(dataSet, polygonPnts);

		/*
		 * for(int i=0;i<selectedCluster.getElements().size();i++){ Sequence seq
		 * = selectedCluster.getElements().get(i); // removed ID changing, was
		 * used for coloring.. //seq.setLabelId(2623); drawSequence(seq, page);
		 * repaint();
		 * //System.out.println(Cer.getElements().get(i).getLocation()); }
		 */
	}

	// -----------------------------------------------------------------
	// Draws the x and y axes.
	// -----------------------------------------------------------------
	// can use the params to set the points for the lines
	// can get the params according to the window size and draw the axes
	// accordingly
	public void paintAxes(Graphics g/*
									 * ,int x_upperLeft, int y_upperLeft,int
									 * x_lowerLeft, int y_lowerLeft,int
									 * x_lowerRight, int y_lowerRight
									 */) {
		// Points for the Y axis
		int x_upperLeft = 40;
		int y_upperLeft = 0;
		int x_lowerLeft = 40;
		int y_lowerLeft = this.getHeight() - 40; // also used for X axis

		// Points for the X axis
		int x_lowerRight = this.getWidth();
		int y_lowerRight = this.getHeight() - 40;

		// Points for dashes on the Y axis
		int x1_YDash = x_upperLeft - 2;
		int x2_YDash = x_upperLeft + 2; // The length of the dashes is 4

		// Points for dashes on the X axis
		int y1_XDash = y_lowerLeft - 2;
		int y2_XDash = y_lowerLeft + 2; // The length of the dashes is 4

		g.drawLine(x_upperLeft, y_upperLeft, x_lowerLeft, y_lowerLeft); // Y
																		// axis
		g.drawLine(x_lowerRight, y_lowerRight, x_lowerLeft, y_lowerLeft); // X
																			// axis

		// Loop that draws the Y dashes
		int i_add = this.getHeight() / 10;
		double yd_off = i_add * 0.08; // this is to place the numbers in a
										// better position
		int y_off = (int) yd_off;
		Y_range_l = startY;
		Y_range_h = endY; // so here I am overwriting the range so tht it wld be
							// from -400 to 400
		for (int i = Y_range_l; i <= Y_range_h; i += 50) {
			// here the range is ending to this.getHeight()-40 since the axis is
			// drawn till this.getHeight()-40
			// so we get a range from 40 to this.getWidth()
			int y_i = (int) Remap(i, Y_range_l, Y_range_h, 0,
					this.getHeight() - 40);
			g.drawLine(x1_YDash, y_i, x2_YDash, y_i);
			int ii = -i;
			g.drawString("" + ii, x1_YDash - 30, y_i + y_off);
		}

		// Loop that draws the X dashes
		i_add = this.getWidth() / 10;
		double xd_off = i_add * 0.12; // this is to place the numbers in a
										// better position
		int x_off = (int) xd_off;
		X_range_l = startX;
		X_range_h = endX; // so here I am overwriting the range so tht it wld be
							// from -400 to 400
		for (int i = X_range_l; i <= X_range_h; i += 50) {
			// here the range is starting from 40 since the axis is not drawn
			// from 0, it's drawn from 40
			// so we get a range from 40 to this.getWidth()
			int x_i = (int) Remap(i, X_range_l, X_range_h, 40, this.getWidth());
			g.drawLine(x_i, y1_XDash, x_i, y2_XDash);
			if (i == 0) {
				xd_off = i_add * 0.02;
				x_off = (int) xd_off;
			}
			if (i == 50 || i == -50) {
				xd_off = i_add * 0.08;
				x_off = (int) xd_off;
			}
			g.drawString("" + i, x_i - x_off, y1_XDash + 20);
		}
	}

	// ---------------------------------------------------------------
	// Remaps a value from 1 range to another
	// ---------------------------------------------------------------
	public double Remap(double value, double from1, double to1, double from2,
			double to2) {

		return (value - from1) / (to1 - from1) * (to2 - from2) + from2;

	}

	// ---------------------------------------------------------------
	// This is their code!
	// ---------------------------------------------------------------
	static final double pointSize = 1;
	static final double lineSize = 2;
	// new 12/18/2013 to get min and max range for x and y
	int X_range_h = 0;
	int X_range_l = Integer.MAX_VALUE;
	int Y_range_h = 0;
	int Y_range_l = Integer.MAX_VALUE;

	// new 12/18/2013 to get min and max range for x and y
	protected void drawDataSet(double x, double y, double scale, int size,
			Graphics2D graphics) {
	
	
//	//	Tomasz Sternal - new version
//	protected void drawDataSet(double x, double y, double scaleX, double scaleY, int size,
//			Graphics2D graphics) {
		// new 12/18/2013
		X_range_h = 0;
		// new 12/18/2013
		// Graphics2D graphics = getGraphics();

		// turn on antialiasing
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// set scale
		graphics.scale(scale, scale);

		// move the upper left corner to coordinates
		graphics.translate(-x, -y);

		graphics.fillRect((int) (x), (int) (y), (int) (size / scale),
				(int) (size / scale));

		Rectangle rect = new Rectangle((int) x - 10, (int) y - 10,
				(int) (size / scale) + 20, (int) (size / scale) + 20);
		for (Sequence sequence : dataSet.getSequences()) {
			if (rect.contains(sequence.getLocation())) {
				// new 12/18/2013 to get min and max range for x and y
				//Tomasz Sternal - this seems unnecessary
				/*
				if (sequence.getLocation().getX() > X_range_h)
					X_range_h = (int) sequence.getLocation().getX();
				if (sequence.getLocation().getX() < X_range_l)
					X_range_l = (int) sequence.getLocation().getX();
				if (sequence.getLocation().getY() > Y_range_h)
					Y_range_h = (int) sequence.getLocation().getY();
				if (sequence.getLocation().getY() < Y_range_l)
					Y_range_l = (int) sequence.getLocation().getY();
				*/
				// new 12/18/2013 to get min and max range for x and y
				drawSequence(sequence, graphics);
			}
		}
	}

	public void drawSequence(Sequence sequence, Graphics2D graphics) {
		// Graphics2D graphics = (Graphics2D) getGraphics();
		Integer id = sequence.getLabelId();
		Point2D location = sequence.getLocation();
		drawPoint(graphics, id, location);
	}

	protected void drawPoint(Graphics2D graphics, Integer id, Point2D location) {

		Color color = getColor(id);
		graphics.setColor(color);
		int shape = getShape(id);

		switch (shape) {
		case 0:// DOT:
			graphics.drawOval((int) (location.getX() - pointSize),
					(int) (location.getY() - pointSize), (int) (2 * pointSize),
					(int) (2 * pointSize));
			graphics.fillOval((int) (location.getX() - pointSize),
					(int) (location.getY() - pointSize), (int) (2 * pointSize),
					(int) (2 * pointSize));
			break;
		case 1:// ASTERISK:
			graphics.drawLine((int) (location.getX() - lineSize),
					(int) (location.getY() - lineSize),
					(int) (location.getX() + lineSize),
					(int) (location.getY() + lineSize));
			graphics.drawLine((int) (location.getX() + lineSize),
					(int) (location.getY() - lineSize),
					(int) (location.getX() - lineSize),
					(int) (location.getY() + lineSize));
			graphics.drawLine((int) (location.getX() - lineSize),
					(int) (location.getY()),
					(int) (location.getX() + lineSize), (int) (location.getY()));
			graphics.drawLine((int) (location.getX()),
					(int) (location.getY() - lineSize),
					(int) (location.getX()), (int) (location.getY() + lineSize));
			break;
		case 2:// CROSS:
			graphics.drawLine((int) (location.getX() - lineSize),
					(int) (location.getY() - lineSize),
					(int) (location.getX() + lineSize),
					(int) (location.getY() + lineSize));
			graphics.drawLine((int) (location.getX() + lineSize),
					(int) (location.getY() - lineSize),
					(int) (location.getX() - lineSize),
					(int) (location.getY() + lineSize));
			break;
		case 3:// PLUS:
			graphics.drawLine((int) (location.getX() - lineSize),
					(int) (location.getY()),
					(int) (location.getX() + lineSize), (int) (location.getY()));
			graphics.drawLine((int) (location.getX()),
					(int) (location.getY() - lineSize),
					(int) (location.getX()), (int) (location.getY() + lineSize));
			break;
		}
	}

	private int getShape(Integer labelId) {
		Integer id = labelId % 4;
		switch (id) {
		case 0:
			return 0;// Shape.DOT;
		case 1:
			return 1;// Shape.CROSS;
		case 2:
			return 2;// Shape.PLUS;
		case 3:
			return 3;// Shape.ASTERISK;
		}
		return -1;
	}

	private Color getColor(Integer labelId) {
		switch (labelId % 11) {
		case (0):
			return Color.BLUE;
		case (1):
			return new Color(0x8A3324);
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

	protected void drawLegend(int width, int height, Graphics2D graphics) {
		// Graphics2D graphics = getGraphics();

		// turn on antialiasing
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		graphics.fillRect(0, 0, (int) width, (int) height);

		Set<Integer> labels = new HashSet<Integer>();
		int posy = 10;
		for (Sequence sequence : dataSet.getSequences()) {
			if (!labels.contains(sequence.getLabelId())) { // This statement
															// will always be
															// true first
															// iteration!
				drawPoint(graphics, sequence.getLabelId(), /*
															 * sequence.getLocation
															 * ()
															 */
						new Point2D.Double(10, posy)); // why new
														// Point2D.Double(10,
														// posy) ? :/
				posy += 5;
				if (!sequence.getLabelName().equals("")) {
					graphics.drawString(sequence.getLabelName(), /*
																 * (int)
																 * sequence
																 * .getLocation
																 * ().getX(),
																 * (int)
																 * sequence
																 * .getLocation
																 * ().getY()
																 */20, posy); // why
																				// 20,
																				// posy?
				} else {
					graphics.drawString("GENERAL SET", /*
														 * (int)
														 * sequence.getLocation
														 * ().getX(), (int)
														 * sequence
														 * .getLocation().getY()
														 */20, posy); // why 20,
																		// posy?
				}
				posy += 10;
				labels.add(sequence.getLabelId()); // adds a labelId which
													// previously was not in
													// Labels
			}
		}
	}

	@Override
	public void notifyObserver() {

		repaint();

	}

	// end of their code

	// *****************************************************************
	// Represents the listener for mouse events.
	// *****************************************************************
	private class DotsListener implements MouseListener {
		// --------------------------------------------------------------
		// Adds the current point to the list of points and redraws
		// the panel whenever the mouse button is pressed.
		// --------------------------------------------------------------
		public void mousePressed(MouseEvent event) {
			if (SwingUtilities.isMiddleMouseButton(event)) {
				startDragPoint = event.getPoint();
			} else if (SwingUtilities.isLeftMouseButton(event)) {
				// pointList.add(event.getPoint());
				/*
				 * double x = event.getPoint().x - 400; double y =
				 * event.getPoint().y - 400; Point2D point = new
				 * Point2D.Double(x, y); polygonPoints.add(point);
				 */
				int pos = -1;
				for (int i = 0; i < polygonPoints.size(); i++) {
					if (polygonPoints.get(i).getX() + 3 > event.getPoint()
							.getX()
							&& polygonPoints.get(i).getX() - 3 < event
									.getPoint().getX()
							&& polygonPoints.get(i).getY() + 3 > event
									.getPoint().getY()
							&& polygonPoints.get(i).getY() - 3 < event
									.getPoint().getY())
						pos = i;
				}
				if (pos == -1)
					polygonPoints.add((Point2D) event.getPoint());
				else
					polygonPoints.remove(pos);
				// Tomasz Sternal - communication between DataSetUtils and ClusterPannel about pointList
				// and polygonPoints is completely unnecessary 
				//DataSetUtils.setPolygonPoints(polygonPoints);
				//DataSetUtils.setPointList(pointList);
				
				NotificationCenter.notifyObservers();
				repaint();
			} else {
				if (SwingUtilities.isRightMouseButton(event)) {
					if (selectedCluster.getElements().size() > 0) {
						int option;
						option = JOptionPane
								.showConfirmDialog(
										null,
										selectedCluster.getElements().size()
												+ " sequences selected, export to file? \n\nPress Cancel to remove your selection.",
										"Export selected cluster",
										JOptionPane.YES_NO_CANCEL_OPTION);
						if (option == JOptionPane.YES_OPTION)
							DataExporter.exportCluster(parentFrame, inFileName,
									selectedCluster.getElements());
						else if (option == JOptionPane.CANCEL_OPTION) {
							polygonPoints.clear();
							
							// Tomasz Sternal - communication between DataSetUtils and ClusterPannel 
							// about pointList and polygonPoints is completely unnecessary 
							// DataSetUtils.setPolygonPoints(polygonPoints);
							NotificationCenter.notifyObservers();
							repaint();
						}
					}
				}
			}
		}

		public void mouseReleased(MouseEvent event) {
			if (SwingUtilities.isMiddleMouseButton(event)) {
				Point endDragPoint = event.getPoint();
				int diffX = startDragPoint.x - endDragPoint.x;
				int diffY = startDragPoint.y - endDragPoint.y;
				startX += diffX;
				startY += diffY;
				endX += diffX;
				endY += diffY;
				polygonPoints.clear();
				
				// Tomasz Sternal - communication between DataSetUtils and ClusterPannel about pointList
				// and polygonPoints is completely unnecessary 
				// DataSetUtils.setPolygonPoints(polygonPoints);
				NotificationCenter.notifyObservers();
				repaint();
			}
		}

		// --------------------------------------------------------------
		// Provide empty definitions for unused event methods.
		// --------------------------------------------------------------
		public void mouseClicked(MouseEvent event) {
		}

		public void mouseEntered(MouseEvent event) {
		}

		public void mouseExited(MouseEvent event) {
		}

	}
}
