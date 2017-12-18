package org.ace.java.fileTypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DMTDFile implements Runnable {

	String path;
	
	public DMTDFile(String path) {
		this.path = path;
	}
	
	@Override
	public void run() {

		processDMTDFile();
	}
	
	public void processDMTDFile() {
		int[] fileData = new int[4];;
		List<File> list = getAllMTDFiles(this.path);
		for (File f : list) {
			fileData = getMTDFiles(f);
		}
		try {
			createDMTDFiles (this.path, fileData);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	private void createDMTDFiles(String path2, int[] arr) throws FileNotFoundException, UnsupportedEncodingException {
		int index = path2.lastIndexOf("\\");
		String dir = path2.substring(0, index-1);
		PrintWriter writer = new PrintWriter(dir+".dmtd", "UTF-8");
		
		writer.println("Total Words :" +arr[0]);
		writer.println("Total Vowels :" +arr[1]);
		writer.close();
	}

	private int[] getMTDFiles(File f) {
		int[] data = new int[2];
		
		try {
			Scanner scan = new Scanner(f);
			while (scan.hasNext()) {
				String s = scan.nextLine();
				String[] string = s.split(":");
				if ((string[0].substring(0, string[0].length()-1).equalsIgnoreCase("Word Count is"))) {
					data[0] = Integer.parseInt(string[1].substring(1));
				}
				
				if ((string[0].substring(0, string[0].length()-1).equalsIgnoreCase("Vowel Count is"))) {
					data[1] = Integer.parseInt(string[1].substring(1));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return data;
	}

	public static List<File> getAllMTDFiles(String dir) {

		List<File> mtdFile = new ArrayList<File>();

		File directory = new File(dir);

		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isFile()) {
				if (file.getAbsolutePath().endsWith(".mtd")) {
					mtdFile.add(file);
				}
			}
		}

		return mtdFile;

	}
	

}
