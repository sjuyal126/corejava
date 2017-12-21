package org.ace.java;

import java.io.File;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.ace.java.cache.CacheMapImpl;
import org.ace.java.cache.CleanerThread;
import org.ace.java.cache.FileStatus;
import org.ace.java.files.CreateDMTDFile;

public class TestApplication {
	
private static CacheMapImpl cache;
private static List<File> newFiles;

public TestApplication () {
	cache = new CacheMapImpl();
	newFiles = new ArrayList<>();
}

public static void main(String[] args) throws IOException, InterruptedException {
	Scanner scan = new Scanner (System.in);
	String s = scan.nextLine();
	Path path = Paths.get(s);
	
	new TestApplication().poll(path);
	
}

public void poll(Path path) {
    ScheduledExecutorService executorService= Executors.newScheduledThreadPool(5);
    ScheduledFuture files = executorService.scheduleAtFixedRate(() -> {
    	
    	Thread t1 = new Thread(new CleanerThread(60000, cache));
    	t1.setDaemon(true);
    	t1.start();
    	
    	
    	this.listFiles(path);
    	if (!newFiles.isEmpty()) {
    		FileProcessor.process(newFiles, cache, path);
    	}
    	
    }, 0, 10, TimeUnit.SECONDS);
	
}


private List<File> listFiles(Path path) {
	File folder = path.toFile();
	File[] listOfFiles = folder.listFiles();
	for (File file : listOfFiles) {
		if (file.isFile() && 
				(file.getName().endsWith(".txt") || file.getName().endsWith(".csv"))) {
				if (cache.size() > 0) {
					if (cache.containsKey(file) && cache.get(file) != FileStatus.PROCESSED) {
						newFiles.remove(file);
					}
					else {
						cache.put(file, FileStatus.NEW);
						newFiles.add(file);
					}
				}
				else {
					cache.put(file, FileStatus.NEW);
					newFiles.add(file);
				}
		}
	}
	return newFiles;
}
}
