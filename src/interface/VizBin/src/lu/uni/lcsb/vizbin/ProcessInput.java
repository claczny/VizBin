package lu.uni.lcsb.vizbin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import lcsb.vizbin.data.DataSet;
import lcsb.vizbin.graphics.PngGraphicsConverter;
import lcsb.vizbin.service.DataSetFactory;
import lcsb.vizbin.service.InvalidMetaFileException;
import lcsb.vizbin.service.utils.DataSetUtils;
import lcsb.vizbin.service.utils.PcaType;

import org.apache.log4j.Logger;

/**
 * 
 * @author <a href="mailto:valentin.plugaru.001@student.uni.lu">Valentin
 *         Plugaru</a>
 */
public class ProcessInput {

	private Logger						logger				= Logger.getLogger(ProcessInput.class.getName());

	private String						indatafile, filteredSequencesFile, inpointsfile, inlabelsfile;
	private Integer						kMerLength, pcaColumns, contigLen, numThreads, seed;
	private PcaType						pcaAlgorithmType;
	private Double						theta, perplexity;
	private Boolean						merge;

	private volatile JLabel		label_status;
	private JProgressBar			progBar;
	private JTabbedPane				tabPane;
	private JFrame						parentFrame;
	private File							tsneCmd;
	private boolean						drawAxes;
	private boolean						log;

	private DataSet						dataSet_orig	= null;
	private DataSet						dataSet				= null;

	private volatile Integer	progressVal;

	private AtomicBoolean			processEnded;

	ProcessInput(String _indatafile, Integer _contigLen, Integer _numThreads, String _inpointsfile, String _inlabelsfile, Integer _kmer, Boolean _merge,
			Integer _pca, Double _theta, Double _perplexity, Integer _seed, JLabel _status, JProgressBar _progBar, JTabbedPane tabPane, JFrame _parentFrame,
			File _tsneCmd, boolean _drawAxes, PcaType pcaType, boolean _log) {
		logger.debug("Init of ProcessInput");
		indatafile = _indatafile;
		filteredSequencesFile = "filteredSequences.fa";
		inpointsfile = _inpointsfile;
		inlabelsfile = _inlabelsfile;
		contigLen = _contigLen;
		numThreads = _numThreads;
		kMerLength = _kmer;
		merge = _merge;
		pcaColumns = _pca;
		theta = _theta;
		perplexity = _perplexity;
		seed = _seed;
		label_status = _status;
		progBar = _progBar;
		this.tabPane = tabPane;
		parentFrame = _parentFrame;
		tsneCmd = _tsneCmd;
		drawAxes = _drawAxes;
		progressVal = 0;
		processEnded = new AtomicBoolean(true);
		pcaAlgorithmType = pcaType;
		log = _log;
	}

	void updateStatus(String message) {
		updateStatus(message, 0);
	}

	void updateStatus(final String message, int amount) {
		logger.debug(message);
		progressVal += amount;
		if (progressVal > 100)
			progressVal = 100;
		if (progressVal < 0)
			progressVal = 0;
		final AtomicInteger value = new AtomicInteger(progressVal);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				label_status.setText(message);
				progBar.setValue(value.get());
			}
		});

		// Do some work and update value.
	}

	public void doProcess() {
		new Thread() {
			public void run() {

				processEnded.set(false);
				String localPath = System.getProperty("java.io.tmpdir");
				logger.debug(localPath);
				try {
					File myTempDir = File.createTempFile("map", "", new File(localPath));
					myTempDir.delete();
					myTempDir.mkdirs();

					String directory = myTempDir.getAbsolutePath();
					filteredSequencesFile = directory + "/" + filteredSequencesFile;

					logger.debug("Loading data from file.\nContig length treshold: " + contigLen);
					updateStatus("Loading fasta file: " + indatafile, 5);
					dataSet = DataSetFactory.createDataSetFromFastaFile(indatafile, filteredSequencesFile, inlabelsfile, inpointsfile, contigLen, log);
					if (dataSet == null) {
						JOptionPane.showMessageDialog(null, "Error during loading data from given file! Check the logs.", "alert", JOptionPane.ERROR_MESSAGE);
						updateStatus("", -100);
						processEnded.set(true);
						return;
					}
					// for (int i=0;i<dataSet.getSequences().size(); i++)
					// System.out.println(dataSet.getSequences().get(i).getLabelName());
					updateStatus("DataSet loaded (" + dataSet.getSequences().size() + " sequences)");

					// If points file is provided, no calculations are needed
					if (inpointsfile.isEmpty()) {
						updateStatus("Creating kmers (k=" + kMerLength + ", merge = " + merge + ")");
						DataSetUtils.createKmers(dataSet, kMerLength, merge);
						updateStatus("Normalizing vectors...", 5);
						DataSetUtils.normalizeDescVectors(dataSet, kMerLength);
						updateStatus("Clr normalization...", 5);
						DataSetUtils.createClrData(dataSet);
						updateStatus("Running PCA... (" + pcaAlgorithmType.getName() + ")", 5);
						DataSetUtils.computePca(dataSet, pcaColumns, pcaAlgorithmType);
						updateStatus("Running T-SNE...", 15);
						DataSetUtils.runTsneAndPutResultsToDir(dataSet, numThreads, directory, theta, perplexity, seed, label_status, progBar, tsneCmd);
						progressVal = progBar.getValue();
						File f = new File(directory + "/points.txt");
						if (f.exists() && !f.isDirectory()) {
							updateStatus("Points created."); // 90% progress up
							// to// here
						} else {
							throw new FileNotFoundException("points.txt file not found. Probably bh_tsne binaries execution failed.");
						}

						inpointsfile = directory + "/points.txt";
					}

					File flabels = null;
					FileInputStream labelsIS = null;
					if (!inlabelsfile.equals("")) {
						try {
							flabels = new File(inlabelsfile);
							labelsIS = new FileInputStream(flabels);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					dataSet_orig = dataSet;
					int scale = 10; // Scaling is needed since AWT.Polygon() requires
													// int-coordinates for the polygon vertices.
					// Scaling by a factor of ten allows to zoom in and still get
					// meaningful int-coordinates form double points.
					// TODO: Refactor such that this is done internally in
					// ClusterFactory.createClusterFromPolygon()
					dataSet = DataSetFactory.createDataSetFromPointFile(new FileInputStream(inpointsfile), labelsIS, scale,log);
					updateStatus("Creating png files....", 5);
					PngGraphicsConverter converter = new PngGraphicsConverter(dataSet);
					converter.createPngDirectory(directory + "/images/", 2);
					updateStatus("Done.", 100); // 100% progress, make sure
					// progress bar is at 100

					// add label ID and name from initial dataset
					for (int i = 0; i < dataSet.getSequences().size(); i++) {
						dataSet.getSequences().get(i).setLabelId(dataSet_orig.getSequences().get(i).getLabelId());
						dataSet.getSequences().get(i).setLabelName(dataSet_orig.getSequences().get(i).getLabelName());
					}

					DataSetUtils.setDataSet(dataSet);
					DataSetUtils.setIsDataSetCreated(true);

					ClusterPanel cp = new ClusterPanel(dataSet, filteredSequencesFile, parentFrame, drawAxes);
					tabPane.setComponentAt(1, cp.getChartPanel());
					// NotificationCenter.addObserver(cp);
					// Show data points
					JFrame frame = new JFrame("Cluster");
					DataSetUtils.setDrawingFrame(frame);
					// frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
					ClusterPanel cpPopOut = new ClusterPanel(dataSet, filteredSequencesFile, parentFrame, drawAxes);
					// NotificationCenter.addObserver(cpPopOut);
					// frame.getContentPane().add(cpPopOut);

					frame.setSize(800, 600);
					JScrollPane scrPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					scrPane.getViewport().add(cpPopOut.getChartPanel());
					frame.setContentPane(scrPane);
					frame.setVisible(true);

				} catch (OutOfMemoryError e) {
					JOptionPane.showMessageDialog(parentFrame, "Error! Java machine ran out of memmory.\n" + "Check input file size, or increase java heap size.\n"
							+ "Application will now restart.");
					e.printStackTrace();
					restartApplication();
				} catch (InvalidMetaFileException e) {
					logger.error(e.getMessage(), e);
					updateStatus("Error! Check the logs.");
					JOptionPane.showMessageDialog(parentFrame, e.getMessage(), "Label file error", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
					updateStatus("Error! Check the logs.");
					logger.error(e.getMessage(), e);
				}
				processEnded.set(true);
			}
		}.start();
	}

	public void restartApplication() {
		final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		File currentJar = null;
		currentJar = new File(ProcessInput.class
				.getProtectionDomain().getCodeSource().getLocation().toString().replace("build/classes", "dist/VizBin-dist.jar ").replace("file:", ""));

		logger.error("Jar: " + currentJar);

		/* Build command: java -jar application.jar */
		final ArrayList<String> command = new ArrayList<String>();
		command.add(javaBin);
		command.add("-jar");
		command.add(currentJar.getPath());

		logger.error("Command: " + command);

		final ProcessBuilder builder = new ProcessBuilder(command);

		try {
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public AtomicBoolean getProcessEnded() {
		return processEnded;
	}

	/**
	 * @return the pcaAlgorithmType
	 * @see #pcaAlgorithmType
	 */
	public PcaType getPcaAlgorithmType() {
		return pcaAlgorithmType;
	}

	/**
	 * @param pcaAlgorithmType
	 *          the pcaAlgorithmType to set
	 * @see #pcaAlgorithmType
	 */
	public void setPcaAlgorithmType(PcaType pcaAlgorithmType) {
		this.pcaAlgorithmType = pcaAlgorithmType;
	}
}
