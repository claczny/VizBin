package lu.uni.lcsb.vizbin;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu.Separator;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import lcsb.vizbin.service.utils.DataSetUtils;
import lcsb.vizbin.service.utils.PcaType;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

/**
 * 
 * @author <a href="mailto:valentin.plugaru.001@student.uni.lu">Valentin
 *         Plugaru</a>
 */
public class MainFrame extends javax.swing.JFrame {
	private static final FileNameExtensionFilter	ZIP_FILTER							= new FileNameExtensionFilter("zip files", "zip");
	private static final FileNameExtensionFilter	TEXT_FILTER							= new FileNameExtensionFilter("text files", "txt");

	/**
	 * 
	 */
	private static final long											serialVersionUID				= 1L;

	private static final String										SAVEABLE_PROPERTY				= "custom:saveable";
	private static final String										KMER_DATA_FILE_PROPERTY	= "custom:k-merDataFile";
	private static final String										WORKSPACE_NAME_PROPERTY	= "custom:workspaceName";

	/**
	 * Property defining if the project can be saved or not.
	 */
	private Boolean																saveable								= false;

	/**
	 * Property describing where the k-mer frequencies should be saved (for debug
	 * purposes). If null then this data won't be saved.
	 */
	private String																kmerDataFile						= null;

	/**
	 * Property describing the name of workspace.
	 */
	private String																workspaceName						= null;

	private Boolean																def_log									= true;
	private Boolean																moreOpionsVisible				= false;
	private Settings															settings								= null;

	/**
	 * Data file from which results where computed.
	 */
	private String																indatafile							= null;
	/**
	 * Labels file from which results where computed.
	 */
	private String																inlabelsfile						= null;
	/**
	 * Input file from which results where computed.
	 */
	private String																inpointsfile						= null;

	private File																	lastOpenPath						= null;

	private ProcessInput													processor								= null;

	private PcaType																pcaType									= PcaType.EJML;

	private Logger																logger									= Logger.getLogger(MainFrame.class.getName());

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MainFrame() {
		logger.debug("Init of Main application frame");

		PropertyChangeListener propertyChangeLogger = new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent arg0) {
				logger.debug("Property changed: " + arg0.getPropertyName() + ". Old: " + arg0.getOldValue() + " New: " + arg0.getNewValue());
			}

		};
		addPropertyChangeListener(propertyChangeLogger);

		initComponents();
		// set default values to the GUI
		this.formatfield_contigLen.setText(Integer.toString(Config.DEFAULT_CONTIG_LENGTH));
		this.formatfield_numThreads.setText(Integer.toString(Config.DEFAULT_THREAD_NUM));
		this.formatfield_kmer.setText(Integer.toString(Config.DEFAULT_KMER_LENGTH));
		this.formatfield_pca.setText(Integer.toString(Config.DEFAULT_PCA_COLUMNS));
		this.formatfield_theta.setText(Double.toString(Config.DEFAULT_THETA));
		this.formatfield_perplexity.setText(Double.toString(Config.DEFAULT_PERPLEXILITY));
		this.formatfield_seed.setText(Integer.toString(Config.DEFAULT_SEED));
		this.combobox_merge.setModel(new DefaultComboBoxModel(new String[] { "Yes", "No" }));
		this.combobox_log.setModel(new DefaultComboBoxModel(new String[] { "Yes", "No" }));
		this.combobox_pca.setModel(new DefaultComboBoxModel(new String[] { PcaType.MTJ.getName(), PcaType.EJML.getName() }));
		this.combobox_pca.setSelectedIndex(0);
		if (Config.DEFAULT_MERGE) {
			this.combobox_merge.setSelectedIndex(0);
		} else {
			this.combobox_merge.setSelectedIndex(1);
		}

		if (def_log) {
			this.combobox_log.setSelectedIndex(0);
		} else {
			this.combobox_log.setSelectedIndex(1);
		}

		// for easy debugging, pre-set input file selector:
		// this.textfield_file.setText("/Users/cedric.laczny/Documents/phd/projects/BINNING/publication/VizBin_-_Application_Note/data/EssentialGenes.fa");

		// this.textfield_file.setText("/Users/cedric.laczny/Documents/phd/projects/BINNING/publication/VizBin_-_Application_Note/revision_01/data/DaVis_testdat.fa");
		// this.textfield_points_file.setText("/Users/cedric.laczny/Documents/phd/projects/BINNING/publication/VizBin_-_Application_Note/revision_01/data/DaVis_testdat.points.txt");
		// this.textfield_labels.setText("/Users/cedric.laczny/Documents/phd/projects/BINNING/publication/VizBin_-_Application_Note/revision_01/data/DaVis_testdat.loglength.ann");
	}

	void setSettings(Settings _settings) {
		settings = _settings;
	}

	@SuppressWarnings({ "rawtypes" })
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		jMenuItem1 = new javax.swing.JMenuItem();
		tabpanel = new javax.swing.JTabbedPane();
		tab_panel_main = new javax.swing.JPanel();
		label_file = new javax.swing.JLabel();
		label_contigLen = new javax.swing.JLabel();
		label_numThreads = new javax.swing.JLabel();
		label_points_file = new javax.swing.JLabel();
		label_labels = new javax.swing.JLabel();
		label_kmer = new javax.swing.JLabel();
		label_merge = new javax.swing.JLabel();
		label_pca_dimensions = new javax.swing.JLabel();
		label_theta = new javax.swing.JLabel();
		label_perplexity = new javax.swing.JLabel();
		label_seed = new javax.swing.JLabel();
		label_pca_library = new javax.swing.JLabel();
		label_log = new javax.swing.JLabel();
		textfield_file = new javax.swing.JTextField();
		textfield_points_file = new javax.swing.JTextField();
		textfield_labels = new javax.swing.JTextField();
		formatfield_kmer = new javax.swing.JFormattedTextField();
		combobox_merge = new javax.swing.JComboBox();
		combobox_log = new javax.swing.JComboBox();
		combobox_pca = new javax.swing.JComboBox();
		formatfield_contigLen = new javax.swing.JFormattedTextField();
		formatfield_numThreads = new javax.swing.JFormattedTextField();
		formatfield_pca = new javax.swing.JFormattedTextField();
		formatfield_theta = new javax.swing.JFormattedTextField();
		formatfield_perplexity = new javax.swing.JFormattedTextField();
		formatfield_seed = new javax.swing.JFormattedTextField();
		button_file = new javax.swing.JButton();
		button_points_file = new javax.swing.JButton();
		button_labels = new javax.swing.JButton();
		button_more_options = new javax.swing.JButton();
		button_process = new javax.swing.JButton();
		tab_panel_vis = new javax.swing.JPanel();
		tab_panel_plugins = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		plugin_list = new javax.swing.JList();
		button_reload_plugins = new javax.swing.JButton();
		button_load_plugin = new javax.swing.JButton();
		filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
		panel_plugin_options = new javax.swing.JPanel();
		label_plugin_list = new javax.swing.JLabel();
		label_plugin_opts = new javax.swing.JLabel();
		statuspanel = new javax.swing.JPanel();
		label_status = new javax.swing.JLabel();
		progBar = new javax.swing.JProgressBar();

		jMenuItem1.setText("jMenuItem1");

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new java.awt.GridBagLayout());

		tab_panel_main.setName("tab_panel_main"); // NOI18N
		tab_panel_main.setLayout(new java.awt.GridBagLayout());

		label_file.setText("File to visualise:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_file, gridBagConstraints);

		label_contigLen.setText("Minimal contig length:");
		label_contigLen.setVisible(true);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_contigLen, gridBagConstraints);

		label_numThreads.setText("Number of threads:");
		label_numThreads.setVisible(true);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_numThreads, gridBagConstraints);

		label_points_file.setText("Point file (optional):");
		label_points_file.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_points_file, gridBagConstraints);

		label_labels.setText("Annotation file (optional):");
		label_labels.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_labels, gridBagConstraints);

		label_kmer.setText("Kmer length:");
		label_kmer.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_kmer, gridBagConstraints);

		label_merge.setText("Merge rev compl:");
		label_merge.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_merge, gridBagConstraints);

		label_pca_dimensions.setText("Intermediate dimensions:");
		label_pca_dimensions.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_pca_dimensions, gridBagConstraints);

		label_theta.setText("Theta:");
		label_theta.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_theta, gridBagConstraints);

		label_perplexity.setText("Perplexity:");
		label_perplexity.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_perplexity, gridBagConstraints);

		label_seed.setText("Seed:");
		label_seed.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_seed, gridBagConstraints);

		label_pca_library.setText("PCA library:");
		label_pca_library.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 12;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_pca_library, gridBagConstraints);

		textfield_file.setMinimumSize(new java.awt.Dimension(4, 7));
		textfield_file.setPreferredSize(new java.awt.Dimension(140, 15));
		textfield_file.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				textfield_fileMouseClicked(evt);
			}
		});
		label_log.setText("Take logarithm of coverage & length?");
		label_log.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 13;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		tab_panel_main.add(label_log, gridBagConstraints);

		textfield_file.setMinimumSize(new java.awt.Dimension(4, 7));
		textfield_file.setPreferredSize(new java.awt.Dimension(140, 15));
		textfield_file.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				textfield_fileMouseClicked(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 136;
		gridBagConstraints.ipady = 12;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(textfield_file, gridBagConstraints);

		formatfield_contigLen.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
		formatfield_contigLen.setPreferredSize(new java.awt.Dimension(140, 29));
		formatfield_contigLen.setVisible(true);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(formatfield_contigLen, gridBagConstraints);

		formatfield_numThreads.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
		formatfield_numThreads.setPreferredSize(new java.awt.Dimension(140, 29));
		formatfield_numThreads.setVisible(true);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(formatfield_numThreads, gridBagConstraints);

		button_more_options.setText("Show additional options");
		button_more_options.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button_more_optionsActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		tab_panel_main.add(button_more_options, gridBagConstraints);

		textfield_points_file.setVisible(false);
		textfield_points_file.setMinimumSize(new java.awt.Dimension(4, 7));
		textfield_points_file.setPreferredSize(new java.awt.Dimension(140, 15));
		textfield_points_file.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				textfield_points_fileMouseClicked(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 136;
		gridBagConstraints.ipady = 12;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(textfield_points_file, gridBagConstraints);

		textfield_labels.setVisible(false);
		textfield_labels.setMinimumSize(new java.awt.Dimension(4, 7));
		textfield_labels.setPreferredSize(new java.awt.Dimension(140, 15));
		textfield_labels.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				textfield_labelsMouseClicked(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 136;
		gridBagConstraints.ipady = 12;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(textfield_labels, gridBagConstraints);

		formatfield_kmer.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
		formatfield_kmer.setPreferredSize(new java.awt.Dimension(140, 29));
		formatfield_kmer.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(formatfield_kmer, gridBagConstraints);

		combobox_merge.setPreferredSize(new java.awt.Dimension(140, 24));
		combobox_merge.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 69;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(combobox_merge, gridBagConstraints);

		formatfield_pca.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
		formatfield_pca.setPreferredSize(new java.awt.Dimension(140, 29));
		formatfield_pca.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 8;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(formatfield_pca, gridBagConstraints);

		formatfield_theta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
		formatfield_theta.setPreferredSize(new java.awt.Dimension(140, 29));
		formatfield_theta.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(formatfield_theta, gridBagConstraints);

		formatfield_perplexity.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
		formatfield_perplexity.setPreferredSize(new java.awt.Dimension(140, 29));
		formatfield_perplexity.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(formatfield_perplexity, gridBagConstraints);

		formatfield_seed.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
		formatfield_seed.setPreferredSize(new java.awt.Dimension(140, 29));
		formatfield_seed.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(formatfield_seed, gridBagConstraints);

		combobox_pca.setPreferredSize(new java.awt.Dimension(140, 24));
		combobox_pca.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 12;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 69;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(combobox_pca, gridBagConstraints);

		combobox_log.setPreferredSize(new java.awt.Dimension(140, 24));
		combobox_log.setVisible(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 13;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 69;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_main.add(combobox_log, gridBagConstraints);

		button_file.setText("Choose...");
		button_file.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button_fileActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		tab_panel_main.add(button_file, gridBagConstraints);

		button_points_file.setVisible(false);
		button_points_file.setText("Choose...");
		button_points_file.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button_points_fileActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 4;
		tab_panel_main.add(button_points_file, gridBagConstraints);

		button_labels.setVisible(false);
		button_labels.setText("Choose...");
		button_labels.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button_labelsActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 5;
		tab_panel_main.add(button_labels, gridBagConstraints);

		button_process.setText("Start");
		button_process.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button_processActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 7;
		tab_panel_main.add(button_process, gridBagConstraints);

		tabpanel.addTab("Main", tab_panel_main);
		tab_panel_main.getAccessibleContext().setAccessibleName("Main");

		javax.swing.GroupLayout tab_panel_visLayout = new javax.swing.GroupLayout(tab_panel_vis);
		tab_panel_vis.setLayout(tab_panel_visLayout);
		tab_panel_visLayout.setHorizontalGroup(tab_panel_visLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 625, Short.MAX_VALUE));
		tab_panel_visLayout.setVerticalGroup(tab_panel_visLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));

		tabpanel.addTab("Visualisation", tab_panel_vis);
		tab_panel_vis.getAccessibleContext().setAccessibleName("Visualisation");

		tab_panel_plugins.setLayout(new java.awt.GridBagLayout());

		plugin_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		jScrollPane1.setViewportView(plugin_list);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 0.25;
		gridBagConstraints.weighty = 1.0;
		tab_panel_plugins.add(jScrollPane1, gridBagConstraints);

		button_reload_plugins.setText("Reload list");
		button_reload_plugins.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button_reload_pluginsActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		tab_panel_plugins.add(button_reload_plugins, gridBagConstraints);

		button_load_plugin.setText("Load plugin");
		button_load_plugin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button_load_pluginActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		tab_panel_plugins.add(button_load_plugin, gridBagConstraints);
		tab_panel_plugins.add(filler1, new java.awt.GridBagConstraints());

		panel_plugin_options.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

		javax.swing.GroupLayout panel_plugin_optionsLayout = new javax.swing.GroupLayout(panel_plugin_options);
		panel_plugin_options.setLayout(panel_plugin_optionsLayout);
		panel_plugin_optionsLayout.setHorizontalGroup(panel_plugin_optionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
				0, 360, Short.MAX_VALUE));
		panel_plugin_optionsLayout.setVerticalGroup(panel_plugin_optionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
				0, 256, Short.MAX_VALUE));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 0.75;
		gridBagConstraints.weighty = 1.0;
		tab_panel_plugins.add(panel_plugin_options, gridBagConstraints);

		label_plugin_list.setText("Plugin list:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		tab_panel_plugins.add(label_plugin_list, gridBagConstraints);

		label_plugin_opts.setText("Plugin options:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		tab_panel_plugins.add(label_plugin_opts, gridBagConstraints);

		/*
		 * Commented out - plugins are not used for now
		 */
		// tabpanel.addTab("Plugins", tab_panel_plugins);
		// tab_panel_plugins.getAccessibleContext().setAccessibleName("Plugins");

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 11;
		gridBagConstraints.gridheight = 11;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		getContentPane().add(tabpanel, gridBagConstraints);
		tabpanel.getAccessibleContext().setAccessibleName("Main");

		statuspanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		statuspanel.setLayout(new java.awt.GridBagLayout());

		label_status.setToolTipText("Double click this status bar to open the application's log file.");
		label_status.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		label_status.setMinimumSize(new java.awt.Dimension(4, 20));
		label_status.setPreferredSize(new java.awt.Dimension(20, 20));
		label_status.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				label_statusMouseClicked(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
		gridBagConstraints.weightx = 0.5;
		statuspanel.add(label_status, gridBagConstraints);
		statuspanel.add(progBar, new java.awt.GridBagConstraints());

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.gridwidth = 11;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
		getContentPane().add(statuspanel, gridBagConstraints);

		setJMenuBar(createMenu());

		pack();
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * This method creates menu for the main form.
	 * 
	 * @return menu
	 */
	protected JMenuBar createMenu() {

		final JMenuBar menu = new JMenuBar();
		JMenu menu_file = new JMenu();
		JMenuItem menu_file_reinitialize = new JMenuItem();
		Separator menu_file_sep1 = new Separator();
		JMenuItem menu_file_exit = new JMenuItem();
		JMenu menu_options = new JMenu();
		JMenuItem menu_options_extvis = new JMenuItem();
		// menu_options_drawaxes = new javax.swing.JCheckBoxMenuItem();
		JMenuItem menu_options_plottopng = new JMenuItem();
		JMenu menu_about = new JMenu();
		JMenuItem menu_options_showlog = new JMenuItem();

		menu_file.setMnemonic('F');
		menu_file.setText("File");

		menu_file_reinitialize.setMnemonic('r');
		menu_file_reinitialize.setText("Reinitialize settings");
		menu_file_reinitialize.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menu_file_reinitializeActionPerformed(evt);
			}
		});
		menu_file.add(menu_file_reinitialize);

		final JMenuItem saveProject = new JMenuItem();
		saveProject.setText("Save workspace");
		saveProject.setMnemonic('s');
		saveProject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveProject();
			}
		});
		saveProject.setEnabled(saveable);
		saveProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (SAVEABLE_PROPERTY.equals(evt.getPropertyName())) {
					if ((Boolean) evt.getNewValue()) {
						saveProject.setEnabled(true);
					} else {
						saveProject.setEnabled(false);
					}
				}
			}
		});

		menu_file.add(saveProject);

		final JMenuItem openProject = new JMenuItem();
		openProject.setText("Open workspace");
		openProject.setMnemonic('o');
		openProject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openProject();
			}
		});
		openProject.setEnabled(true);
		openProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));

		menu_file.add(openProject);

		menu_file.add(menu_file_sep1);

		menu_file_exit.setMnemonic('x');
		menu_file_exit.setText("Exit");
		menu_file_exit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menu_file_exitActionPerformed(evt);
			}
		});
		menu_file.add(menu_file_exit);

		menu.add(menu_file);

		menu_options.setMnemonic('o');
		menu_options.setText("Options");

		menu_options_extvis.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
		menu_options_extvis.setText("Create external visualisation window");
		menu_options_extvis.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				popOutVisWindow(evt);
			}
		});
		menu_options.add(menu_options_extvis);

		// menu_options_drawaxes.setText("Draw plot axes");
		/*
		 * Commented - not use for now. Axes drawing needs to be fixed.
		 */
		// menu_options.add(menu_options_drawaxes);

		menu_options_plottopng.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
		menu_options_plottopng.setText("Export plot to PNG");
		menu_options_plottopng.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exportVisToFile(evt);
			}
		});
		menu_options.add(menu_options_plottopng);

		JMenu debugMenu = new JMenu();
		debugMenu.setText("Debug");
		debugMenu.setMnemonic('d');
		final JCheckBoxMenuItem kmerDataMenu = new JCheckBoxMenuItem();
		kmerDataMenu.setText("K-mer data");
		kmerDataMenu.setMnemonic('k');
		kmerDataMenu.setSelected(false);
		kmerDataMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (kmerDataMenu.isSelected()) {
					File output = getSelectedOutputFile(TEXT_FILTER);
					if (output == null) {
						kmerDataMenu.setSelected(false);
						setKmerDataFile(null);
					} else {
						try {
							setKmerDataFile(output.getCanonicalPath());
						} catch (IOException e1) {
							logger.error(e1, e1);
							JOptionPane.showMessageDialog(menu.getComponent(), e1.getMessage(), "Problem with file", JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					setKmerDataFile(null);
				}
			}
		});
		debugMenu.add(kmerDataMenu);

		menu_options.add(debugMenu);

		menu.add(menu_options);

		menu_about.setMnemonic('A');
		menu_about.setText("About");

		menu_options_showlog.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
		menu_options_showlog.setText("Show application log");
		menu_options_showlog.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menu_options_showlogActionPerformed(evt);
			}
		});
		menu_about.add(menu_options_showlog);

		menu.add(menu_about);
		return menu;
	}

	protected void saveProject() {
		File output = getSelectedOutputFile(ZIP_FILTER);
		if (output != null && !output.equals("")) {
			Appender app = Logger.getRootLogger().getAppender("R");
			String logFile = null;
			if (app instanceof FileAppender && app != null) {
				logFile = ((FileAppender) app).getFile();
			} else {
				logger.error("Cannot find log appender");
			}
			ZipProject zp = new ZipProject(indatafile, inlabelsfile, inpointsfile, logFile);
			try {
				if (new File(output.getCanonicalPath()).exists()) {
					new File(output.getCanonicalPath()).delete();
				}
				zp.saveTo(output.getCanonicalPath());
				JOptionPane.showMessageDialog(this, "File saved successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				logger.error(e, e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "Problem with file", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	protected void openProject() {
		String input = getSelectedInputFile(ZIP_FILTER);
		if (input != null && !input.equals("")) {
			ZipProject zip;
			try {
				zip = new ZipProject(input);
				this.textfield_file.setText(zip.getDataInputFile());
				if (zip.getLabelInputFile() != null) {
					this.textfield_labels.setText(zip.getLabelInputFile());
				}
				this.textfield_points_file.setText(zip.getPointInputFile());
				File f = new File(input);
				setWorkspaceName(f.getName());
			} catch (IOException e) {
				logger.error(e, e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "Problem with file", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void button_processActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_button_processActionPerformed
		Integer contigLen = Config.DEFAULT_CONTIG_LENGTH;
		Integer numThreads = Config.DEFAULT_THREAD_NUM;
		Integer kmer = Config.DEFAULT_KMER_LENGTH;
		Integer pca = Config.DEFAULT_PCA_COLUMNS;
		Double theta = Config.DEFAULT_THETA;
		Double perplexity = Config.DEFAULT_PERPLEXILITY;
		Integer seed = Config.DEFAULT_SEED;
		Boolean merge = Config.DEFAULT_MERGE;
		Boolean log = def_log;

		indatafile = this.textfield_file.getText();
		if (indatafile.isEmpty()) {
			JOptionPane.showMessageDialog(null, "You must specify an input Fasta file!");
			return;
		}

		try {
			contigLen = Integer.parseInt(this.formatfield_contigLen.getText().replaceAll(",", ""));
			if (contigLen < 0) {
				throw new NumberFormatException(this.formatfield_contigLen.getText());
			}
		} catch (NumberFormatException e) {
			logger.warn("Invalid minimal contig length value: " + this.formatfield_contigLen.getText() + ". Using value: " + contigLen);
		}

		try {
			numThreads = Integer.parseInt(this.formatfield_numThreads.getText());

		} catch (NumberFormatException e) {
			logger.warn("Invalid numThreads value: " + this.formatfield_numThreads.getText().replaceAll(",", "") + ". Using value: " + numThreads);
		}

		inpointsfile = this.textfield_points_file.getText();
		inlabelsfile = this.textfield_labels.getText();
		try {
			kmer = Integer.parseInt(this.formatfield_kmer.getText());
		} catch (NumberFormatException e) {
			logger.warn("Invalid kmer value: " + this.formatfield_kmer.getText() + ". Using value: " + kmer);
		}

		merge = ((String) this.combobox_merge.getSelectedItem()).equals("Yes");

		log = ((String) this.combobox_log.getSelectedItem()).equals("Yes");

		pcaType = null;
		for (PcaType type : PcaType.values()) {
			if (((String) this.combobox_pca.getSelectedItem()).equals(type.getName())) {
				pcaType = type;
			}
		}
		if (pcaType == null) {
			logger.warn("Invalid PCA type: " + this.combobox_pca.getSelectedItem() + ".");
			pcaType = PcaType.EJML;
		}

		try {
			pca = Integer.parseInt(this.formatfield_pca.getText());
		} catch (NumberFormatException e) {
			logger.warn("Invalid PCA value: " + this.formatfield_pca.getText() + ". Using value: " + pca);
		}

		try {
			theta = Double.parseDouble(this.formatfield_theta.getText());
		} catch (NumberFormatException e) {
			logger.warn("Invalid theta value: " + this.formatfield_theta.getText() + ". Using value: " + theta);
		}

		try {
			perplexity = Double.parseDouble(this.formatfield_perplexity.getText());
		} catch (NumberFormatException e) {
			logger.warn("Invalid perplexity value: " + this.formatfield_perplexity.getText() + ". Using value: " + perplexity);
		}

		try {
			seed = Integer.parseInt(this.formatfield_seed.getText());
		} catch (NumberFormatException e) {
			logger.warn("Invalid seed value: " + this.formatfield_seed.getText() + ". Using value: " + seed);
		}

		if (processor == null || processor.getProcessEnded() == true) {
			try {
				ProcessParameters params = new ProcessParameters().inputFastaFile(indatafile).//
				contigLength(contigLen).//
						threads(numThreads).//
						inputPointFile(inpointsfile).//
						inputLabelFile(inlabelsfile).//
						kMerLength(kmer).//
						merge(merge).//
						pcaColumns(pca).//
						theta(theta).//
						perplexity(perplexity).//
						seed(seed).//
						pcaAlgorithmType(pcaType).//
						kmerDebugFile(kmerDataFile).//
						extendedLogs(log)//
				;
				ProcessGuiParameters guiParams = new ProcessGuiParameters(this.label_status, this.progBar, this.tabpanel, this, false);
				processor = new ProcessInput(params, guiParams, settings.binFile);

				processor.setName(getWorkspaceName());

				processor.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (ProcessInput.FINISHED_PROPERTY.equals(evt.getPropertyName())) {
							boolean saveable = false;
							if ((Boolean) evt.getNewValue() && processor.getProgressVal() == 100) {
								saveable = true;
							}
							setSaveable(saveable);
						}
					}
				});
				processor.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (ProcessInput.POINTS_FILE_PROPERTY.equals(evt.getPropertyName())) {
							setInpointsfile((String) evt.getNewValue());
						}
					}
				});

				processor.doProcess();
			} catch (Exception e) {
				logger.error("Problem with initializing compuatations...", e);
			}
		}
	}// GEN-LAST:event_button_processActionPerformed

	private String getSelectedInputFile(FileNameExtensionFilter filter) {
		final JFileChooser fc = new JFileChooser();
		if (filter != null) {
			fc.setFileFilter(filter);
		}
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		if (lastOpenPath != null)
			fc.setCurrentDirectory(lastOpenPath);
		fc.showOpenDialog(this.getParent());
		if (fc.getSelectedFile() == null) {
			return "";
		} else {
			if (!fc.getSelectedFile().exists()) {
				JOptionPane.showMessageDialog(null, "The file you specified doesn't exist!");
				return "";
			} else {
				lastOpenPath = fc.getSelectedFile();
				return fc.getSelectedFile().toString();
			}
		}
	}

	private File getSelectedOutputFile(FileNameExtensionFilter filter) {
		final JFileChooser fc = new JFileChooser();
		if (filter != null) {
			fc.setFileFilter(filter);
		}
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		if (lastOpenPath != null)
			fc.setCurrentDirectory(lastOpenPath);
		int returnVal = fc.showSaveDialog(null);
		if (fc.getSelectedFile() == null)
			return null;
		else {
			lastOpenPath = fc.getSelectedFile();
			if (fc.getSelectedFile().exists()) {
				returnVal = JOptionPane.showConfirmDialog(
						null, "The file you specified already exists, do you wish to overwrite it?", "Confirm overwrite", JOptionPane.YES_NO_OPTION);
				if (returnVal != JOptionPane.YES_OPTION)
					return null;
				else
					return fc.getSelectedFile();
			} else
				return fc.getSelectedFile();
		}
	}

	private void button_fileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_button_fileActionPerformed
		this.textfield_file.setText(getSelectedInputFile(null));
		setSaveable(false);
		File f = new File(this.textfield_file.getText());
		setWorkspaceName(f.getName());

	}// GEN-LAST:event_button_fileActionPerformed

	private void textfield_fileMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_textfield_fileMouseClicked
		this.textfield_file.setText(getSelectedInputFile(null));
		setSaveable(false);
		File f = new File(this.textfield_file.getText());
		setWorkspaceName(f.getName());
	}// GEN-LAST:event_textfield_fileMouseClicked

	private void button_points_fileActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_button_points_fileActionPerformed
		this.textfield_points_file.setText(getSelectedInputFile(null));
		setSaveable(false);
	}// GEN-LAST:event_button_points_fileActionPerformed

	private void textfield_points_fileMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_textfield_points_fileMouseClicked
		this.textfield_points_file.setText(getSelectedInputFile(null));
		setSaveable(false);
	}// GEN-LAST:event_textfield_points_fileMouseClicked

	private void button_labelsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_button_labelsActionPerformed
		this.textfield_labels.setText(getSelectedInputFile(null));
		setSaveable(false);
	}// GEN-LAST:event_button_labelsActionPerformed

	private void button_more_optionsActionPerformed(ActionEvent evt) {
		if (moreOpionsVisible) {
			label_points_file.setVisible(false);
			label_labels.setVisible(false);
			label_kmer.setVisible(false);
			label_merge.setVisible(false);
			label_pca_dimensions.setVisible(false);
			label_theta.setVisible(false);
			label_perplexity.setVisible(false);
			label_seed.setVisible(false);
			label_pca_library.setVisible(false);
			label_log.setVisible(false);
			textfield_labels.setVisible(false);
			textfield_points_file.setVisible(false);
			formatfield_kmer.setVisible(false);
			formatfield_pca.setVisible(false);
			formatfield_perplexity.setVisible(false);
			formatfield_seed.setVisible(false);
			formatfield_theta.setVisible(false);
			combobox_merge.setVisible(false);
			combobox_pca.setVisible(false);
			combobox_log.setVisible(false);
			button_more_options.setText("Show additional options");
			moreOpionsVisible = false;
			button_labels.setVisible(false);
			button_points_file.setVisible(false);
			repaint();
		} else {
			label_points_file.setVisible(true);
			label_labels.setVisible(true);
			label_kmer.setVisible(true);
			label_merge.setVisible(true);
			label_pca_dimensions.setVisible(true);
			label_theta.setVisible(true);
			label_perplexity.setVisible(true);
			label_seed.setVisible(true);
			label_pca_library.setVisible(true);
			label_log.setVisible(true);
			textfield_labels.setVisible(true);
			textfield_points_file.setVisible(true);
			formatfield_kmer.setVisible(true);
			formatfield_pca.setVisible(true);
			formatfield_perplexity.setVisible(true);
			formatfield_seed.setVisible(true);
			formatfield_theta.setVisible(true);
			combobox_merge.setVisible(true);
			combobox_pca.setVisible(true);
			combobox_log.setVisible(true);
			button_more_options.setText("Hide additional options");
			moreOpionsVisible = true;
			button_labels.setVisible(true);
			button_points_file.setVisible(true);
			pack();
			repaint();
		}
	}

	private void textfield_labelsMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_textfield_labelsMouseClicked
		this.textfield_labels.setText(getSelectedInputFile(null));
		setSaveable(false);
	}// GEN-LAST:event_textfield_labelsMouseClicked

	private void menu_file_exitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_file_exitActionPerformed
		System.exit(0);
	}// GEN-LAST:event_menu_file_exitActionPerformed

	private void menu_file_reinitializeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_file_reinitializeActionPerformed
		Integer option = JOptionPane.showConfirmDialog(this, "Are you sure you want to reinitialize the settings?\n"
				+ "This will recreate the config file and redeploy the OS-specific TSNE application.", "Confirm reinitialization", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			settings.createSettings();
			settings.extractTSNEBin();
		}
	}// GEN-LAST:event_menu_file_reinitializeActionPerformed

	private void label_statusMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_label_statusMouseClicked
		if (evt.getClickCount() == 2) {
			try {
				Desktop.getDesktop().open(new File(((FileAppender) Logger.getRootLogger().getAppender("R")).getFile()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}// GEN-LAST:event_label_statusMouseClicked

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void button_reload_pluginsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_button_reload_pluginsActionPerformed
		DefaultListModel pluginListModel = new DefaultListModel();
		ArrayList<String> pluginList = settings.pluginUtils.listPlugins();
		for (int i = 0; i < pluginList.size(); i++) {
			pluginListModel.addElement(pluginList.get(i));
		}
		plugin_list.setModel(pluginListModel);
	}// GEN-LAST:event_button_reload_pluginsActionPerformed

	private void button_load_pluginActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_button_load_pluginActionPerformed
		PluginUtils.PLUGINSTATE pluginState;
		if (plugin_list.getSelectedIndex() != -1) {
			settings.pluginUtils.setParentOptionsPanel(panel_plugin_options);
			settings.pluginUtils.loadPlugin((String) plugin_list.getSelectedValue());
			pluginState = settings.pluginUtils.getPluginState();
			if (pluginState == PluginUtils.PLUGINSTATE.CANNOTLOAD) {
				JOptionPane.showMessageDialog(this, "The plugin could not be loaded.");
			} else if (pluginState == PluginUtils.PLUGINSTATE.NOTVALID) {
				JOptionPane.showMessageDialog(this, "The specified plugin doesn't appear to be a valid plugin.");
			} else if (pluginState == PluginUtils.PLUGINSTATE.NOTSUPPORTED) {
				JOptionPane.showMessageDialog(this, "The plugin's version is not supported!");
			} else {
				// perform action if plugin is valid
			}
		}

	}// GEN-LAST:event_button_load_pluginActionPerformed

	private void popOutVisWindow(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_popOutVisWindow
		if (DataSetUtils.isIsDataSetCreated()) {
			JFrame frame = new JFrame("Visualisation");
			ClusterPanel panel = new ClusterPanel(DataSetUtils.getDataSet(), indatafile, this,
			// this should be refactorized when fixed (access by property not the
			// menu option)
			// menu_options_drawaxes.isSelected(),
					false);
			frame.getContentPane().add(panel.getChartPanel());
			frame.pack();
			frame.setVisible(true);
			frame.setSize(800, 600);
		} else {
			JOptionPane.showMessageDialog(null, "No data set available!\nPlease process a data file first.", "Missing dataset", JOptionPane.WARNING_MESSAGE);
		}
	}// GEN-LAST:event_popOutVisWindow

	private void menu_options_showlogActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_options_showlogActionPerformed
		try {
			Desktop.getDesktop().open(new File(((FileAppender) Logger.getRootLogger().getAppender("R")).getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}// GEN-LAST:event_menu_options_showlogActionPerformed

	private void exportVisToFile(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exportVisToFile
		if (DataSetUtils.isIsDataSetCreated()) {
			Container contentPane = DataSetUtils.getDrawingFrame().getContentPane();
			BufferedImage bufferedImage = new BufferedImage(contentPane.getWidth(), contentPane.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2DContext = bufferedImage.createGraphics();
			contentPane.printAll(graphics2DContext);
			graphics2DContext.dispose();
			try {
				File outFile = getSelectedOutputFile(null);
				if (outFile != null)
					ImageIO.write(bufferedImage, "png", outFile);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "No data set available!\nPlease process a data file first.", "Missing dataset", JOptionPane.WARNING_MESSAGE);
		}
	}// GEN-LAST:event_exportVisToFile

	/**
	 * @param args
	 *          the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed"
		// desc=" Look and feel setting code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
			}
		});

	}

	/**
	 * @return the saveable
	 * @see #saveable
	 */
	public Boolean getSaveable() {
		return saveable;
	}

	/**
	 * @param saveable
	 *          the saveable to set
	 * @see #saveable
	 */
	public void setSaveable(Boolean saveable) {
		Boolean oldValue = this.saveable;
		this.saveable = saveable;
		firePropertyChange(SAVEABLE_PROPERTY, oldValue, saveable);
	}

	/**
	 * @return the inpointsfile
	 * @see #inpointsfile
	 */
	public String getInpointsfile() {
		return inpointsfile;
	}

	/**
	 * @param inpointsfile
	 *          the inpointsfile to set
	 * @see #inpointsfile
	 */
	private void setInpointsfile(String inpointsfile) {
		this.inpointsfile = inpointsfile;
	}

	/**
	 * @return the kmerDataFile
	 * @see #kmerDataFile
	 */
	public String getKmerDataFile() {
		return kmerDataFile;
	}

	/**
	 * @param kmerDataFile
	 *          the kmerDataFile to set
	 * @see #kmerDataFile
	 */
	public void setKmerDataFile(String kmerDataFile) {
		String oldValue = this.kmerDataFile;
		this.kmerDataFile = kmerDataFile;
		firePropertyChange(KMER_DATA_FILE_PROPERTY, oldValue, kmerDataFile);
	}

	/**
	 * @return the workspaceName
	 * @see #workspaceName
	 */
	public String getWorkspaceName() {
		return workspaceName;
	}

	/**
	 * @param workspaceName
	 *          the workspaceName to set
	 * @see #workspaceName
	 */
	public void setWorkspaceName(String workspaceName) {
		String oldValue = this.workspaceName;
		this.workspaceName = workspaceName;
		firePropertyChange(WORKSPACE_NAME_PROPERTY, oldValue, workspaceName);
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton							button_file;
	private javax.swing.JButton							button_points_file;
	private javax.swing.JButton							button_labels;
	private javax.swing.JButton							button_more_options;
	private javax.swing.JButton							button_load_plugin;
	private javax.swing.JButton							button_process;
	private javax.swing.JButton							button_reload_plugins;
	@SuppressWarnings("rawtypes")
	private javax.swing.JComboBox						combobox_merge;
	@SuppressWarnings("rawtypes")
	private javax.swing.JComboBox						combobox_log;
	@SuppressWarnings("rawtypes")
	private javax.swing.JComboBox						combobox_pca;
	private javax.swing.Box.Filler					filler1;
	private javax.swing.JFormattedTextField	formatfield_contigLen;
	private javax.swing.JFormattedTextField	formatfield_numThreads;
	private javax.swing.JFormattedTextField	formatfield_kmer;
	private javax.swing.JFormattedTextField	formatfield_pca;
	private javax.swing.JFormattedTextField	formatfield_perplexity;
	private javax.swing.JFormattedTextField	formatfield_seed;
	private javax.swing.JFormattedTextField	formatfield_theta;
	private javax.swing.JMenuItem						jMenuItem1;
	private javax.swing.JScrollPane					jScrollPane1;
	private javax.swing.JLabel							label_contigLen;
	private javax.swing.JLabel							label_numThreads;
	private javax.swing.JLabel							label_file;
	private javax.swing.JLabel							label_points_file;
	private javax.swing.JLabel							label_kmer;
	private javax.swing.JLabel							label_labels;
	private javax.swing.JLabel							label_merge;
	private javax.swing.JLabel							label_pca_dimensions;
	private javax.swing.JLabel							label_perplexity;
	private javax.swing.JLabel							label_seed;
	private javax.swing.JLabel							label_pca_library;
	private javax.swing.JLabel							label_log;
	private javax.swing.JLabel							label_plugin_list;
	private javax.swing.JLabel							label_plugin_opts;
	private javax.swing.JLabel							label_status;
	private javax.swing.JLabel							label_theta;
	// private javax.swing.JCheckBoxMenuItem menu_options_drawaxes;
	private javax.swing.JPanel							panel_plugin_options;
	@SuppressWarnings("rawtypes")
	private javax.swing.JList								plugin_list;
	private javax.swing.JProgressBar				progBar;
	private javax.swing.JPanel							statuspanel;
	private javax.swing.JPanel							tab_panel_main;
	private javax.swing.JPanel							tab_panel_plugins;
	private javax.swing.JPanel							tab_panel_vis;
	private javax.swing.JTabbedPane					tabpanel;
	private javax.swing.JTextField					textfield_file;
	private javax.swing.JTextField					textfield_points_file;
	private javax.swing.JTextField					textfield_labels;

	// End of variables declaration//GEN-END:variables

}
