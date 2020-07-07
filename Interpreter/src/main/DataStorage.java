package main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * class to store data
 * @author st
 *
 */

public class DataStorage{

    private Map<String, String> map = new HashMap<String, String>();
    
    private Map<String, LinkedHashMap<String, String>> functionOuterMap = new HashMap<String, LinkedHashMap<String, String>>();
        
    private Map<String, LinkedHashMap<String, String>> testOuterMap = new HashMap<String, LinkedHashMap<String, String>>();
    
    public DataStorage() {}

    public void put(String key, String value) {
        map.put(key,value);       
    }
    
    public String get(String key) {
    	return map.get(key);
    }
    
    public void addFunction(String key, LinkedHashMap<String, String> methodVariables) {
        functionOuterMap.put(key,methodVariables);       
    }
    
    public LinkedHashMap<String,String> getFunction(String key) {
    	return functionOuterMap.get(key);
    }
    
    public void addTest(String key, LinkedHashMap<String,String> value) {
        testOuterMap.put(key,value);       
    }
    
    public LinkedHashMap<String,String> getTest(String key) {
    	return testOuterMap.get(key);
    }
}
