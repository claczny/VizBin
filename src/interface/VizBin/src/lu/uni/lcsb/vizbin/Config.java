package lu.uni.lcsb.vizbin;

import lu.uni.lcsb.vizbin.pca.PcaType;

/**
 * Class with config parameters.
 * 
 * @author Piotr Gawron
 * 
 */
public class Config {

	/**
	 * Default constructor for utility class. Prevents instatiation.
	 */
	private Config() {

	}

	/**
	 * Default minimum contig length.
	 */
	public static final Integer	DEFAULT_CONTIG_LENGTH	= 1000;
	/**
	 * Default number of threads.
	 */
	public static final Integer	DEFAULT_THREAD_NUM		= 1;
	/**
	 * Default k-mer length.
	 */
	public static final Integer	DEFAULT_KMER_LENGTH		= 5;
	public final static Integer	DEFAULT_PCA_COLUMNS		= 50;
	public final static Double	DEFAULT_THETA					= 0.5;
	public final static Boolean	DEFAULT_MERGE					= true;
	public final static Double	DEFAULT_PERPLEXILITY	= 30.;
	public final static Integer	DEFAULT_SEED					= 0;
	public static final PcaType	DEFAULT_PCA_TYPE			= PcaType.MTJ;

}
