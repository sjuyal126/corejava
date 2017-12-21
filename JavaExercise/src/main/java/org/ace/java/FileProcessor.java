package org.ace.java;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ace.java.cache.CacheMapImpl;
import org.ace.java.cache.FileStatus;
import org.ace.java.files.CreateMTDFile;

public class FileProcessor {

	private List<File> unprocessedFiles;
	
	public FileProcessor (List<File> unprocessedFiles) {
		this.unprocessedFiles = unprocessedFiles;
	}
	
	public static void process (List<File> unprocessedFiles, CacheMapImpl cache) {
		ExecutorService service = Executors.newFixedThreadPool(unprocessedFiles.size());
		CountDownLatch latch = new CountDownLatch(unprocessedFiles.size());
		
		for (File f : unprocessedFiles) {
			cache.put(f, FileStatus.PROCESSED);
			service.submit(new CreateMTDFile(f, latch));
		}
		try {
			service.shutdown();
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
