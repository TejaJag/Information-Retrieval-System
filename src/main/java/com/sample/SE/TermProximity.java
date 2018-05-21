package com.sample.SE;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;


public class TermProximity {

	
	public static final Map<String, Map<String, LinkedList<Integer>>> termPos = new TreeMap<String, Map<String, LinkedList<Integer>>>();
	public static final Map<String, Integer> doc_len = new HashMap<String, Integer>();
	public static final Map<String, Float> Mod_CosSim = new HashMap<String, Float>();	
	public static void buildTermProximity(List<String> list, String docId)
	{
		//if (docId == "Zion_National_Park.htm")
		//{
		Map<String,LinkedList<Integer>> mp = new HashMap <String, LinkedList<Integer>>();
		doc_len.put(docId, list.size());
		Set<String> uniqueTerm = new HashSet<String>(list);
		//System.out.println("list " + list);
		
		for (String uTerm:uniqueTerm)
			{
			LinkedList<Integer> pos = new LinkedList<Integer>();
				for(int i=0; i<list.size(); i++)
				{
					
					String term = list.get(i);
					
					if (term.equals(uTerm))
					{
						//add index value to LinkedList --Not appending correctly
						pos.addLast(i);
					}
				}
				mp.put(uTerm, pos);
				termPos.put(docId, mp);
		    }
		//}
		
		
		//System.out.println(termPos);
			
				
	}
	public static Map<String, Float> getTermPair (Map<String, Float> qterms)
	{	
		//System.out.println("termPos: "+termPos);
		List<String> terms = new ArrayList<>(qterms.keySet());
	
		System.out.println("terms: "+ terms);
		Map<String, Float> doc_term_avg =  new HashMap<String, Float>();
		if(terms.size()>1){
			for (int i=0;i<terms.size();i++)
			{
				for (int j=i+1; j<terms.size();j++)
				{
					//System.out.println(terms.get(i));
					//System.out.println(terms.get(j));
					min_dis_terms(terms.get(i), terms.get(j), termPos, AppController.CosSim.keySet());
					//System.out.println(""+AppController.CosSim.keySet());
				}
			}
			
			for (String doc : Mod_CosSim.keySet())
			{
				Mod_CosSim.put(doc, (float) (Mod_CosSim.get(doc)*2)/(qterms.size()*(qterms.size()-1)));
			}
			System.out.println("Initial----"+AppController.CosSim);
			System.out.println("Mod_CosSim: "+Mod_CosSim);
			
			return Mod_CosSim;
		}
		return AppController.CosSim;
		
		
		
		
			
	}
	
	public static void min_dis_terms(String term1, String term2, Map<String, Map<String, LinkedList<Integer>>> docs_pos, Set<String> returned_docs)
	{
		System.out.println("entered"+ returned_docs);
		Float min=Float.MAX_VALUE;
		Float currmin;
		for (String doc: returned_docs)
		{
			Map<String, LinkedList<Integer>> oneDoc = docs_pos.get(doc);
			if (oneDoc.containsKey(term1) && oneDoc.containsKey(term2))
			{
				for (int i=0; i<oneDoc.get(term1).size(); i++)
				{
					for (int j=0; j<oneDoc.get(term2).size(); j++)
					{
						currmin = (float) Math.abs(oneDoc.get(term1).get(i)-oneDoc.get(term2).get(j));
						if (min>currmin)
						{
							min=currmin;
						}
								
					}
				}
			}else if(oneDoc.containsKey(term1) || oneDoc.containsKey(term2)){
				min = (float) doc_len.get(doc);
			}
			if(!Mod_CosSim.containsKey(doc)){
				Mod_CosSim.put(doc, min);
			}else{
				Mod_CosSim.put(doc, Mod_CosSim.get(doc)+ min);
			}
			//System.out.println("min: "+ min);
		}
	}
		
}