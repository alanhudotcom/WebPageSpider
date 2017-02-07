package com.daqi.spider.products;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.daqi.spider.products.utils.DownloadUtils;
import com.daqi.spider.products.utils.EnviormentUtils;
import com.daqi.spider.products.utils.FileUtils;
import com.daqi.spider.products.utils.ObjectUtils;

public class ProductDownloader {
	
	private static final String BASE_IMAGE_WANTTOBE_PATH =  EnviormentUtils.BASE_PATH + "/wanttobe/";
	
	public void downloadWantToBeProducts() {
		//从wanttobe列表中取出
		File wanttobeDir = new File(BASE_IMAGE_WANTTOBE_PATH);
		Map<String, Object> productMap = ObjectUtils.mapFromGetFile(ObjectUtils.CACHE_FILE_PATH);
		
		String[] tmpFileList = wanttobeDir.list();
		ArrayList<String> tmpArrayList = new ArrayList<>(tmpFileList.length);
		for (String string : tmpFileList) {
			if (string.startsWith(".")) {
				continue;
			}
			tmpArrayList.add(string);
		}
		
		String[] wanttobeProductList = new String[tmpArrayList.size()];
		wanttobeProductList = tmpArrayList.toArray(wanttobeProductList);
		System.out.println("=====Downloading productinfos，total = " + wanttobeProductList.length);
		for (String productName : wanttobeProductList) {
			if (productName.contains(".")) {
				String productId = productName.substring(0, productName.indexOf("."));
				Object object = productMap.get(productId);
				if (object instanceof ProductInfo) {
					ProductInfo product = (ProductInfo)object;
					downloadProduct(productId, product);
				}
			}
		}
		
		waitDownloadFinishToCompress();
	}
	
	private void downloadProduct(String productId, final ProductInfo product) {
		if (productId == null || productId.trim().equals("")) {
			return;
		}
		if (product == null || product.imgPathList.size() == 0) {
			return;
		}
		
		// 首先创建目录
		final String productDirPath = BASE_IMAGE_WANTTOBE_PATH + productId;
		File productDirectory = new File(productDirPath);
		if (!productDirectory.exists()) {
			productDirectory.mkdirs();
		}
		
		// 下载图片
		List<String> imgList = product.imgPathList;
		if (imgList.size() > 0) {
			DownloadUtils.downloadImageBatch(imgList, productDirPath + "/");
		}
		
		// 保存信息到info.txt文件中
		DownloadUtils.getExecutorService().execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				storeProductInfo(product, productDirPath);
			}
		});
		
	}
	
	private void waitDownloadFinishToCompress() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
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
				// 下载完成，打包后即将关闭
				compressProductList();
			}
		}, "wait-to-compress").start();
	}
	
	private void storeProductInfo(ProductInfo product, String productDirPath) {
		try {
			StringBuffer buffer = new StringBuffer();
			File infoFile = new File(productDirPath + "/info.txt");
			if (!infoFile.exists()) {
				infoFile.createNewFile();
			}
		    FileWriter writer = new FileWriter(infoFile, false);
		    buffer.append(product.showAllInfoWithZH());
		    writer.write(buffer.toString());
		    writer.flush();
		    writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void compressProductList() {
		File rootDir = new File(BASE_IMAGE_WANTTOBE_PATH);
		File[] listFiles = rootDir.listFiles();
		ArrayList<File> targetFiles = new ArrayList<>();
		for (File file : listFiles) {
			if (file.isDirectory()) {
				targetFiles.add(file);
			}
		}
		if (targetFiles.size() == 0) return;
		File[] toZipFiles = new File[targetFiles.size()]; 
		toZipFiles = targetFiles.toArray(toZipFiles);
		File destFile = new File(BASE_IMAGE_WANTTOBE_PATH + "products.zip");
		FileUtils.compressFileList(toZipFiles, destFile);
	}
	
	public static void main(String[] args) {
		new ProductDownloader().downloadWantToBeProducts();
//	new ProductDownloader().compressProductList();
	}
	
}
