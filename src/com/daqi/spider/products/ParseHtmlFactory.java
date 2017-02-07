package com.daqi.spider.products;

import us.codecraft.webmagic.selector.Html;

public class ParseHtmlFactory {
	
	private static String[] SUPPORT_WEB = {"theoutnet.com", "mytheresa.com", "yoox.com",
											"ifchic.com", "farfetch.com", "tradesy.com"
									};
	
	public static ProductInfo parseHtmlToProductInfo(Html pageHtml, String pageUrlString) {
		ProductInfo product = new ProductInfo();
		product.url = pageUrlString;
		HtmlParser parser = createHtmlParser(pageUrlString);
		if (parser != null) parser.parseHtmlToProductDetail(pageHtml, product);
    	
    	return product;
	}

	
	private static HtmlParser createHtmlParser(String pageUrlString) {
		if (pageUrlString.contains("theoutnet.com")) {
			return new HtmlParserForTheoutnet();
		} else if (pageUrlString.contains("mytheresa.com")) {
			return new HtmlParserForMytheresa();
		} else if (pageUrlString.contains("ifchic.com")) {
			return new HtmlParserForIfchic();
		} else if (pageUrlString.contains("farfetch.com")) {
			return new HtmlParserForFarfetch();
		} else if (pageUrlString.contains("tradesy.com")) {
			return new HtmlParserForTradesy();
		} else if (pageUrlString.contains("yoox.com")) {
			return new HtmlParserForYoox();
		}
		return null;
	}
	
	public static boolean isSupportWebPage(String pageUrlString) {
		for (String web : SUPPORT_WEB) {
			if (pageUrlString.contains(web)) {
				return true;
			}
		} 
		return false;
	}
}
