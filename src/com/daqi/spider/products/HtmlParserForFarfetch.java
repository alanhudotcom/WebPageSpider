package com.daqi.spider.products;

import java.util.List;

import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;

public class HtmlParserForFarfetch implements HtmlParser {

	@Override
	public void parseHtmlToProductDetail(Html pageHtml, ProductInfo product) {
		try {
			product.brand = pageHtml.xpath("//div[@class='productDetailModule']//a[@itemprop='brand']/text()").toString();
	    	product.title = pageHtml.xpath("//div[@class='productDetailModule']//span[@itemprop='name']/text()").toString();
	    	product.price = pageHtml.xpath("//div[@class='productDetailModule']//span[@data-tstid='itemsalesprice']/text()").toString();
	    	if (product.price == null) {
	    		product.price = pageHtml.xpath("//div[@class='productDetailModule']//span[@data-tstid='itemprice']/text()").toString();
			}
//	    	product.color = pageHtml.xpath("//div[@id='colours']/h3/text()").toString();
//	    	if (product.color == null || product.color.equals("")) {
//	    		product.color = pageHtml.xpath("//div[@id='colours']/h3/span/text()").toString();
//			}
	    	product.imgPathList = pageHtml.xpath("//meta[@property='og:image']/@content").all();
	    	
	    	// 产品编码
	    	product.code = pageHtml.xpath("//span[@itemprop='sku']/text()").toString();
   	
	    	// 产品材质
	    	List<String> details = pageHtml.xpath("//div[@data-tstid='Content_Composition&Care']//dd/text()").all();
	    	StringBuffer stringBuffer = new StringBuffer();
	    	for (String detailItem : details) {
				if (detailItem.contains("%")) {
					stringBuffer.append(detailItem).append(", ");
				}
			}
	    	product.fabrics = stringBuffer.toString();
		} catch (Exception e) {
			System.out.println("=exception================================");
			product.printJsonString();
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		String url = "http://www.farfetch.com/shopping/women/Carolina-Herrera-floral-jacquard-gown-item-11315279.aspx?fsb=1";
		Html pageHtml = new HttpClientDownloader().download(url);
		ProductInfo product = new ProductInfo();
		new HtmlParserForFarfetch().parseHtmlToProductDetail(pageHtml, product);
	}
	
}
