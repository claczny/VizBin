package lcsb.vizbin.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class DataSet {
	static Logger logger = Logger.getLogger(DataSet.class);
	private List<Sequence>	sequences	= new ArrayList<Sequence>();
	
	public List<Sequence> getSequences() {
		return sequences;
	}

	public void setSequences(List<Sequence> sequences) {
		this.sequences = sequences;
	}

	public void addSequence(Sequence sequence) {
		sequences.add(sequence);
	}

	public Integer getLabelsCount() {
		Set<Integer> labels = new HashSet<Integer>();
		for (Sequence s : sequences) {
			labels.add(s.getLabelId());
		}
		return labels.size();
	}
	
	public int getSize(){
		return sequences.size();
	}
}
