package com.sample.SE;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
//import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

public class Crawler {
	//public static final String Base_URL = "https://www.ku.edu";
	public static final Pattern TAG_PATTERN = Pattern.compile("(<(?!a|\\/a>)(?=[a-zA-Z\\/]+).*?>)");
	public static final Pattern A_TAG_PATTERN = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");
	public static final Pattern HREF_TAG_PATTERN = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
	public static int count = 0;
	
	public static final Set<String> crawled_links = new HashSet<String>();
	public static Set<String> disallowed_sites = new HashSet<String>();
	public static Queue<String> linksTo_crawl = new LinkedList<String>();
	public String url;
	public int max_sites;
	public Crawler(String url, int max_sites){
		this.url = url;
		this.max_sites = max_sites;
		disallowed_sites = getDisallowedSites(url);
	}
	
	public void crawl(){
		//Checks if the seedURL is allowed to be crawled or not
		int flag = 0;
		if (disallowed_sites.contains(this.url)) {
			System.out.println("Seed URL is not allowed by Robots.txt");
			System.exit(0);
		}
		do{
			Crawler.crawled_links.add(url);
			try {
				getWordsAndLinks(url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String lin;
			
			if((lin = Crawler.linksTo_crawl.poll()) != null){
				this.url =  lin ;
				
			}
			else flag =1;
			
		}while(Crawler.crawled_links.size() <= max_sites && flag == 0); // while(Crawler.crawled_links.size() <= max_sites && flag == 0);
		
		if(flag == 1){
			System.out.println("Unable to retrive enough no of sites");
		}else{
			System.out.println(Crawler.crawled_links.size());
		}
		
	}
	
	public static BufferedReader readURL(String httpUrl) throws IOException {
		URL url = new URL(httpUrl);
		InputStream is = url.openStream();
		return new BufferedReader(new InputStreamReader(is));
	}
	
	
	public Set<String> getDisallowedSites(String url){
		Set<String> disallowedSites = new HashSet<>();
		//System.out.println("uuuurl:  "+url);
		try (BufferedReader br = readURL("https://en.wikipedia.org"+ "/robots.txt")) {
			String line;
			
			// Ignore till user-agent : *
			while ((line = br.readLine()) != null) {
				if (line.equalsIgnoreCase("User-agent: *"))
					break;
			}
			while ((line = br.readLine()) != null) {
				//System.out.println("line content:  "+ line);
				if (line.startsWith("Disallow:")) {
					//System.out.println(line.split("\\s+")[1].trim());
					disallowedSites.add(line.split("\\s+")[1].trim());
				}
			}

		} catch (IOException e) {
			System.out.println("Unable to capture the robots.txt fie.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return disallowedSites;
	}
	
	
	public void getWordsAndLinks(String httpUrl) throws IOException{
		URL url = new URL(httpUrl);
		File file = null;
		String path = httpUrl.replace("https://en.wikipedia.org/wiki/","");
			file = new File(File.separator+"Users"+File.separator+"tejaswinijagarlamudi"+File.separator+"Documents"+File.separator+"IR3"+File.separator+path+".html");
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		try (BufferedReader br = readURL(httpUrl)) {
			String line = "";
			
			// Don't process till the first p tag.
			while ((line = br.readLine()) != null) {
				writer.write(line);
				if (line.contains("<p>"))
					break;
			}
			
			do {
				line = line.replaceAll(TAG_PATTERN.pattern(), "");
				Matcher linkMatcher = A_TAG_PATTERN.matcher(line);
				
				while (linkMatcher.find()) {
					
					String href = linkMatcher.group(1);
					//String linkText = linkMatcher.group(2);
					Matcher matcher = HREF_TAG_PATTERN.matcher(href);
					while (matcher.find()) {
						String link = matcher.group(1).replaceAll("\"", "");
						//System.out.println("link "+ link);
						if (allowLink(link) && !Crawler.crawled_links.contains(link)) {
							//if(count <= max_sites){
								Crawler.linksTo_crawl.add("https://en.wikipedia.org"+link);
								
								//System.out.println(link);
							//}else break;
							
							// links.add(new HTMLLink(link, linkText, pos));
						}
					}
					

				}
				
				

			} while ((line = br.readLine()) != null);
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//Checks if a specific link should be crawled or not.
		public static boolean allowLink(String link) {
			if (!link.contains("#") && !link.contains(":") && link.startsWith("/wiki/")) {

				return !Crawler.disallowed_sites.contains(link);
						//WikiCrawler.disAllowedLinks.stream().noneMatch(s -> s.equals(link));

			}

			return false;

		}
	
	
	
	
}
