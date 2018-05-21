
package com.sample.SE;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.lang.Math; 

public class TfIdf {
	public static  Map<String, Float> idf = new TreeMap<String, Float>();
	public static final Map<String, Map<String, Float>> weights = new TreeMap<String, Map<String, Float>>();
	public static void computeIDF(){
		for(String term: InvertedIndex.pl.keySet()){
			idf.put(term, (float) Math.log10(((double)AppController.fileCount)/InvertedIndex.pl.get(term).size()));
		}
	}
	
	public static void computeWeights(){
		for(String term: InvertedIndex.pl.keySet()){
			float term_idf = TfIdf.idf.get(term);
			Map<String, Float> wts = new HashMap<String, Float>();
			for(Map.Entry<String, Integer> entry:InvertedIndex.pl.get(term).entrySet()){
				wts.put(entry.getKey(), entry.getValue()*term_idf);
			}
			weights.put(term, wts);
		}
	}
	
}
