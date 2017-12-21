package org.ace.java.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class CreateMTDFile implements FileGenerator, Runnable{
	
	private File file;
	private PrintWriter writer;
	private CountDownLatch latch;
	
	public CreateMTDFile (File file) {
		this.file = file;
	}

	@Override
	public void generateFiles(File file, long wcount) {
		int index = file.getAbsolutePath().lastIndexOf(".");
		String str = file.getAbsolutePath().substring(0, index);
		try {
			writer = new PrintWriter (str+".mtd", "UTF-8");
			writer.println("Word Count is :"+wcount);
			writer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long wcount = 0;
		try {
			wcount = countWords(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.generateFiles(file, wcount);
		latch.countDown();
	}
	
	public static long countWords(File file) throws FileNotFoundException {
		long wordCount = 0;
		Scanner in = null;
		try {
			if (file.getName().endsWith(".txt")) {
				 in = new Scanner (new FileInputStream(file));
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
		finally {
			in.close();
		}
		
		return 0;
	}

}
