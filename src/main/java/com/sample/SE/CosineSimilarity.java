package com.sample.SE;
//import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.lang.Math;
public class CosineSimilarity {
	public static Map<String, Float> docMag = new HashMap<String, Float>();
	public static void setDocumentMagnitudes(Set<String> docs){
		for(String doc:docs){
			float mag = 0;
			Map<String, Map<String, Integer>> invInd = InvertedIndex.pl;
			for(String term:invInd.keySet()){
				if(invInd.get(term).keySet().contains(doc)){
					mag = mag + (float) Math.pow(invInd.get(term).get(doc),2);
				}
			}
			docMag.put(doc, (float) Math.sqrt( mag));
		}
	}
	
	public void docSimilarityTest(Map<String, Integer> queryTermFreq){
		Set<String> docs = queryDocs(queryTermFreq);
		Map<String, Float> docSim = new TreeMap<String, Float>();
		for(String doc: docs){
			float queryMag = 0;
			float numerator = 0;
			for(Map.Entry<String, Integer> entry: queryTermFreq.entrySet()){
				float qTermWt = entry.getValue() * TfIdf.idf.get(entry.getKey());
			
				if(TfIdf.weights.containsKey(entry.getKey()))
					numerator = numerator + TfIdf.weights.get(entry.getKey()).get(doc) * qTermWt;
				queryMag = queryMag + (float) Math.pow(qTermWt, 2);
			}
			if(numerator != 0.0 && queryMag != 0.0 && docMag.get(doc) != 0.0)
				docSim.put(doc, numerator/(queryMag * docMag.get(doc)));
		}
		//System.out.println(docSim);
	}
	
	
	/*plnode :posting list Node*/
	public Map<String, Float> docSimilarity(Map<String, Float> queryTermFreq){
		float queryMag = 0;
		Map<String, Float> docSim = new TreeMap<String, Float>();
		for(Map.Entry<String, Float> entry: queryTermFreq.entrySet()){
			
			if(TfIdf.weights.containsKey(entry.getKey())){
				float qTermWt = entry.getValue(); //* TfIdf.idf.get(entry.getKey()); // just entry.getValue()
				for(Map.Entry<String, Float> plnode:TfIdf.weights.get(entry.getKey()).entrySet()){
					if(!docSim.containsKey(plnode.getKey())){
						docSim.put(plnode.getKey(), plnode.getValue() * entry.getValue() * TfIdf.idf.get(entry.getKey()));
					}else{
						docSim.put(plnode.getKey(),docSim.get(plnode.getKey())+plnode.getValue() * qTermWt);
					}
				}
				queryMag = queryMag + (float) Math.pow(qTermWt, 2);
			}
			
		}
		for(Map.Entry<String, Float> doc: docSim.entrySet()){
			String d = doc.getKey();
			docSim.put(d, doc.getValue()/(queryMag * docMag.get(d)));
		}
		//System.out.println(docSim);
		return docSim;
	}
	
	/* */
	public Set<String> queryDocs(Map<String, Integer> queryTermFreq){
		Set<String> docs = new HashSet<String>();
		for(String term: queryTermFreq.keySet()){
			if(TfIdf.weights.containsKey(term))
				docs.addAll(TfIdf.weights.get(term).keySet());
		}
		return docs;
	}
	
}
