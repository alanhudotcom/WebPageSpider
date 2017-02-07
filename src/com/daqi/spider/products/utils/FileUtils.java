package com.daqi.spider.products.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

	public static void compressFileList(File[] fileList, File destFile) {
		try {
			CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(destFile), new CRC32());
			ZipOutputStream zos = new ZipOutputStream(cos);
			for (File file : fileList) {
				compressDir(file, zos, "/");
			}
			zos.flush();
			zos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 
     * 压缩目录 
     *  
     * @param dir 
     * @param zos 
     * @param basePath 
     * @throws Exception 
     */  
    private static void compressDir(File toZipFile, ZipOutputStream zos, String basedir) throws Exception {
    	
    	if (toZipFile.isFile()) {
			compressFile(toZipFile, zos, basedir);
			return;
		}
    	
        File[] files = toZipFile.listFiles();
        // 构建空目录  
        if (files.length < 1) {
            ZipEntry entry = new ZipEntry(basedir + toZipFile.getName() + "/");  
            zos.putNextEntry(entry);  
            zos.closeEntry(); 
            return;
        }  
  
        for (File file : files) {  
            // 递归压缩
        	if (file.isFile()) {
        		compressFile(file, zos, basedir + toZipFile.getName() + "/");
			} else {
				compressDir(file, zos, basedir + toZipFile.getName() + "/");
			}
        }  
    }
    
    /** 
     * 文件压缩 
     *  
     * @param file 
     *            待压缩文件 
     * @param zos 
     *            ZipOutputStream 
     * @param dir 
     *            压缩文件中的当前路径 
     * @throws Exception 
     */  
    private static void compressFile(File file, ZipOutputStream zos, String dir)  
            throws Exception {
    	if (file.getName().startsWith(".")) {
			return;		// 系统文件，不用压缩 
		}
        ZipEntry entry = new ZipEntry(dir + file.getName());  
        zos.putNextEntry(entry);  
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));  
  
        int count;  
        final int BUFFER = 4 * 1024;
        byte data[] = new byte[BUFFER];  
        while ((count = bis.read(data, 0, BUFFER)) != -1) {  
            zos.write(data, 0, count);  
        }  
        bis.close();  
        zos.closeEntry();  
    }
}
