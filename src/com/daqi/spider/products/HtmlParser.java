package com.daqi.spider.products;

import us.codecraft.webmagic.selector.Html;

public interface HtmlParser {
	
	public void parseHtmlToProductDetail(Html pageHtml, ProductInfo product);
}
