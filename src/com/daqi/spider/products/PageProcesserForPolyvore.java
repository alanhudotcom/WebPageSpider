package com.daqi.spider.products;

import java.io.UnsupportedEncodingException;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import com.alibaba.fastjson.JSONObject;

public class PageProcesserForPolyvore implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100).setCycleRetryTimes(3);
//    		.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
//    		.addHeader("Connection", "keep-alive")
//    		.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

    private int mCurPage = 1;
    private String mBaseUrl = "http://www.polyvore.com/cgi/shop?.in=json&.out=jsonx&request=";
    private JSONObject mJsonRequest;

    
    public PageProcesserForPolyvore(JSONObject jsonRequest) {
    	super();
    	mJsonRequest = jsonRequest;
    }
    
	@Override
	public void process(Page page) {
		String pageHtmlString = "";
		if (page.getUrl().toString().startsWith(mBaseUrl)) {
			JSONObject pageJson = JSONObject.parseObject(page.getRawText().toString());
			JSONObject statusJson = pageJson.getJSONObject("status");
			JSONObject resultJson = pageJson.getJSONObject("result");
			int morePage = resultJson.getIntValue("more_pages");
			pageHtmlString = resultJson.getString("html");
			mCurPage = resultJson.getIntValue("page");
			
			if (morePage == 1) {
				page.addTargetRequest(nextUrl());
			} else {
				System.out.println("==last page，to finish==========");
//				page.putField("isLastOne", "Yes");
			}
		} else {
			pageHtmlString = page.getRawText();
			page.addTargetRequest(nextUrl());
		}

		Html pageHtml = new Html(pageHtmlString);
		List<String> productImgList = pageHtml.xpath("//div[@class='main']/a/img//@src").all();
        List<String> productUrlList = pageHtml.xpath("//div[@class='main']/a//@href").all();
//        List<String> productLinks = page.getHtml().xpath("//div[@class='grid_item']/div[@class='main']/a//@href").all();

        System.out.println("==Catching " + (mCurPage - 1) + " page，this page has " + productImgList.size() + " pictures to be handled =================");
        
        page.putField("productUrlList", productUrlList);
        page.putField("productImgList", productImgList);
	}

	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	private String nextUrl() {
		int size = 60 * (mCurPage);
		++mCurPage;
		// 拼接请求地址, 增加翻页相关信息
		mJsonRequest.put("page", mCurPage);
		JSONObject tmpPassback = new JSONObject();
		tmpPassback.put("idx_search", size);
		tmpPassback.put("grid_idx_1x1", size);
		mJsonRequest.put("passback", tmpPassback);
		
		String requestStr = mJsonRequest.toString();
		String requestStrInUrl = "";
		try {
			requestStrInUrl = java.net.URLEncoder.encode(requestStr, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mBaseUrl + requestStrInUrl;
	}
	
}
