package lu.uni.lcsb.vizbin.data;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private List<Sequence>	elements	= new ArrayList<Sequence>();


	public Cluster(List<Sequence> elements) {
		this.elements = elements;
	}

	public List<Sequence> getElements() {
		return elements;
	}

	public void setElements(ArrayList<Sequence> elements) {
		this.elements = elements;
	}
}
