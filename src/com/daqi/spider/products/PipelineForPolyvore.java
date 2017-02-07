package com.daqi.spider.products;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.selector.Html;

import com.daqi.spider.products.utils.DownloadUtils;
import com.daqi.spider.products.utils.EnviormentUtils;
import com.daqi.spider.products.utils.ObjectUtils;

public class PipelineForPolyvore implements Pipeline {
	
	@Override
	public void process(ResultItems resultItems, Task task) {
		// TODO Auto-generated method stub
		List<String> productImgList = resultItems.get("productImgList");
        List<String> productUrlList = resultItems.get("productUrlList");
        if (productImgList == null || productImgList.size() == 0 || productUrlList == null || productUrlList.size() == 0) {
        	System.out.println("no data to handle");
			return;
		}
        handleParsepageContent(productImgList, productUrlList);
	}
	
	private void handleParsepageContent(List<String> productImgList, List<String> pageUrlList) {     
        ArrayList<String> supportImgList = new ArrayList<>(100);
        ArrayList<String> supportPageList = new ArrayList<>(100);
        // 打开页面后，进行页面数据解析
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpClientDownloader pageDownloader = new HttpClientDownloader();
        pageDownloader.setThread(30);
        for (int i = 0; i < productImgList.size(); i++) {
        	String pageUrlString = pageUrlList.get(i);
        	if (!ParseHtmlFactory.isSupportWebPage(pageUrlString)) {
				continue;
			}
        	String url = productImgList.get(i);
        	supportImgList.add(url);
        	supportPageList.add(pageUrlString);
		}

		// 下载支持的图片到本地
        DownloadUtils.downloadImageBatch(supportImgList, EnviormentUtils.BASE_THUMB_IMAGE_PATH);
        // 下载页面信息到本地
        downloadPageBatch(supportImgList, supportPageList, resultMap);

    	ObjectUtils.storeInfoToCache(resultMap);
	}
	
	
	private void downloadPageBatch(List<String> imgList, List<String>pageList, final Map<String, Object> resultMap) {
		final HttpClientDownloader pageDownloader = new HttpClientDownloader();
        pageDownloader.setThread(3);
        final int downCount = imgList.size();
		for (int i = 0; i < imgList.size(); i++) {
			final String imgUrl = imgList.get(i);
			final String pageUrl = pageList.get(i);
			if (imgUrl == null || pageUrl == null) {
				continue;
			}
			final int index = i;
			DownloadUtils.getExecutorService().execute(new Runnable() {
				@Override
				public void run() {
					String productImgCode = imgUrl.substring(imgUrl.lastIndexOf("=") + 1);
					if (productImgCode.contains(".")) {
						productImgCode = productImgCode.substring(0, productImgCode.indexOf("."));
					}
					
			        System.out.println("===============download page " + index + "/" + downCount + "===");
			        ProductInfo product = new ProductInfo();
			        try {
			        	Site pageSite = Site.me().setRetryTimes(3).setTimeOut(60*1000).setSleepTime(1 * 800);
			        	pageSite.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
			    		.addHeader("Connection", "keep-alive")
			    		.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			        	Html pageHtml = pageDownloader.download(new Request(pageUrl), Site.me().setRetryTimes(3).setTimeOut(60 * 1000).setSleepTime(1 * 800).toTask()).getHtml();
			        	product = ParseHtmlFactory.parseHtmlToProductInfo(pageHtml, pageUrl);
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("=======download page error for " + pageUrl);
					}
		        	
		        	if (product.imgPathList == null || product.imgPathList.size() == 0) {
		        		 DownloadUtils.delImageFromDownload(imgUrl, EnviormentUtils.BASE_THUMB_IMAGE_PATH);
						return;
					}
		        	resultMap.put(productImgCode, product);
				}
			});
		}
	}
	
	

}
