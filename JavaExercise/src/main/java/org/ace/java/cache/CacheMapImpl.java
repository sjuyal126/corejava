package org.ace.java.cache;

import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class CacheMapImpl {

    private Map<File, Long> timesCache;
    private Map<File, FileStatus> values;
    
    public CacheMapImpl() {
    	this.timesCache = new HashMap<File, Long>();
    	this.values = new HashMap<File, FileStatus>();
    }
    
    
    public void put (File key, FileStatus value) {
        values.put(key, value);
        timesCache.put(key, System.currentTimeMillis());
    }
    
    public FileStatus get(File key) {

        return this.values.get(key);
    }
    
    public int size() {
    	return this.values.size();
    }
    
    public void clear() {
    	this.timesCache.clear();
    	this.values.clear();
    }
    
    public boolean containsKey(File key) {
    	return this.values.containsKey(key);
    }
    
    public boolean containsValue(String value) {
    	return this.values.containsValue(value);
    }
    
    public boolean isEmpty() {
    	return this.values.isEmpty();
    }

	public Set<Entry<File, FileStatus>> entrySet() {
		return this.values.entrySet();
	}
	
	public Set<Entry<File, Long>> expirySet() {
		return this.timesCache.entrySet();
	}
	
	public void removeTimeKey(File key) {
		this.timesCache.remove(key);
	}
	
	public void removeValueKey(File key) {
		this.values.remove(key);
	}
}
