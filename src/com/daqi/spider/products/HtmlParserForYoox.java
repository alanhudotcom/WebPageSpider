package com.daqi.spider.products;

import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;

public class HtmlParserForYoox implements HtmlParser {

	@Override
	public void parseHtmlToProductDetail(Html pageHtml, ProductInfo product) {
		try {
			product.brand = pageHtml.xpath("//span[@itemprop='brand']/text()").toString().replaceAll(" ", "");
	    	product.title = pageHtml.xpath("//span[@itemprop='name']/text()").toString().replaceAll(" ", "");
	    	product.price = pageHtml.xpath("//span[@itemprop='price']/text()").toString().replaceAll(" ", "");
	    	List<String> thumbImgList = pageHtml.xpath("//ul[@id='itemThumbs']//img/@src").all();
	    	List<String> imgList = new ArrayList<String>();
	    	for (String thumbImg : thumbImgList) {
	    		int lastSpideIndex = thumbImg.lastIndexOf("/");
	    		String name = thumbImg.substring(lastSpideIndex + 1);
	    		String id = thumbImg.substring(thumbImg.lastIndexOf("/", lastSpideIndex - 3) + 1, lastSpideIndex);
	    		String lastImgName = name.substring(0, name.indexOf("_") + 1) + "14" + name.substring(name.lastIndexOf("_"));
	    		String imgUrl = "http://images.yoox.com/items/" + id + "/" + lastImgName;
	    		imgList.add(imgUrl);
			}
	    	product.imgPathList = imgList;
	    	
	    	// 产品编码
	    	product.code = pageHtml.xpath("//span[@id='itemInfoCod10']/text()").toString();
   	
	    	// 产品材质
	    	String details = pageHtml.xpath("//div[@id='itemInfoTab']//li[@id='Composition']/text()").toString().replaceAll(" ", "");
	    	product.fabrics = details;
		} catch (Exception e) {
			System.out.println("=exception================================");
			product.printJsonString();
			e.printStackTrace();
		}
		
	}
	
//	public static void main(String[] args) {
//		String url = "http://www.yoox.com/GB/34618868/item?tp=59310&utm_source=direct_uk&utm_medium=affiliazione&utm_campaign=polyvore_uk&dept=women&cod10=34618868VX&utm_content=feed#cod10=34618868VX&sizeId=5";
//		Html pageHtml = new HttpClientDownloader().download(url);
//		ProductInfo product = new ProductInfo();
//		new HtmlParserForYoox().parseHtmlToProductDetail(pageHtml, product);
//	}
	
}
