package main;

import java.util.HashMap;
import java.util.Map;

public class DataStorage{

    private Map<String, String> map = new HashMap<String, String>();

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
}
