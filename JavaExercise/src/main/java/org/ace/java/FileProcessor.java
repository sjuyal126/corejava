package org.ace.java;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ace.java.cache.CacheMapImpl;
import org.ace.java.cache.FileStatus;
import org.ace.java.files.CreateDMTDFile;
import org.ace.java.files.CreateMTDFile;

public class FileProcessor {

	private List<File> unprocessedFiles;
	private static List<File> mtdFiles;
	
	public FileProcessor (List<File> unprocessedFiles) {
		this.unprocessedFiles = unprocessedFiles;
	}
	
	public static void process (List<File> unprocessedFiles, CacheMapImpl cache, Path path) {
		ExecutorService service = Executors.newFixedThreadPool(unprocessedFiles.size());
		for (File f : unprocessedFiles) {
			cache.put(f, FileStatus.PROCESSED);
			service.submit(new CreateMTDFile(f));
		}
		service.shutdown();
		//listMTDFiles(path);
		//processMTDFiles(path, mtdFiles);
	}
	
	private static List<File> listMTDFiles(Path path) {
		File folder = path.toFile();
		File[] listOfMtdFiles = folder.listFiles();
		for (File file : listOfMtdFiles) {
			if (file.isFile() &&
					(file.getName().endsWith(".mtd"))) {
				mtdFiles.add(file);
			}
		}
		return mtdFiles;
	}
	
	public static void processMTDFiles(Path path, List<File> mtdFiles) {
		
		new CreateDMTDFile(mtdFiles, path).run();
		
	}
}
