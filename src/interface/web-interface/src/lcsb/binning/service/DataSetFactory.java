package lcsb.binning.service;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import lcsb.binning.data.DataSet;
import lcsb.binning.data.Sequence;

public class DataSetFactory {
	static Logger logger = Logger.getLogger(DataSetFactory.class);
	public static DataSet createDataSetFromPointFile(String fileName, String labelFileName, double scale) throws IOException {
		InputStream labelIS = null;
		if (labelFileName != null) {
			if (new File(labelFileName).exists()) {
				labelIS = new FileInputStream(new File(labelFileName));
			}
		}
		return createDataSetFromPointFile(new FileInputStream(fileName), labelIS,scale);
	}

	public static DataSet createDataSetFromPointFile(InputStream is,  InputStream labelIS,double scale) throws IOException {
		DataSet result = new DataSet();
		int i = 0;

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line = br.readLine();
			while (line != null) {
				Sequence sequence = new Sequence();
				String coordinates[] = line.split(",");
				if (coordinates.length == 2) {
					double x = Double.parseDouble(coordinates[0]);
					double y = Double.parseDouble(coordinates[1]);
					sequence.setLocation(new Point2D.Double(x * scale, y * scale));
					sequence.setId("Seq" + (i++));
					result.addSequence(sequence);
				}
				line = br.readLine();
			}
		} finally {
			br.close();
		}

		if (labelIS != null) {
			Map<String, Integer> labels = new HashMap<String, Integer>();
			br = new BufferedReader(new InputStreamReader(labelIS));
			try {
				String line = br.readLine();
				int id =0;
				while (line != null) {
					Sequence sequence = result.getSequences().get(id);
					Integer labelId =labels.get(line); 
					if (labelId==null) {
						labelId = labels.size();
						labels.put(line, labelId);
					}
					sequence.setLabelId(labelId);
					sequence.setLabelName(line);
					line = br.readLine();
					id++;
				}
			} finally {
				br.close();
			}
		}

		return result;
	}

	public static DataSet createDataSetFromFastaFile(String fileName) throws IOException {
		return createDataSetFromFastaFile(new FileInputStream(fileName));
	}

	public static DataSet createDataSetFromFastaFile(InputStream is) throws IOException {
		DataSet result = new DataSet();
		int i = 0;

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line = br.readLine();
			Sequence sequence = null;
			while (line != null) {
				if (line.startsWith(">")) {
					if (sequence != null)
						result.addSequence(sequence);
					sequence = new Sequence();
					sequence.setId("Seq" + (i++));
				} else {
					sequence.setDna(sequence.getDna() + line.toUpperCase());
				}
				line = br.readLine();
			}
			if (sequence != null && !sequence.getDna().equals(""))
				result.addSequence(sequence);
		} finally {
			br.close();
		}

		return result;
	}

	public static void saveToPcaFile(DataSet dataSet, String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		saveToPcaFile(dataSet, fileName, 0.5, 30);
	}

	public static void saveToPcaFile(DataSet dataSet, String fileName, double theta, double perplexity) throws FileNotFoundException,
			UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		writer.println(dataSet.getSequences().size());
		writer.println(dataSet.getSequences().get(0).getPcaVector().length);
		writer.println(theta);
		writer.println(perplexity);
		for (Sequence sequence : dataSet.getSequences()) {
			double vector[] = sequence.getPcaVector();
			for (int i = 0; i < vector.length; i++)
				writer.print(vector[i] + " ");
			writer.println();
		}
		writer.close();
	}

}
