package org.ace.java.fileTypes;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Stream;

public class MTDFile  implements Runnable{
	
	private static Path path;
	private static File file;
	private PrintWriter writer;
	// private CyclicBarrier cyclicBarrier;
	
	public MTDFile(File file) {
		this.file = file;
		// this.cyclicBarrier = cyclicBarrier;
	}

	static long wordCount;
	static long vowelCount;
	
	public static long countWords(File file) throws FileNotFoundException {

		try {
			if (file.getName().endsWith(".txt")) {
				Scanner in = new Scanner (new FileInputStream(file));
		     while (in.hasNext()) {
		    	 in.next();
		    	 wordCount++;
		     }
		      return wordCount;
		    }
		}
			catch (IOException ioException) {
		      ioException.printStackTrace();
		    }
		
		return 0;
	}

	@Override
	public void run() {
		while(true) {
		try {
			countWords (file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		countVowels (file);
		try {
			createMTDFile (file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	}
		
	private void countVowels(File files) {
		try {
			Scanner in = new Scanner (files);
			while (in.hasNextLine()) {
			String str = in.nextLine();
			for (int i = 0; i < str.length(); i++) {
				if (isVowel (str.charAt(i))) {
					vowelCount ++;
				}
			}
			}
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private static boolean isVowel(char ch) {
		if ((ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u' || ch == 'A' || ch == 'E' || ch == 'I'
				|| ch == 'O' || ch == 'U')) {
			return true;
		}
		return false;
	}

	private void createMTDFile(File file) throws IOException {
		
		int index = file.getAbsolutePath().lastIndexOf(".");
		String str = file.getAbsolutePath().substring(0, index);
		try {
			writer = new PrintWriter (str+".mtd", "UTF-8");
			writer.println("Word Count is :"+wordCount);
			writer.print("Vowel Count is :"+vowelCount);
			writer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {

			e.printStackTrace();
		}
	}
}
	
