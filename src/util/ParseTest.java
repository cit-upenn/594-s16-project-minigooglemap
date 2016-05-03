package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import geography.GeographicPoint;

public class ParseTest {

	public static void main(String[] args) {
			
			int count = 0;
		
			try { 
				FileReader fileReader = new FileReader("data/maps/upenn.map");
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	            // Read the lines out of the file and put them in a HashMap by points
	            String nextLine = bufferedReader.readLine();
	            while (nextLine != null) {
	            	// parse the line 
	            	
	            	// parse the string
	            	String pattern = "([\\S]+)(\\s)([\\S]+)(\\s)([\\S]+)(\\s)([\\S]+)(\\s\")(.*)(\"\\s)(.*)";
	            		
	            	Pattern r = Pattern.compile(pattern);
	            	Matcher _m = r.matcher(nextLine);
	            	
            		double d1 = 0;
            		double d2 = 0;
            		double d3 = 0;
            		double d4 = 0;            		
            		String loc = "";
            		String typ = "";
	            	while (_m.find()){
	            		d1 = Double.parseDouble(_m.group(1));
	            		d2 = Double.parseDouble(_m.group(3));
	            		d3 = Double.parseDouble(_m.group(5));
	            		d4 = Double.parseDouble(_m.group(7));
	            		loc = _m.group(9);
	            		typ = _m.group(11);
	            	}
            		count++;
	            	
	    	        GeographicPoint p1 = new GeographicPoint(d1, d2);
	    	        GeographicPoint p2 = new GeographicPoint(d3, d4);
	            	Road line = new Road(p1, p2, loc, typ);
//	            	String stuff = line.print();
//	            	System.out.println(stuff);
//	            	buildMapHelper(line, map);
	            	nextLine = bufferedReader.readLine();
	            }
	            bufferedReader.close();
	            System.out.println(count);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			
		
		
	}

}
