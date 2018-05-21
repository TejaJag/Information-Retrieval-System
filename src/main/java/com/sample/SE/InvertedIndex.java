package com.sample.SE;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import org.apache.log4j.Logger;

public class InvertedIndex {
	private static final Logger logger = Logger.getLogger(AppController.class);
	public static final Map<String, Map<String, Integer>> pl = new TreeMap<String, Map<String, Integer>>();
	public static void buildInvertedIndex(List<String> list, String docId){
		//logger.info(list.size());
		for(String term:list){
			if(!pl.containsKey(term)){
				Map<String, Integer> mp = new HashMap<String, Integer>();
				mp.put(docId, 1);
				pl.put(term, mp);
				//logger.info("--------"+pl);
			}else{
				if(pl.get(term).containsKey(docId))
					pl.get(term).put(docId, pl.get(term).get(docId)+1);
				else pl.get(term).put(docId, 1);
			}
				
		} 
		//System.out.println(pl.size()); 14403
	}
	
}
