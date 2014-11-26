package lcsb.vizbin.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class DataSet {
	static Logger						logger						= Logger.getLogger(DataSet.class);
	private List<Sequence>	sequences					= new ArrayList<Sequence>();

	private double											minSequenceLength	= 0;

	public List<Sequence> getSequences() {
		return sequences;
	}

	public void setSequences(List<Sequence> sequences) {
		this.sequences = sequences;
	}

	public void addSequence(Sequence sequence) {
		sequences.add(sequence);
		if(sequence.getLength() != null)
		{
			// First sequence that is visited? -> Initialize to a _meaningful_ value
			if((double) 0 == this.minSequenceLength)
			{
				this.minSequenceLength = sequence.getLength();
			}
			else
			{
				this.minSequenceLength = Math.min(this.minSequenceLength, sequence.getLength());				
			}
		}
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
	 * @see #minSequenceLength
	 */
	public double getMinSequenceLength() {
		return minSequenceLength;
	}

	/**
	 * @param minSequenceLength the minSequenceLength to set
	 * @see #minSequenceLength
	 */
	public void setMinSequenceLength(double minSequenceLength) {
		this.minSequenceLength = minSequenceLength;
	}
}
