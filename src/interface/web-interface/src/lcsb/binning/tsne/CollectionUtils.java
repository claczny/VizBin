package lcsb.binning.tsne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectionUtils {
	static <T extends Comparable<T>> T getNthElement(int n, List<T> list) {
		List<T> newList = new ArrayList<T>(list);
		Collections.sort(newList);
		return newList.get(n);
	}

	static <T extends Comparable<T>> void nthElement(int start, int n, int end, List<T> list) {
		List<T> newList = new ArrayList<T>();
		for (int i = start; i < end; i++)
			newList.add(list.get(i));
		Collections.sort(newList);
		for (int i = start, j = 0; i < end; i++, j++)
			list.set(i, newList.get(j));
	}
	public static <T> void nthElement(int start, int n, int end, List<T> list, Comparator<T> comparator) {
		List<T> newList = new ArrayList<T>();
		for (int i = start; i < end; i++)
			newList.add(list.get(i));
		Collections.sort(newList,comparator);
		for (int i = start, j = 0; i < end; i++, j++)
			list.set(i, newList.get(j));
	}
}
