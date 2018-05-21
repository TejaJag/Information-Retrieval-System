package com.sample.SE;

/**
 * @author tejaswinijagarlamudi
 *
 */
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;


@RestController
@RequestMapping("/query")
public class AppController 
{	
	private static final Logger logger = Logger.getLogger(AppController.class);
	public static  int fileCount;
	public static final List<String> stopwords = Arrays.asList("a", "about", "above", "above", "across", "after", 
			"afterwards", "again", "against", "all", "almost", 
			"alone", "along", "already", "also","although","always",
			"am","among", "amongst", "amoungst", "amount",  "an", "and",
			 "another", "any","anyhow","anyone","anything","anyway", "anywhere",
			 "are", "around", "as",  "at", "back","be","became", "because","become",
			"becomes", "becoming", "been", "before", "beforehand",
			 "behind", "being", "below", "beside", "besides", "between", 
			"beyond", "bill", "both", "bottom","but", "by", "call", "can",
			 "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de",
			 "describe", "detail", "do", "done", "down", "due", "during",
			 "each", "eg", "eight", "either", "eleven","else", "elsewhere",
			 "empty", "enough", "etc", "even", "ever", "every", "everyone", 
			"everything", "everywhere", "except", "few", "fifteen", "fify", 
			"fill", "find", "fire", "first", "five", "for", "former", "formerly",
			 "forty", "found", "four", "from", "front", "full", "further", "get",
			 "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here",
			 "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself",
			 "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest",
			 "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least",
			 "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", 
			 "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name",
			 "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none",
			 "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once",
			 "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out",
			 "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem",
			 "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since",
			 "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", 
			 "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them",
			 "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein",
			 "thereupon", "these", "they", "thick", "thin", "third", "this", "those", "though", 
			 "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", 
			 "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", 
			 "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter",
			 "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", 
			 "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would",
			 "yet", "you", "your", "yours", "yourself", "yourselves", "the","i");
	public static final String basedir = "/Users/tejaswinijagarlamudi/Documents/IR3/";
	public static  Map<String, Float> CosSim = new HashMap<String, Float>();
	@RequestMapping("/qterm")
    public ResponseEntity<?> getRelevantDocs( @RequestParam(value = "name") String name, @RequestParam(value = "doclist", required= false) String[] doclist, @RequestParam(value = "prox") boolean prox)
    {
        
        
        final File folder = new File(basedir);
        Set<String> files = listFilesForFolder(folder, basedir); 
        
        CosineSimilarity.setDocumentMagnitudes(files);
        TfIdf.computeIDF();
        TfIdf.computeWeights();
        
       
        String query = name;
        Map<String, Float> qtermFreq = processQuery(query);
        CosineSimilarity cs = new CosineSimilarity();
        CosSim = cs.docSimilarity(qtermFreq);
        //include term proximity here
        CosSim = AppController.sortByValue(CosSim);
        //System.out.println("initial"+AppController.sortByValue(CosSim));
        //System.out.println("modified"+AppController.sortByValueAsc(TermProximity.getTermPair(qtermFreq)));
        //System.out.println(TfIdf.weights);
//        for(Map.Entry<String, Float> entry: CosSim.entrySet()){
//        	System.out.println(entry.getKey()+":"+ entry.getValue());
//        }
        
        List<String> out = new ArrayList<String>();
        if(prox == true){
	        if(qtermFreq.keySet().size()==1){
		        for(Map.Entry<String, Float> entry: AppController.sortByValue(TermProximity.getTermPair(qtermFreq)).entrySet()){
		        	out.add(entry.getKey());
		        }
	        }else if(qtermFreq.keySet().size() > 1){
	        	for(Map.Entry<String, Float> entry: AppController.sortByValueAsc(TermProximity.getTermPair(qtermFreq)).entrySet()){
		        	out.add(entry.getKey());
		        }
	        }
        }else{
        	//CosSim = AppController.sortByValue(CosSim);
        	for(Map.Entry<String, Float> entry: CosSim.entrySet()){
	        	out.add(entry.getKey());
	        }
        	
        }
        //TermProximity.getTermPair(qtermFreq);
        
        
        //---------
//        Set<String> st = new HashSet<String>();
//        st.add("d3.txt");st.add("d1.txt");
//        Map<String, Float> NewCosSim = cs.docSimilarity(RelevanceFeedback.relFeed(new ArrayList<>(st), qtermFreq));
       
        Crawler c = new Crawler("https://en.wikipedia.org/wiki/Tennis", 15);
        c.crawl();
        //Foo[] array = list.toArray(new Foo[list.size()]);
        
        //System.out.println(AppController.sortByValue(NewCosSim));
        
        List<PageInfo> test = new ArrayList<PageInfo>();
        test = setPageInfo(out, qtermFreq.keySet());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Access-Control-Allow-Origin", "*");
        httpHeaders.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        return new ResponseEntity<>(test, httpHeaders, HttpStatus.OK);
    }
    
    // Method to read list files in a folder
    public  Set<String> listFilesForFolder(final File folder, String cd)  {
    	String cdir=cd;
    	Set<String> files = new HashSet<String>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
            	cdir = cdir +fileEntry.getName()+"/";  // going down one folder
            	System.out.println(cdir);
                listFilesForFolder(fileEntry, cdir);
                cdir = cd; // backtracking to go up the folder to read the rest of the files.
            } else if(!fileEntry.isHidden()){
            	//String path = cdir+fileEntry.getName();
            	files.add(fileEntry.getName());
            	fileCount ++;
            	try{
            		processFile(cdir, fileEntry.getName());
            	}catch(FileNotFoundException ex){
            		System.out.println(ex.getMessage());
            	}
            	
                
            }
        }
        return files;
    }
    
    /*
     *  Method to pre-process the file (removes html script tags)
     */
    private void processFile(String di, String fn) throws FileNotFoundException{
    	
    	String path = di+fn; 
    	List<String> terms = new ArrayList<String>();
    	
    	//if(path.equals("/Users/tejaswinijagarlamudi/Downloads/docsnew/Zion_National_Park.htm")){
    	try{
    		StringBuilder sb = new StringBuilder();
    		BufferedReader br = new BufferedReader(new FileReader(path));
	    	String line;
	    	while((line=br.readLine())!=null){
	    		sb.append(line);
	    	}
	    	String clean = sb.toString().replaceAll("\\<script>.*?</script>","").replaceAll("\\<style.*?>.*?</style>","").replaceAll("\\<.*?>"," ").replaceAll("[^a-zA-Z]", " ").toLowerCase();
	    	Stemmer s = new Stemmer();
	    	terms = s.myStem(removeStopWords(clean));
			InvertedIndex.buildInvertedIndex(terms, fn);
			TermProximity.buildTermProximity(terms, fn);
			br.close();
    		
    		
    	}catch(Exception ex){
    		System.out.println(ex.getMessage());
    	}
    	
    }
    	
    
    public String removeStopWords(String str){
    	List<String> terms = Arrays.asList(str.split("\\s"));
    	StringBuffer sb = new StringBuffer();
    	for(String term: terms){
    		if(!stopwords.contains(term))
    			sb.append(term+" ");
    	}
    	return sb.toString();
    }
    
    @RequestMapping("/term")
    public Map<String, Float> processQuery(String query){
    	if(query!=null){
	    	Stemmer s = new Stemmer();
	    	List<String> allQterms = s.myStem(removeStopWords(query));
	    	Set<String> q = new HashSet<String>(allQterms);
	    	Map<String, Float> queryTermfreq = new HashMap<String, Float>();
	    	if(q.size()>0){
		    	for(String term:q){
		    		if(TfIdf.idf.containsKey(term))
		    			queryTermfreq.put(term, Collections.frequency(allQterms, term) * TfIdf.idf.get(term));
		    	}
	    	}
	    	return queryTermfreq;
    	}
    	return null;
    }
    
    @RequestMapping("/termproximity")
    public void termProximity(){
    	//ReadFiles("");
    	
    }
    
  //sort hashmap by value
    
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> unsortMap) {

        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;

    }
    
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueAsc(Map<K, V> unsortMap) {

        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;

    }
    
   public List<PageInfo> setPageInfo(List<String> docs, Set<String> queryTerm){
	   List<String> queryTerms = new ArrayList<String>(queryTerm);
	   //queryTerms.add("jayhawk");
	   List<PageInfo> pi = new ArrayList<PageInfo>();
	   for(String doc: docs){
		   String title = doc, link, description;
		   try {
			  String path = AppController.basedir+doc;
			String content = readFile(path, Charset.forName("UTF-8"));
			Pattern title_pattern = Pattern.compile("<head>.*?<title>(.*?)</title>.*?</head>", Pattern.DOTALL); 
			Matcher m = title_pattern.matcher(content);
			while (m.find()) {
			    title = m.group(1);
			}
			StringBuilder qt = new StringBuilder();
			
			if(queryTerms.size()<=10){
				for(String st: queryTerms){
				qt.append(st+"|");
				}
			}else{
				for(int i=0;i<10;i++){
					qt.append(queryTerms.get(i)+"|");
				}
			}
			
			StringBuilder desc = new StringBuilder();
			Pattern pTag = Pattern.compile("<p>(.*?("+qt+").*?)</p>");
			Matcher m2 = pTag.matcher(content);
			int i=2;
			while(m2.find() && i>0){
				//System.out.println("entered:"+m2.group(1));
				desc.append(m2.group(1));
				i--;
			}
			description = desc.toString();
			link = doc;
			PageInfo pg = new PageInfo(title,link, description);
			
			pi.add(pg);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("File not found");
		}
		   
	   }
	   return pi;
   }
   
   /* reads a file path and returns the content as string*/
   public static String readFile(String path, Charset encoding) 
		   throws IOException 
		 {
		   byte[] encoded = Files.readAllBytes(Paths.get(path));
		   return new String(encoded, encoding);
		 }
    
    
}




/* 
 *        final String dir = System.getProperty("user.dir");
 * 
 */
