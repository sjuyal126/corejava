package org.ace.java.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

public class CleanerThread implements Runnable{

	private CacheMapImpl cache;
	private long timeToLive;
	public CleanerThread(long timeToLive, CacheMapImpl cache) {
		this.timeToLive = timeToLive;
		this.cache = cache;
	}
	
	@Override
	public void run() {
		
        	if (this.timeToLive > 0) {
			List<File> keysToClear = new ArrayList<> ();
            long currentTime = System.currentTimeMillis();
            
        	for (Entry<File, Long> e : this.cache.expirySet()) {
        		if ((currentTime - e.getValue()) > this.timeToLive) {
        			keysToClear.add(e.getKey());
        		}
        	}
        	
        	for (File key : keysToClear) {
        		this.cache.removeTimeKey(key);
        		this.cache.removeValueKey(key);
        	}
            
        	}  
       }

}
