package org.ace.java.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.List;

public class CreateDMTDFile {
	
	private List<File> mtdFiles;
	private Path path;

	public CreateDMTDFile(List<File> mtdFiles, Path path) {
		this.mtdFiles = mtdFiles;
		this.path = path;
	}
	
	public void run() {
		try {
			createDMTDFiles (path.toString());
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void createDMTDFiles(String path) throws FileNotFoundException, UnsupportedEncodingException {
		int index = path.lastIndexOf("\\");
		String dir = path.substring(0, index-1);
		PrintWriter writer = new PrintWriter(dir+".dmtd", "UTF-8");
		
		writer.println("Total Words :" );
		writer.println("Total Vowels :");
		writer.close();
	}

}
