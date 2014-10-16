package com.edifecs.epp.security.service.util;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;

public class JsonObjectLoader {
    private static final Logger logger = LoggerFactory.getLogger(JsonObjectLoader.class);

    public static <T> T load(File file, Class<T> type) {
        T returns = null;
        try {
            InputStream stream = new FileInputStream(file);
            getLogger().debug("found json file : {}", file.getName());
            returns = load(stream, type);
        } catch (FileNotFoundException e) {
            getLogger().error("file not found : {}", file.getAbsolutePath());
        }

        return returns;
    }

    public static <T> T load(InputStream stream, Class<T> type) {
        Gson gson = new Gson();
        BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
        T result = gson.fromJson(buff, type);
        getLogger().debug("json file parsed sucessfully.");
        return result;
    }
    
    /**
     * 
     * @param stream
     * @param datasetListType
     * @return
     * @throws UnsupportedEncodingException
     */
    public static <T> T load(InputStream stream, Type datasetListType) {
    	T result = null;
    	try{
    		Reader reader = new InputStreamReader(stream, "UTF-8");
            Gson gson = new Gson();
            result = gson.fromJson(reader, datasetListType);
            getLogger().debug("json file parsed sucessfully.");
            return result;
    	} catch(UnsupportedEncodingException e){
    		getLogger().debug("Unsupported encoding exception: "+e.getMessage());
    	}
    	return result;
    }
    
    
    /**
     * 
     * @param stream
     * @param datasetListType
     * @return
     * @throws UnsupportedEncodingException
     */
    public static <T> T load(String stream, Type datasetListType) {
    	T result = null;
    	Gson gson = new Gson();
		result = gson.fromJson(stream, datasetListType);
		getLogger().debug("json file parsed sucessfully.");
		return result;
    }
    
    public static String StreamToString(InputStream stream) throws IOException {
    	String resp = "";
    	BufferedReader buff = new BufferedReader(new InputStreamReader(stream));
    	String line = "";
    	while ((line = buff.readLine()) != null){
    		resp = resp + line + "\n";
    	}
    	if (buff != null){
    		buff.close();
    	}
    	if (stream != null){
    		stream.close();
    	}
    	return resp.trim();
    }

    private static Logger getLogger() {
        return logger;
    }
}