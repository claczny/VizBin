package lcsb.binning.servlet;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.PropertyConfigurator;

/**
 * This servlet is supposed to be run only once at the beginning. The only
 * purpose of this class is to turn on logging information.
 * 
 * @author Piotr Gawron
 * 
 */
public class Log4jInit extends HttpServlet {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -9016017987887520957L;

	public void init() {
		String prefix = getServletContext().getRealPath("/");
		String file = getInitParameter("log4j-init-file");

		// if the log4j-init-file context parameter is not set, then no point in
		// trying
		if (file != null) {
			PropertyConfigurator.configure(prefix + file);
			System.out.println("Log4J Logging started: " + prefix + file);
		} else {
			System.out.println("Log4J Is not configured for your Application: " + prefix + file);
		}
	}
}