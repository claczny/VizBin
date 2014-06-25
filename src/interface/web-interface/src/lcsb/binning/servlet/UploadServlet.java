package lcsb.binning.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import lcsb.binning.data.DataSet;
import lcsb.binning.graphics.PngGraphicsConverter;
import lcsb.binning.service.DataSetFactory;
import lcsb.binning.service.utils.DataSetUtils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

@MultipartConfig
public class UploadServlet extends HttpServlet {
	private Logger						logger						= Logger.getLogger(UploadServlet.class.getName());

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6866052231469707463L;

	String										localPath					= null;

	public void init(final ServletConfig config) {
		logger.debug("Init upload servlet");
		localPath = config.getServletContext().getRealPath("/");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Part filePart = request.getPart("file");

			InputStream labelsIS = null;
			Part labelsPart = request.getPart("labels");
			if (labelsPart != null) {
				String lFileName = getFilename(labelsPart);
				if (lFileName != null && !lFileName.equals("")) {
					labelsIS = labelsPart.getInputStream();
				}
			}

			if (filePart == null) {
				response.sendRedirect("index.html");
			} else {
				String filename = getFilename(filePart);
				if (filename.equals("")) {
					response.sendRedirect("index.html");
				} else {
					int kMerLength = 5;
					int pcaColumns = 50;
					double theta = 0.5;
					double perplexity = 30;
					boolean merge = false;

					String string = request.getParameter("kmer");
					try {
						kMerLength = Integer.parseInt(string);
					} catch (Exception e) {
						logger.warn("Invalid kmer value: " + string + ". Default value used");
					}

					string = request.getParameter("pcaColumns");
					try {
						pcaColumns = Integer.parseInt(string);
					} catch (Exception e) {
						logger.warn("Invalid pcaColumns: " + string + ". Default value used");
					}

					string = request.getParameter("theta");
					try {
						theta = Double.parseDouble(string);
					} catch (Exception e) {
						logger.warn("Invalid theta value: " + string + ". Default value used");
					}

					string = request.getParameter("perplexity");
					try {
						perplexity = Double.parseDouble(string);
					} catch (Exception e) {
						logger.warn("Invalid perplexity value: " + string + ". Default value used");
					}

					string = request.getParameter("merge");
					try {
						int i = Integer.parseInt(string);
						if (i == 0)
							merge = false;
						else
							merge = true;
					} catch (Exception e) {
						logger.warn("Invalid merge value: " + string + ". Default value used");
					}

					InputStream pointInputStream = filePart.getInputStream();
					logger.debug(localPath);
					File myTempDir = File.createTempFile("map", "", new File(localPath));
					myTempDir.delete();
					myTempDir.mkdirs();
					String directory = myTempDir.getAbsolutePath();

					FileUtils.copyDirectory(new File(localPath + "/template"), new File(directory));

					logger.debug("Loading fasta file: " + filename);
					DataSet dataSet = DataSetFactory.createDataSetFromFastaFile(pointInputStream);

					logger.debug("DataSet loaded (" + dataSet.getSequences().size() + " sequences)");
					logger.debug("Creating kmers (k=" + kMerLength + ", merge = " + merge + ")");
					DataSetUtils.createKmers(dataSet, kMerLength, merge);
					logger.debug("Normalizing vectors...");
					DataSetUtils.normalizeDescVectors(dataSet, kMerLength);
					logger.debug("Clr normalization...");
					DataSetUtils.createClrData(dataSet);
					logger.debug("Starting PCA...");
					DataSetUtils.computePca(dataSet, pcaColumns);
					logger.debug("T-SNE...");
					DataSetUtils.runTsneAndPutResultsToDir(dataSet, directory, theta, perplexity);
					logger.debug("Points created.");

					dataSet = DataSetFactory.createDataSetFromPointFile(new FileInputStream(directory + "/points.txt"), labelsIS, 10);
					logger.debug("Let's create png files....");
					PngGraphicsConverter converter = new PngGraphicsConverter(dataSet);
					converter.createPngDirectory(directory + "/images/", 2);

					logger.debug("Redirect user: " + myTempDir.getName());
					response.sendRedirect(myTempDir.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			response.sendRedirect("error.html");
		}
	}

	private static String getFilename(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE
																																																						// fix.
			}
		}
		return null;
	}
}