package com.daqi.spider.products;

import java.util.List;

import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;

public class HtmlParserForIfchic implements HtmlParser {

	@Override
	public void parseHtmlToProductDetail(Html pageHtml, ProductInfo product) {
		try {
			product.brand = pageHtml.xpath("//div[@class='product-info']//h1[@class='product_brand']/a/text()").toString();
	    	product.title = pageHtml.xpath("//div[@class='product-info']//h2[@class='product_name']/text()").toString();
	    	product.price = pageHtml.xpath("//div[@class='product-info']//span[@class='our_price_display']/text()").toString();
	    	product.color = pageHtml.xpath("//div[@class='infoProduct']//input[@id='currentColor']/@value").toString();
	    	product.imgPathList = pageHtml.xpath("//div[@id='product-cover']/div[@class='owl-theme']//a//@href").all();	
	    	// 产品编码
	    	product.code = pageHtml.xpath("//div[@class='ref']//li/text()").toString();
	    	// 产品材质
	    	List<String> details = pageHtml.xpath("//div[@class='desc']/ul/li/text()").all();
	    	StringBuffer stringBuffer = new StringBuffer();
	    	for (String detailItem : details) {
				if (detailItem.contains("%")) {
					stringBuffer.append(detailItem).append(", ");
				}
			}
	    	product.fabrics = stringBuffer.toString();
	    	
		} catch (Exception e) {
//			System.out.println(pageHtml.toString());
			System.out.println("=exception================================");
			product.printJsonString();
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		String url = "https://www.ifchic.com/etre-cecile/1727-postcard-flock-oversize-tank-5060352753004-grey-marle.html";
		Html pageHtml = new HttpClientDownloader().download(url);
		ProductInfo product = new ProductInfo();
		new HtmlParserForIfchic().parseHtmlToProductDetail(pageHtml, product);
	}
	
}
