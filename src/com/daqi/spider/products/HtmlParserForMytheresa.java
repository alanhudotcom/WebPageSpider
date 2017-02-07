package com.daqi.spider.products;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.selector.Html;

public class HtmlParserForMytheresa implements HtmlParser {

	@Override
	public void parseHtmlToProductDetail(Html pageHtml, ProductInfo product) {
		try {
			product.brand = pageHtml.xpath("//div[@class='product-designer']/span/a/text()").toString();
	    	product.title = pageHtml.xpath("//div[@class='product-name']/span/text()").toString();
	    	product.price = pageHtml.xpath("//span[@class='price']/text()").toString();
//	    	product.color = pageHtml.xpath("//div[@id='colours']/h3/text()").toString();
//	    	if (product.color == null || product.color.equals("")) {
//	    		product.color = pageHtml.xpath("//div[@id='colours']/h3/span/text()").toString();
//			}
	    	List<String> tmpImgList = pageHtml.xpath("//div[@class='product-image-gallery']/div/img/@src").all();
	    	product.imgPathList = new ArrayList<String>(tmpImgList.size());
	    	for (String imgPath : tmpImgList) {
				if (!imgPath.startsWith("http:")) {
					String newImgPath = "http:";
					newImgPath += imgPath;
					product.imgPathList.add(newImgPath);
				} else {
					product.imgPathList.add(imgPath);
				}
			}
	    	
	    	// 产品编码
	    	String code = pageHtml.xpath("//div[@class='product-sku']/span/text()").toString();
	    	if (code != null) {
	    		String regEx="[^0-9]";   
		    	Pattern p = Pattern.compile(regEx);   
		    	Matcher m = p.matcher(code);   
		    	product.code = m.replaceAll("").trim();
			} else {
				System.out.println("=code 获取失败，可能页面结构发生了变化，需要修改爬虫的解析================================");
				product.printJsonString();
			}
   	
	    	// 产品材质
	    	List<String> details = pageHtml.xpath("//ul[@class='disc featurepoints']/li").all();
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
