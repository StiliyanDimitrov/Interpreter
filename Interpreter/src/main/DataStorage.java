package main;

import java.util.HashMap;
import java.util.Map;

public class DataStorage{

    private Map<String, String> map = new HashMap<String, String>();
    
    private Map<String, HashMap<String, String>> functionOuterMap = new HashMap<String, HashMap<String, String>>();
    private Map<String, String> functionInnerMap = new HashMap<String, String>();  
    
    private Map<String, HashMap<String, String>> testOuterMap = new HashMap<String, HashMap<String, String>>();
    private Map<String, String> testInnerMap = new HashMap<String, String>();

    private static DataStorage instance = null;

    private DataStorage() {}

    public static DataStorage getInstance() {
        if (instance == null)
            instance = new DataStorage();
        return instance;
    }

    public void put(String key, String value) {
        map.put(key,value);       
    }
    
    public String get(String key) {
    	return map.get(key);
    }
    
    public void functionPut(String key, String value) {
        functionInnerMap.put(key,value);       
    }
    
    public String functionGet(String key) {
    	return functionInnerMap.get(key);
    }
    
    public void testPut(String key, String value) {
        testInnerMap.put(key,value);       
    }
    
    public String testGet(String key) {
    	return testInnerMap.get(key);
    }
}
