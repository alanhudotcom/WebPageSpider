package com.daqi.spider.products;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.selector.Html;

public class HtmlParserForTheoutnet implements HtmlParser {

	@Override
	public void parseHtmlToProductDetail(Html pageHtml, ProductInfo product) {
		try {
			product.brand = pageHtml.xpath("//div[@id='product-heading']/h1/a/text()").toString();
	    	product.title = pageHtml.xpath("//div[@id='product-heading']//span/text()").toString();
	    	product.price = pageHtml.xpath("//span[@class='exact-price']/text()").toString();
	    	product.color = pageHtml.xpath("//div[@id='colours']/h3/text()").toString();
	    	if (product.color == null || product.color.equals("")) {
	    		product.color = pageHtml.xpath("//div[@id='colours']/h3/span/text()").toString();
			}
	    	product.imgPathList = pageHtml.xpath("//div[@id='expanded-image-container']/ul/li/a//@href").all();
	    	
	    	// 产品编码
	    	String code = pageHtml.xpath("//div[@class='tab-details translateSection']/regex('Product code:&nbsp;\\d+')").toString();
	    	if (code == null || code.equals("")) {
	    		code = pageHtml.xpath("//div[@id='product-code']/regex('Product code:&nbsp;\\d+')").toString();
			}
	    	if (code != null) {
	    		String regEx="[^0-9]";   
		    	Pattern p = Pattern.compile(regEx);   
		    	Matcher m = p.matcher(code);   
		    	product.code = m.replaceAll("").trim();
			} else {
//				System.out.println(pageHtml.toString());
				System.out.println("=code 获取失败，可能页面结构发生了变化，需要修改爬虫的解析================================");
				product.printJsonString();
			}
   	
	    	// 产品材质
	    	List<String> details = pageHtml.xpath("//div[@class='tab-details translateSection']/ul/li").all();
	    	for (String detailItem : details) {
				if (detailItem.contains("%")) {
					product.fabrics = detailItem.subSequence(detailItem.indexOf(">") + 1, detailItem.lastIndexOf("<")).toString();
					break;
				}
			}
		} catch (Exception e) {
//			System.out.println(pageHtml.toString());
			System.out.println("=exception================================");
			product.printJsonString();
			e.printStackTrace();
		}
		
	}
}
