package com.daqi.spider.products.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.daqi.spider.products.ProductInfo;

public class ObjectUtils {
	
	private static ThreadPoolExecutor mStoreInfoExecutor = 
			new ThreadPoolExecutor(1, 1,
            2000, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
	static {
		mStoreInfoExecutor.allowCoreThreadTimeOut(true);
	}
	
	public static final String CACHE_FILE_PATH = EnviormentUtils.BASE_PATH + "/cache.txt";
	
	private static void mapToStoreFile(String filePath, Map<String, Object> map){
		try {
			StringBuffer buffer = new StringBuffer();
		    FileWriter writer = new FileWriter(new File(filePath), false);
		    for(Map.Entry entry : map.entrySet()){
		        String key = (String) entry.getKey();
		        String value = entry.getValue().toString();
		        buffer.append(key + "=" + value).append(EnviormentUtils.LINE);
		    }
		    writer.write(buffer.toString());
		    writer.flush();
		    writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, Object> mapFromGetFile(String filePath) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    File file = new File(filePath);
	    if (!file.exists()) {
			return map;
		}
	    BufferedReader reader = null;
	    try {
//	        System.out.println("以行为单位读取文件内容，一次读一整行：");
	        reader = new BufferedReader(new FileReader(file));
	        String tempString = null;
	        // 一次读入一行，直到读入null为文件结束
	        int line = 0;
	        while ((tempString = reader.readLine()) != null) {
	        	++line;
	            if (!tempString.startsWith("#")) {
	                String[] strArray = tempString.split("=", 2);
	                if (strArray.length < 2) {
						System.out.println("mapFromGetFile error in " + line + ", for " + tempString);
					} else {
						map.put(strArray[0], JSONObject.parseObject(strArray[1], ProductInfo.class));
					}
	            }
	        }
	        reader.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (reader != null) {
	            try {
	                reader.close();
	            } catch (Exception e1) {
	            }
	        }
	    }
	    return map;
	}
	
	/**
	 * 在newMap替换oldMap时，是否覆盖（isOverwrite)如果是，就直接替换，如果否，则将oldMap中的key前加“#”，默认为否
	 */
	public static void mapToAppendStoreFile(String filePath, Map<String, Object> newMap) {
		if (newMap == null || newMap.size() == 0) {
			return;
		}
		Map<String, Object> mergeMap = newMap;
		
	    Map<String, Object> oldMap = mapFromGetFile(filePath);
	    if (oldMap != null && oldMap.size() > 0) {
			 mergeMap = newMapToOldMap(newMap, oldMap);
		}
	    mapToStoreFile(filePath, mergeMap);
	}
	
	private static Map<String, Object> newMapToOldMap(Map<String, Object> newMap, Map<String, Object> oldMap) {
	    // 由于oldMap中包含了file中更多内容，所以newMap中内容在oldMap中调整后，最后返回oldMap修改之后的map.
	    // 如果选择true覆盖相同的key
//	    if (isOverwrite) {
	        // 循环遍历newMap
	        for (Map.Entry<String, Object> entry : newMap.entrySet()) {
	            String newKey = (String) entry.getKey();
	            oldMap.put(newKey, entry.getValue());
	        }
//	    } else {
//	        // 不覆盖oldMap,需要在key相同的oldMap的key前加#；
//	        // 循环遍历newMap
//	        for (Map.Entry entry : newMap.entrySet()) {
//	            String newKey = (String) entry.getKey();
//	            String newValue = (String) entry.getValue();
//	            String oldValue = oldMap.get(newKey);
//	            oldMap.put("#" + newKey, oldValue);
//	            oldMap.put(newKey, newValue);
//	        }
//	    }
	    return oldMap;
	}
	
	public static void storeInfoToCache(final Map<String, Object> resultMap) {
		mStoreInfoExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("here is storeInfoToCache3...");
				int activeCount = DownloadUtils.getExecutorService().getActiveCount();
				while (activeCount > 0) {	
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						// TODO: handle exception
					}
					System.out.println("There is " + activeCount + " task wait to be downloaded...");
					activeCount = DownloadUtils.getExecutorService().getActiveCount();
				}
				if (resultMap != null && resultMap.size() > 0) {
					System.out.println("There is no task and begin to store cache...");
					mapToAppendStoreFile(ObjectUtils.CACHE_FILE_PATH, resultMap);
				}
			}
		});
	}
	
	
}
