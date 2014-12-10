package lcsb.vizbin.service.utils;

//Structure used in DatasSetUtils class to hold return values of createKmers function
public class kmersResult {
		private Integer [] result;
		private int numKmersRemoved;
		public kmersResult(Integer [] _result, int _numKmersRemoved){
			result = _result;
			numKmersRemoved = _numKmersRemoved;
		}
		public Integer[] getResult(){
			return result;
		}
		public int getNumKmersRemoved(){
			return numKmersRemoved;
		}
}
