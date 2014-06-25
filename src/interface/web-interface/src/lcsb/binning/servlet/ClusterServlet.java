package lcsb.binning.servlet;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lcsb.binning.data.Cluster;
import lcsb.binning.data.DataSet;
import lcsb.binning.data.Sequence;
import lcsb.binning.service.ClusterFactory;
import lcsb.binning.service.DataSetFactory;

import org.apache.log4j.Logger;

public class ClusterServlet extends HttpServlet {
	private Logger								logger						= Logger.getLogger(ClusterServlet.class.getName());

	/**
	 * 
	 */
	private static final long			serialVersionUID	= -6866052231469707463L;

	String												localPath					= null;

	private Map<String, DataSet>	datasets					= new HashMap<String, DataSet>();

	public void init(final ServletConfig config) {
		localPath = config.getServletContext().getRealPath("/");
		logger.debug("init");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		// HttpSession session = request.getSession(true);
		response.setContentType("text/html");
		String id = request.getParameter("id");
		String pointsStr = request.getParameter("points");

		PrintWriter out;
		try {
			out = response.getWriter();
			DataSet dataset = datasets.get(id);
			if (dataset == null) {
				try {
					dataset = DataSetFactory.createDataSetFromPointFile(localPath + "/" + id + "/points.txt", localPath + "/" + id + "/labels.txt",10);
					datasets.put(id, dataset);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error(e.getMessage(), e);
				}
			}
			if (dataset != null && pointsStr != null) {
				List<Point2D> points = new ArrayList<Point2D>();
				String pointArray[] = pointsStr.split(";");
				for (String string : pointArray) {
					String tmp[] = string.split(",");
					if (tmp.length > 0) {
						try {
							double x = Double.parseDouble(tmp[0]) - 400;
							double y = Double.parseDouble(tmp[1]) - 400;
							Point2D point = new Point2D.Double(x, y);
							points.add(point);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				Cluster cluster = ClusterFactory.createClusterFromPolygon(dataset, points);
				out.write("<h2>" + cluster.getElements().size() + " elements selected</h2>");
				out.write("<p>");
				DecimalFormat df = new DecimalFormat("#.##");
				for (Sequence sequence : cluster.getElements()) {
					out.write(sequence.getId() + ": " + df.format(sequence.getLocation().getX() / 10) + ", " + df.format(sequence.getLocation().getY() / 10) + "<br/>");
				}
				out.write("</p>");
			} else {
				out.write("<p>Internal server error...</p>");
			}

			out.close();
		} catch (IOException e1) {
			logger.error(e1.getMessage());
			e1.printStackTrace();
			return;
		}
	}

}