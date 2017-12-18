package org.ace.java.main;

import java.io.File;

import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.ace.java.cache.CacheMapImpl;
import org.ace.java.cache.CleanerThread;
import org.ace.java.fileTypes.FileStatus;
import org.ace.java.fileTypes.MTDFile;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class MainApplication {
	
	private static List<Path> paths;
	private static CacheMapImpl cache;
	private static BlockingQueue<Path> newFiles;
	
	public MainApplication () {
		this.paths =  new ArrayList<>();
		this.cache = new CacheMapImpl();
		this.newFiles = new LinkedBlockingQueue();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Scanner scan = new Scanner (System.in);
		String s = scan.nextLine();
		Path path = Paths.get(s);
		
		new MainApplication().listFiles(path);
		for (Path p : paths) {
			cache.put(p.toFile(), FileStatus.NEW);
		}
		
		Thread t1 = new Thread(new CleanerThread(10000, cache));
		t1.setDaemon(true);
		t1.start();
		
		countNewFiles (cache);
		processAndCreateMTDFiles (newFiles, path);
		createDMTDFiles (path);
		

		try {
			doWatch(path);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	

	private static void createDMTDFiles(Path path) {
		System.out.println(getAllMTDFiles(path.toString()));
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

	private static void processAndCreateMTDFiles(BlockingQueue<Path> files, Path path) throws InterruptedException {
		
		ExecutorService service = Executors.newFixedThreadPool(files.size());

		for (Path file : files) {
		try {
			while(files.take() != null) {
				cache.put(file.toFile(), FileStatus.PROCESSED);
				service.submit(new MTDFile(file.toFile()));
				new MTDFile(file.toFile()).run();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
		
		service.shutdown();
	}


	private  List<Path>  listFiles(Path path) {
		File folder = path.toFile();
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile() && 
					(file.getName().endsWith(".txt") || file.getName().endsWith(".csv"))) {
				paths.add(file.toPath());
			}
		}
		
		return paths;
		
	}


	public static void doWatch(Path p) throws IOException {

    FileSystem fs = p.getFileSystem();
    WatchService service = fs.newWatchService();

    try {

    	p.register(service, ENTRY_CREATE, ENTRY_MODIFY); 
    	
    	WatchKey key = service.poll(30,TimeUnit.MILLISECONDS);
        while (true) {
            key = service.take();

            Kind<?> kind = null;
            for (WatchEvent<?> watchEvent : key.pollEvents()) {
            	
                kind = watchEvent.kind();
                if (OVERFLOW == kind) {
                    continue; 
                } else if (ENTRY_CREATE == kind) {
                	
                    @SuppressWarnings("unchecked")
					Path newPath = ((WatchEvent<Path>) watchEvent)
                            .context();
                    System.out.println(newPath.toString());
                   
                } else if (ENTRY_MODIFY == kind) {
                	
                    @SuppressWarnings("unchecked")
					Path newPath = ((WatchEvent<Path>) watchEvent)
                            .context();
                }
            }

            if (!key.reset()) {
                break; 
            }
        }

    } catch (IOException ioe) {
        ioe.printStackTrace();
    } catch (InterruptedException ie) {
        ie.printStackTrace();
    }
    
}
	private static void countNewFiles(CacheMapImpl cache2) {
		for(Map.Entry<File, FileStatus> entry : cache.entrySet()) {
			if (entry.getValue() == FileStatus.NEW) {
				newFiles.add(entry.getKey().toPath());
			}
		}
	}

}
