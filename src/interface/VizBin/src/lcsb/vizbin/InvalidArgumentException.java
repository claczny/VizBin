package lcsb.vizbin;

//import no.uib.cipr.matrix.NotConvergedException;

public class InvalidArgumentException extends Exception {

	public InvalidArgumentException(String string) {
		super(string);
	}

	public InvalidArgumentException(Exception e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

}
