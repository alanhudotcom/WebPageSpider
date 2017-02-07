package com.daqi.spider.products.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;

public class ProductUtils {
	
	private static Map<String, String> CURRENCY_MAP = new HashMap<String, String>();
	private static final int CONNECT_TIMEOUT = 10 * 1000;
	private static final int READ_TIMEOUT = 15 * 1000;
	static {
		CURRENCY_MAP.put("£", "GBP");		// 英镑 £
		CURRENCY_MAP.put("€", "EUR");		// 欧元
		CURRENCY_MAP.put("$", "USD");		// 美元
		CURRENCY_MAP.put("$", "USD");		// 美元
		CURRENCY_MAP.put("US$", "USD");		// 美元
		CURRENCY_MAP.put("CAN$", "CAD");	// 加拿大元
		CURRENCY_MAP.put("A$", "AUD");		// 澳币
		CURRENCY_MAP.put("HK$", "HKD");		// 港币
		CURRENCY_MAP.put("S$", "SGD");		// 新加坡元
		CURRENCY_MAP.put("Fr", "CHF");		// 瑞士法郎
	}
	
	public static String convertPriceInfo(String price) {
		if (price == null || price.equals("")) return price;
		price = price.replaceAll(" ", "").trim();
//		String currency = Pattern.compile("[1-9],*\\d*[.]?\\d*").matcher(price).replaceAll("").trim();
		String currency = Pattern.compile("[1-9]{1}\\d*,?\\d*.?\\d*").matcher(price).replaceAll("").trim();
		String curPrice = null;
		if (price.indexOf(currency) == 0) {
			curPrice = price.substring(currency.length());
		} else {
			int index = price.indexOf(currency);
			curPrice = price.substring(0, index);
		}
		
		curPrice = curPrice.replaceAll(",", "");
		String fromCurrency = CURRENCY_MAP.get(currency);
		
		String httpUrl = "http://apis.baidu.com/apistore/currencyservice/currency";
		String httpArg = "fromCurrency=" + fromCurrency + "&toCurrency=CNY&amount=" + curPrice;  
	    httpUrl = httpUrl + "?" + httpArg.replaceAll(" ", "").trim();
	    httpUrl = httpUrl.trim();
	    BufferedReader reader = null;
	    String result = null;

	    try {
	        URL url = new URL(httpUrl);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setRequestMethod("GET");
	        connection.setConnectTimeout(CONNECT_TIMEOUT);
	        connection.setReadTimeout(READ_TIMEOUT);
	        // 填入apikey到HTTP header
	        connection.setRequestProperty("apikey",  "9bb37a3edcbf649a31f1b968c0d8c4b8");
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        String strRead = null;
	        StringBuffer sbf = new StringBuffer();
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	            sbf.append("\r\n");
	        }
	        reader.close();
	        result = sbf.toString();
	    } catch (Exception e) {
//	        e.printStackTrace();
	    	System.out.println("========连接百度，进行价格翻译失败======");
	    }
	    
	    String value = price;
	    try {
	    	JSONObject jsonObj = (JSONObject) JSONObject.parse(result);
		    if (jsonObj.getString("errMsg").equals("success")) {
		    	double mount = jsonObj.getJSONObject("retData").getDoubleValue("convertedamount");
		    	double curCurrency = jsonObj.getJSONObject("retData").getDoubleValue("currency");
		    	value = "¥" + mount + "(" + price + ", 当前汇率" + curCurrency + ")";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	    
	    return value;
	}
	
	public static String convertInfoToZH(String query) {
		if (query == null || query.equals("")) return query;
		String queryParams = query;
		try {
			queryParams = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String httpUrl = "http://fanyi.youdao.com/openapi.do?keyfrom=oudalady&key=402022355&type=data&doctype=json&version=1.1&q=" + queryParams; 
	    BufferedReader reader = null;
	    String result = null;

	    try {
	        URL url = new URL(httpUrl);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setRequestMethod("GET");
	        connection.setConnectTimeout(CONNECT_TIMEOUT);
	        connection.setReadTimeout(READ_TIMEOUT);
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        String strRead = null;
	        StringBuffer sbf = new StringBuffer();
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	            sbf.append("\r\n");
	        }
	        reader.close();
	        result = sbf.toString();
	    } catch (Exception e) {
	    	System.out.println("========进行有道翻译失败======");
	    }
	    
	    String value = query;
	    try {
	    	JSONObject jsonObj = (JSONObject) JSONObject.parse(result);
		    if (jsonObj.getIntValue("errorCode") == 0) {
		    	String tranlated = (String)jsonObj.getJSONArray("translation").get(0);
		    	value += " (" + tranlated + ")";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	    
		return value;
	}
	
	public static void main(String[] args) {
//		convertInfoToZH("100% polyester");
//		convertPriceInfo("$12,343.00");
//		convertPriceInfo("$343.00");
//		convertPriceInfo("$343");
//		convertPriceInfo("$12,343");
		convertPriceInfo("£90.00");
//		$ 690
	}

}
