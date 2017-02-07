package com.daqi.spider.products;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.Spider;

import com.alibaba.fastjson.JSONObject;

public class ProductSpider {
	
    public static void main(String[] args) {
    	if (args.length < 1) {
			printTipsInfo();
			return;
		}
//    	printArgsInfo(args);
    	String command = args[0];
    	if (command.equals("spider")) {
    		String[] spiderParams = new String[args.length - 1];
    		for (int i = 0; i < spiderParams.length; i++) {
				spiderParams[i] = args[i+1];
			}
    		new ProductSpider().start(spiderParams);
		} else if (command.equals("download")) {
			new ProductDownloader().downloadWantToBeProducts();
		} else {
			printTipsInfo();
		}
    }
    
    private static void printTipsInfo() {
    	System.out.println("请输入正确的参数信息: \r\n1.开始爬数据：spider"
    			+ "\r\n2.开始下载选中数据:download"
    			+ "\r\n备注：spider后需要带上待爬取页面链接地址");
    }
    
    public void start(String[] args) {
    	// 默认链接地址
    	String url = "http://www.polyvore.com/cgi/shop?category_id=2&filter_category_id=2&price.from=325&price.from=655&price.from=1635&price.to=655&price.to=1635&price.to=3275&sale=1";
    	JSONObject jsonRequest = new JSONObject();
    	if (args.length > 0) {
    		url = args[0];
    		String[] tmpUrlParams = url.split("\\?");
    		if (tmpUrlParams.length == 2) {
    			String paramsInfo = tmpUrlParams[1];
    			jsonRequest = parseParamsInfo(paramsInfo);
    		}
		}
    	
        Spider.create(new PageProcesserForPolyvore(jsonRequest)).addUrl(url)
        	.setExitWhenComplete(true)
             .addPipeline(new PipelineForPolyvore()).run();
    }
    
    private JSONObject parseParamsInfo(String urlParams) {
    	JSONObject jsonRequest = new JSONObject();
    	String[] paramsList = urlParams.split("&");
		for (String params : paramsList) {
			String[] paramsKeyValue = params.split("=");
			String key = paramsKeyValue[0];
			String value = paramsKeyValue[1];
			try {// 参数信息部分，需要decode一次
				key = URLDecoder.decode(key, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			try {// 参数信息部分，需要decode一次
				value = URLDecoder.decode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (jsonRequest.containsKey(key)) {
				Object object = jsonRequest.get(key);
				if (object instanceof String) {
					List<String> valueList = new ArrayList<String>();
					valueList.add((String)object);
					valueList.add(value);
					jsonRequest.put(key, valueList);
				} else {
					List<String> valueList = (List<String>)object;
					valueList.add(value);
				}
			} else {
				jsonRequest.put(key, value);
			}
		}
		return jsonRequest;
    }
    
    private static void printArgsInfo(String[] args) {
    	System.out.println("args.length=" + args.length);
    	for (String arg : args) {
			System.out.println("====arg=" + arg);
		}
    }
    
}
