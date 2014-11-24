package lcsb.vizbin.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class DataSet {
	static Logger						logger						= Logger.getLogger(DataSet.class);
	private List<Sequence>	sequences					= new ArrayList<Sequence>();

	private int											maxSequenceLength	= 0;

	public List<Sequence> getSequences() {
		return sequences;
	}

	public void setSequences(List<Sequence> sequences) {
		this.sequences = sequences;
	}

	public void addSequence(Sequence sequence) {
		sequences.add(sequence);
		maxSequenceLength = Math.max(maxSequenceLength, sequence.getDna().length());
	}

	public Integer getLabelsCount() {
		Set<Integer> labels = new HashSet<Integer>();
		for (Sequence s : sequences) {
			labels.add(s.getLabelId());
		}
		return labels.size();
	}

	public int getSize() {
		return sequences.size();
	}

	/**
	 * @return the maxSequenceLength
	 * @see #maxSequenceLength
	 */
	public int getMaxSequenceLength() {
		return maxSequenceLength;
	}

	/**
	 * @param maxSequenceLength the maxSequenceLength to set
	 * @see #maxSequenceLength
	 */
	public void setMaxSequenceLength(int maxSequenceLength) {
		this.maxSequenceLength = maxSequenceLength;
	}
}
