package com.daqi.spider.products;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.daqi.spider.products.utils.ProductUtils;

public class ProductInfo implements Serializable {

	public String price;					// 价格
	public String color;					// 颜色
	public String code;						// 货号
	public String brand;					// 品牌
	public String title;					// 标题
	public String fabrics;					// 面料
	public List<String> imgPathList = new ArrayList<String>(5);		// 图片列表
	
	public String url;						// 单品链接地址页面
	
	public void printJsonString() {
		String jsonString = JSONObject.toJSONString(this);
		System.out.println(jsonString);
	}
	
	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
	
	public String showAllInfoWithZH() {
		StringBuffer sb = new StringBuffer();
		String endLine = "\r\n";
		sb.append("价格:").append(showPriceWithZH()).append(endLine);
		sb.append("颜色:").append(showColorWithZH()).append(endLine);
		sb.append("货号:").append(code).append(endLine);
		sb.append("品牌:").append(showBrandWithZH()).append(endLine);
		sb.append("标题:").append(showTitleWithZH()).append(endLine);
		sb.append("面料:").append(showFbricsWithZH()).append(endLine);
		sb.append("链接地址:").append(url).append(endLine);
		
		return sb.toString();
	}
	
	private String showPriceWithZH() {
		return ProductUtils.convertPriceInfo(price);
	}
	
	private String showColorWithZH() {
		return ProductUtils.convertInfoToZH(color);
	}
	
	private String showFbricsWithZH() {
		return ProductUtils.convertInfoToZH(fabrics);
	}
	
	private String showBrandWithZH() {
		return ProductUtils.convertInfoToZH(brand);
	}
	
	private String showTitleWithZH() {
		return ProductUtils.convertInfoToZH(title);
	}
}
