package com.sandeep.amazon_scrapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.ls.LSOutput;

/**
 * Hello world!
 *
 */
public class App 
{
	String sortPriceLowtoHigh ;
	String sortPriceHightoLow ; 
	String sortReview;
	String sortNewArrival;
	String baseURL;
	
	String csvFileName;
	BufferedWriter fileWriter;
	
	public App() {
		sortPriceLowtoHigh = "price-asc-rank";
		sortPriceHightoLow = "price-desc-rank"; 
		sortReview =  "review-rank";
		sortNewArrival = "date-desc-rank";
		baseURL = "https://www.amazon.com/s?k=";
		csvFileName = getFileName("File_Export");
		fileWriter = null;
	}
	
	
    public static void main( String[] args )  
    {
    	App obj;
    	Scanner in = new Scanner(System.in);
    	int ch;
    	do {
    	System.out.println("************Menu************\n"
    			+ "1. Crawl a webpage\n"
    			+ "2. Amazon web scrapper\n"
    			+ "3. Exit\n"
    			+ "Make a selection");
    	ch = in.nextInt();
    	in.nextLine();
    	
    	switch(ch) {
    		case 1:
    			System.out.print("\nEnter URL to be crawled- ");
    	    	String url = in.nextLine();
    			crawl(1, url, new ArrayList<String>());
    			break;
    			
    		case 2:
    			System.out.print("\nEnter search term for your product : ");
    	    	String searchKey = in.nextLine();
//    	    	System.out.println(searchKey);
    	    	obj = new App();
    			obj.scrapper(searchKey);
    	    	break;
    		default:
    			System.out.println("\nMake a valid selection");
    				break;
    	}
    	
    	}while(ch!=3);
    }
    
    public String getFileName(String baseName) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateTimeInfo = dateFormat.format(new Date());
        return baseName.concat(String.format("_%s.csv", dateTimeInfo));
    }
    
    private static void crawl(int level, String url, ArrayList<String> visited) {
    	try {
    		if(level<=2) {
    			Document doc = request(level, url, visited);
    			if(doc!= null)
    			for(Element ele: doc.select("a[href]")) {
    				String link = ele.absUrl("href");
    				if(visited.contains(link) != true)
    					crawl(level+1, link, visited);
    			}
    		}
    	}
    	catch(Exception e) {
    		
    	}
    }
    private static Document request(int level, String url, ArrayList<String> visited) {
    	try {
    		Connection con = Jsoup.connect(url);
    		Document doc = con.get();
    		
    		if(con.response().statusCode() == 200) {
    			System.out.println("link :"+url );
    			System.out.println("Title: "+doc.title());    			
    			visited.add(url);
    			return doc;
    		}
    		return null;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public  void scrapper(String searchKey) {
    	try {
        	
        	System.out.println("URL :"+(baseURL+searchKey.replace(' ', '+')+"&s="+sortNewArrival));
        	Connection con = Jsoup.connect(baseURL+searchKey.replace(' ', '+')+"&s="+sortNewArrival);
    		Document doc = con.get();
    		System.out.println(con.response().statusCode());
    		if(con.response().statusCode() == 200) {
    			int  i = 1 ;
    			if(doc!= null) {
    				fileWriter = new BufferedWriter(new FileWriter(csvFileName));
    				
    				String line;
    				
    				for(Element ele: doc.select(".a-section.a-spacing-base")) {
        				
        				String brandName = ele.selectFirst(".a-size-base-plus.a-color-base")!=null ?ele.selectFirst(".a-size-base-plus.a-color-base").text().replace(',', ' ') : "DUMMY";
        				String shoeName = ele.selectFirst(".a-size-base-plus.a-color-base.a-text-normal")!=null ? ele.selectFirst(".a-size-base-plus.a-color-base.a-text-normal").text().replace(',', ' '): "DUMMY";
        				String price = ( ele.selectFirst(".a-price-whole")!= null? ele.selectFirst(".a-price-whole").text(): "00" )
        						+ (ele.selectFirst(".a-price-fraction")!= null? ele.selectFirst(".a-price-fraction").text():"00" );
        				line = brandName +","+shoeName+","+price;
        				System.out.println(line);
        				fileWriter.newLine();
                        fileWriter.write(line);
        			}
    		}
        			
    			System.out.println(" END " );	
    		}
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        	}
        	finally {
        		try {
        			if(fileWriter != null)
        				fileWriter.close();
        		}catch(Exception e) {
        			e.printStackTrace();
        		}
        	}
    }
    
    
}
