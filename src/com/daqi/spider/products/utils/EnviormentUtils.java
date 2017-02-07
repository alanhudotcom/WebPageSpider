package com.daqi.spider.products.utils;

import java.io.File;

public class EnviormentUtils {
	public static final String BASE_PATH = System.getProperty("user.dir");
	public static String LINE = System.getProperty("line.separator");		// 换行 
	public static String SPEAR = "="; 				// 间隔符
	
	public static final String BASE_IMAGE_WANTTOBE_PATH =  EnviormentUtils.BASE_PATH + "/wanttobe/";
	public static final String BASE_THUMB_IMAGE_PATH = EnviormentUtils.BASE_PATH + "/thumbimg/";
	
	static {
		initEnviorment();
	}
	
	private static void initEnviorment() {
		File directory = new File(BASE_THUMB_IMAGE_PATH);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		
		directory = new File(BASE_IMAGE_WANTTOBE_PATH);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}
	
}
