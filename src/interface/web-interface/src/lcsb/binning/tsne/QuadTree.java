package lcsb.binning.tsne;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 * Copyright (c) 2013, Laurens van der Maaten (Delft University of Technology)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. All advertising materials
 * mentioning features or use of this software must display the following
 * acknowledgement: This product includes software developed by the Delft
 * University of Technology. 4. Neither the name of the Delft University of
 * Technology nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY LAURENS VAN DER MAATEN ''AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL LAURENS VAN DER MAATEN BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

public class QuadTree {

	// Fixed constants
	static int	QT_NO_DIMS				= 2;
	static int	QT_NODE_CAPACITY	= 1;

	// A buffer we use when doing force computations
	double			buff[]						= new double[QT_NO_DIMS];

	// Properties of this node in the tree
	QuadTree		parent;
	boolean			is_leaf;
	int					size;
	int					cum_size;

	// Axis-aligned bounding box stored as a center with half-dimensions to
	// represent the boundaries of this quad tree
	Rectangle2D	boundary;

	// Indices in this quad tree node, corresponding center-of-mass, and list of
	// all children
	double			data[];
	double			center_of_mass[]	= new double[QT_NO_DIMS];
	int					index[]						= new int[QT_NODE_CAPACITY];

	// Children
	QuadTree		northWest;
	QuadTree		northEast;
	QuadTree		southWest;
	QuadTree		southEast;

	/**
	 * Default constructor for quadtree -- build tree, too
	 * 
	 * @param inp_data
	 * @param N
	 */
	public QuadTree(double inp_data[], int N) {
		// Compute mean, width, and height of current map (boundaries of quadtree)
		double mean_Y[] = new double[QT_NO_DIMS];
		for (int d = 0; d < QT_NO_DIMS; d++)
			mean_Y[d] = .0;
		double min_Y[] = new double[QT_NO_DIMS];
		for (int d = 0; d < QT_NO_DIMS; d++)
			min_Y[d] = Double.MAX_VALUE;
		double max_Y[] = new double[QT_NO_DIMS];
		for (int d = 0; d < QT_NO_DIMS; d++)
			max_Y[d] = -Double.MAX_VALUE;
		for (int n = 0; n < N; n++) {
			for (int d = 0; d < QT_NO_DIMS; d++) {
				mean_Y[d] += inp_data[n * QT_NO_DIMS + d];
				if (inp_data[n * QT_NO_DIMS + d] < min_Y[d])
					min_Y[d] = inp_data[n * QT_NO_DIMS + d];
				if (inp_data[n * QT_NO_DIMS + d] > max_Y[d])
					max_Y[d] = inp_data[n * QT_NO_DIMS + d];
			}
		}
		for (int d = 0; d < QT_NO_DIMS; d++)
			mean_Y[d] /= (double) N;

		// Construct quadtree
		init(null, inp_data, mean_Y[0], mean_Y[1], Math.max(max_Y[0] - mean_Y[0], mean_Y[0] - min_Y[0]) + 1e-5,
				Math.max(max_Y[1] - mean_Y[1], mean_Y[1] - min_Y[1]) + 1e-5);
		fill(N);
	}

	public QuadTree(double inp_data[], double inp_x, double inp_y, double inp_hw, double inp_hh) {
		init(null, inp_data, inp_x, inp_y, inp_hw, inp_hh);
	}

	public QuadTree(double inp_data[], int N, double inp_x, double inp_y, double inp_hw, double inp_hh) {
		init(null, inp_data, inp_x, inp_y, inp_hw, inp_hh);
		fill(N);
	}

	public QuadTree(QuadTree inp_parent, double[] inp_data, int N, double inp_x, double inp_y, double inp_hw, double inp_hh) {
		init(inp_parent, inp_data, inp_x, inp_y, inp_hw, inp_hh);
		fill(N);

	}

	public QuadTree(QuadTree inp_parent, double[] inp_data, double inp_x, double inp_y, double inp_hw, double inp_hh) {
		init(inp_parent, inp_data, inp_x, inp_y, inp_hw, inp_hh);
	}

	public void setData(double[] inp_data) {
		data = inp_data;
	}

	public QuadTree getParent() {
		return parent;
	}

	public boolean insert(int new_index) {
		// Ignore objects which do not belong in this quad tree
		int pointIndex = new_index * QT_NO_DIMS;
		if (!boundary.contains(new Point2D.Double(data[pointIndex], data[pointIndex + 1])))
			return false;

		// Online update of cumulative size and center-of-mass
		cum_size++;
		double mult1 = (double) (cum_size - 1) / (double) cum_size;
		double mult2 = 1.0 / (double) cum_size;
		for (int d = 0; d < QT_NO_DIMS; d++)
			center_of_mass[d] *= mult1;
		for (int d = 0; d < QT_NO_DIMS; d++)
			center_of_mass[d] += mult2 * data[pointIndex + d];

		// If there is space in this quad tree and it is a leaf, add the object here
		if (is_leaf && size < QT_NODE_CAPACITY) {
			index[size] = new_index;
			size++;
			return true;
		}

		// Don't add duplicates for now (this is not very nice)
		boolean any_duplicate = false;
		for (int n = 0; n < size; n++) {
			boolean duplicate = true;
			for (int d = 0; d < QT_NO_DIMS; d++) {
				if (data[pointIndex + d] != data[index[n] * QT_NO_DIMS + d]) {
					duplicate = false;
					break;
				}
			}
			any_duplicate = any_duplicate | duplicate;
		}
		if (any_duplicate)
			return true;

		// Otherwise, we need to subdivide the current cell
		if (is_leaf)
			subdivide();

		// Find out where the point can be inserted
		if (northWest.insert(new_index))
			return true;
		if (northEast.insert(new_index))
			return true;
		if (southWest.insert(new_index))
			return true;
		if (southEast.insert(new_index))
			return true;

		// Otherwise, the point cannot be inserted (this should never happen)
		return false;
	}

	public void subdivide() {

		// Create four children
		northWest = new QuadTree(this, data, boundary.getX() - .5 * boundary.getWidth(), boundary.getY() - .5 * boundary.getHeight(), .5 * boundary.getWidth(),
				.5 * boundary.getHeight());
		northEast = new QuadTree(this, data, boundary.getX() + .5 * boundary.getWidth(), boundary.getY() - .5 * boundary.getHeight(), .5 * boundary.getWidth(),
				.5 * boundary.getHeight());
		southWest = new QuadTree(this, data, boundary.getX() - .5 * boundary.getWidth(), boundary.getY() + .5 * boundary.getHeight(), .5 * boundary.getWidth(),
				.5 * boundary.getHeight());
		southEast = new QuadTree(this, data, boundary.getX() + .5 * boundary.getWidth(), boundary.getY() + .5 * boundary.getHeight(), .5 * boundary.getWidth(),
				.5 * boundary.getHeight());

		// Move existing points to correct children
		for (int i = 0; i < size; i++) {
			boolean success = false;
			if (!success)
				success = northWest.insert(index[i]);
			if (!success)
				success = northEast.insert(index[i]);
			if (!success)
				success = southWest.insert(index[i]);
			if (!success)
				success = southEast.insert(index[i]);
			index[i] = -1;
		}

		// Empty parent node
		size = 0;
		is_leaf = false;
	}

	public boolean isCorrect() {
		for (int n = 0; n < size; n++) {
			int pointIndex = index[n] * QT_NO_DIMS;
			if (!boundary.contains(new Point2D.Double(data[pointIndex], data[pointIndex + 1])))
				return false;
		}
		if (!is_leaf)
			return northWest.isCorrect() && northEast.isCorrect() && southWest.isCorrect() && southEast.isCorrect();
		else
			return true;
	}

	public void rebuildTree() {
		for (int n = 0; n < size; n++) {

			// Check whether point is erroneous
			int pointIndex = index[n] * QT_NO_DIMS;
			if (!boundary.contains(new Point2D.Double(data[pointIndex], data[pointIndex + 1]))) {

				// Remove erroneous point
				int rem_index = index[n];
				for (int m = n + 1; m < size; m++)
					index[m - 1] = index[m];
				index[size - 1] = -1;
				size--;

				// Update center-of-mass and counter in all parents
				boolean done = false;
				QuadTree node = this;
				while (!done) {
					for (int d = 0; d < QT_NO_DIMS; d++) {
						node.center_of_mass[d] = ((double) node.cum_size * node.center_of_mass[d] - data[pointIndex + d]) / (double) (node.cum_size - 1);
					}
					node.cum_size--;
					if (node.getParent() == null)
						done = true;
					else
						node = node.getParent();
				}

				// Reinsert point in the root tree
				node.insert(rem_index);
			}
		}

		// Rebuild lower parts of the tree
		northWest.rebuildTree();
		northEast.rebuildTree();
		southWest.rebuildTree();
		southEast.rebuildTree();
	}

	public void getAllIndices(int[] indices) {
		getAllIndices(indices, 0);
	}

	public int getDepth() {
		if (is_leaf)
			return 1;
		return 1 + Math.max(Math.max(northWest.getDepth(), northEast.getDepth()), Math.max(southWest.getDepth(), southEast.getDepth()));
	}

	public double computeNonEdgeForces(int point_index, double theta, double neg_f[]) {
		double result = 0;
		// Make sure that we spend no time on empty nodes or self-interactions
		if (cum_size == 0 || (is_leaf && size == 1 && index[0] == point_index))
			return 0;

		// Compute distance between point and center-of-mass
		double D = .0;
		int ind = point_index * QT_NO_DIMS;
		for (int d = 0; d < QT_NO_DIMS; d++)
			buff[d] = data[ind + d];
		for (int d = 0; d < QT_NO_DIMS; d++)
			buff[d] -= center_of_mass[d];
		for (int d = 0; d < QT_NO_DIMS; d++)
			D += buff[d] * buff[d];

		// Check whether we can use this node as a "summary"
		if (is_leaf || Math.max(boundary.getHeight(), boundary.getWidth()) / Math.sqrt(D) < theta) {

			// Compute and add t-SNE force between point and current node
			double Q = 1.0 / (1.0 + D);
			result += cum_size * Q;
			double mult = cum_size * Q * Q;
			for (int d = 0; d < QT_NO_DIMS; d++)
				neg_f[d] += mult * buff[d];
		} else {

			// Recursively apply Barnes-Hut to children
			result += northWest.computeNonEdgeForces(point_index, theta, neg_f);
			northEast.computeNonEdgeForces(point_index, theta, neg_f);
			southWest.computeNonEdgeForces(point_index, theta, neg_f);
			southEast.computeNonEdgeForces(point_index, theta, neg_f);
		}
		return result;
	}

	private int[] integerListToArray(List<Integer> list) {
		int[] result = new int[list.size()];
		for (int i=0;i<0;i++)
			result[i]=list.get(i);
		return result;
	}
	
	private double[] doubleListToArray(List<Double> list) {
		double[] result = new double[list.size()];
		for (int i=0;i<0;i++)
			result[i]=list.get(i);
		return result;
	}
	
	public void computeEdgeForces(List<Integer>row_P, List<Integer>col_P, List<Double>val_P, int N, double[] pos_f) {
		computeEdgeForces(integerListToArray(row_P), integerListToArray(col_P), doubleListToArray(val_P), N, pos_f);
	}
		public void computeEdgeForces(int[] row_P, int[] col_P, double[] val_P, int N, double[] pos_f) {

		// Loop over all edges in the graph
		int ind1, ind2;
		double D;
		for (int n = 0; n < N; n++) {
			ind1 = n * QT_NO_DIMS;
			for (int i = row_P[n]; i < row_P[n + 1]; i++) {

				// Compute pairwise distance and Q-value
				D = .0;
				ind2 = col_P[i] * QT_NO_DIMS;
				for (int d = 0; d < QT_NO_DIMS; d++)
					buff[d] = data[ind1 + d];
				for (int d = 0; d < QT_NO_DIMS; d++)
					buff[d] -= data[ind2 + d];
				for (int d = 0; d < QT_NO_DIMS; d++)
					D += buff[d] * buff[d];
				D = val_P[i] / (1.0 + D);

				// Sum positive force
				for (int d = 0; d < QT_NO_DIMS; d++)
					pos_f[ind1 + d] += D * buff[d];
			}
		}
	}

	public void print() {
		if (cum_size == 0) {
			System.out.print("Empty node\n");
			return;
		}

		if (is_leaf) {
			System.out.print("Leaf node; data = [");
			for (int i = 0; i < size; i++) {
				int pointIndex = index[i] * QT_NO_DIMS;
				for (int d = 0; d < QT_NO_DIMS; d++)
					System.out.print(data[pointIndex + d] + ", ");
				System.out.print(" (index = " + index[i] + ")");
				if (i < size - 1)
					System.out.print("\n");
				else
					System.out.print("]\n");
			}
		} else {
			System.out.print("Intersection node with center-of-mass = [");
			for (int d = 0; d < QT_NO_DIMS; d++)
				System.out.print(center_of_mass[d] + ", ");
			System.out.print("]; children are:\n");
			northEast.print();
			northWest.print();
			southEast.print();
			southWest.print();
		}
	}

	private void init(QuadTree inp_parent, double[] inp_data, double inp_x, double inp_y, double inp_hw, double inp_hh) {
		parent = inp_parent;
		data = inp_data;
		is_leaf = true;
		size = 0;
		cum_size = 0;
		boundary = new Rectangle2D.Double(inp_x, inp_y, inp_hw, inp_hh);
		northWest = null;
		northEast = null;
		southWest = null;
		southEast = null;
		for (int i = 0; i < QT_NO_DIMS; i++)
			center_of_mass[i] = .0;
	}

	private void fill(int N) {
		for (int i = 0; i < N; i++)
			insert(i);
	}

	private int getAllIndices(int[] indices, int loc) {
		// Gather indices in current quadrant
		for (int i = 0; i < size; i++)
			indices[loc + i] = index[i];
		loc += size;

		// Gather indices in children
		if (!is_leaf) {
			loc = northWest.getAllIndices(indices, loc);
			loc = northEast.getAllIndices(indices, loc);
			loc = southWest.getAllIndices(indices, loc);
			loc = southEast.getAllIndices(indices, loc);
		}
		return loc;
	}
}