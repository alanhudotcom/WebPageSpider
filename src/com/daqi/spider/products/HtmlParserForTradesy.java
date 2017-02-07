package com.daqi.spider.products;

import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;

public class HtmlParserForTradesy implements HtmlParser {

	@Override
	public void parseHtmlToProductDetail(Html pageHtml, ProductInfo product) {
		try {
			product.brand = pageHtml.xpath("//section[@id='idp-overview']//span[@id='idp-brand']/a/text()").toString();
	    	product.title = pageHtml.xpath("//section[@id='idp-overview']//span[@id='idp-title']/text()").toString();
	    	product.price = pageHtml.xpath("//div[@class='item-price ']/text()").toString();
	    	if (product.price == null) {
	    		product.price = pageHtml.xpath("//div[@class='item-price']/text()").toString().replaceAll(" ", "");
			}
//	    	String[] itemDescription = new String[100];
	    	
//	    	List<String> detailTitle = pageHtml.xpath("//div[@class='item-details-content']//p/text()").all();
//	    	List<String> contentTitle = pageHtml.xpath("//div[@class='item-details-content']//a[@class='tags']/text()").all();
//	    	for (String description : itemDescription) {
//				if (description.contains("Color") && product.color == null) {
//					product.color = description.substring(6);
//				} else if (description.contains("Fabric")) {
//					product.fabrics = description.substring(7);
//				}
//			}
	    	product.imgPathList = pageHtml.xpath("//a[@class='fancybox']/@href").all();
	    	
	    	// 产品编码
	    	product.code = pageHtml.xpath("//section[@id='idp-overview']/p[@class='item-id']/span/text()").toString();
		} catch (Exception e) {
			System.out.println("=exception================================");
			product.printJsonString();
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		String url = "https://www.tradesy.com/dresses/roberto-cavalli-dress-orange-and-black-14945719/?tref=sim_items";
		Html pageHtml = new HttpClientDownloader().download(url);
		ProductInfo product = new ProductInfo();
		new HtmlParserForTradesy().parseHtmlToProductDetail(pageHtml, product);
	}
	
}
