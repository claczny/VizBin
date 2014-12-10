package lcsb.vizbin.data;

//import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class Sequence {

	private Point2D									location;
	private String									id;
	private Integer									labelId		= 0;
	private String									labelName	= "";

	private String									dna				= "";

	private Double									gc;
	private Double									coverage;
	private Boolean									marker;
	private Double									length;

	private double									descVector[];

	private double									clrVector[];
	private double									pcaVector[];

	private Map<Integer, Integer[]>	kmersMap	= new HashMap<Integer, Integer[]>();

	public Point2D getLocation() {
		return location;
	}

	public void setLocation(Point2D location) {
		this.location = location;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDna() {
		return dna;
	}

	public void setDna(String dna) {
		this.dna = dna;
	}

	public Integer[] getKmers(Integer k) {
		return kmersMap.get(k);
	}

	public void setKmers(Integer k, Integer[] kmers) {
		kmersMap.put(k, kmers);

	}

	public double[] getDescVector() {
		return descVector;
	}

	public void setDescVector(double descVector[]) {
		this.descVector = descVector;
	}

	public double[] getClrVector() {
		return clrVector;
	}

	public void setClrVector(double clrVector[]) {
		this.clrVector = clrVector;
	}

	public double[] getPcaVector() {
		return pcaVector;
	}

	public void setPcaVector(double pcaVector[]) {
		this.pcaVector = pcaVector;
	}

	public Integer getLabelId() {
		return labelId;
	}

	public void setLabelId(Integer labelId) {
		this.labelId = labelId;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	/**
	 * @return the gc
	 * @see #gc
	 */
	public Double getGc() {
		return gc;
	}

	/**
	 * @param gc
	 *          the gc to set
	 * @see #gc
	 */
	public void setGc(Double gc) {
		this.gc = gc;
	}

	/**
	 * @return the coverage
	 * @see #coverage
	 */
	public Double getCoverage() {
		return coverage;
	}

	/**
	 * @param coverage
	 *          the coverage to set
	 * @see #coverage
	 */
	public void setCoverage(Double coverage) {
		this.coverage = coverage;
	}

	/**
	 * @return the marker
	 * @see #marker
	 */
	public Boolean getMarker() {
		return marker;
	}

	/**
	 * @param marker
	 *          the marker to set
	 * @see #marker
	 */
	public void setMarker(Boolean marker) {
		this.marker = marker;
	}

	/**
	 * @param length
	 *          the length to set
	 * @see #length
	 */

	public void setLength(Double length) {
		this.length = length;
	}
	
	public Double getLength() {
		return length;
	}

}
