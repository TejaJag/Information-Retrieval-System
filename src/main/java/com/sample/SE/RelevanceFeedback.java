package com.sample.SE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RelevanceFeedback {
	public static Map<String, Float> relFeed(List<String> docs, Map<String, Float> oldqtermFreq){
		Map<String, Map<String, Integer>> pl = InvertedIndex.pl;
		Map<String, Float> modQuery = new HashMap<String, Float>();
		for(String term:pl.keySet()){
			float sum = 0;
			for(String doc: docs){
				if(pl.get(term).containsKey(doc)){
					sum = sum + pl.get(term).get(doc);
				}
			}
			sum = (sum/docs.size()) * 2.75f;
			if(oldqtermFreq.containsKey(term)){
				sum = sum + oldqtermFreq.get(term);
				modQuery.put(term, sum);
			}
		
		}
		//get difference of keysets of old and new
		//add old terms not in new
		Set<String> diff = new HashSet<String>(oldqtermFreq.keySet());
		diff.removeAll(modQuery.keySet());
		for(String term: diff){
			modQuery.put(term, oldqtermFreq.get(term));
		}
		return modQuery;
	}
}
