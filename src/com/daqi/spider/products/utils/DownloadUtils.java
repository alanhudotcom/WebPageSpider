package com.daqi.spider.products.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

//import com.alibaba.fastjson.JSONObject;

public class DownloadUtils {
	
//	Executors.newCachedThreadPool();
	private final static ThreadPoolExecutor mExecutorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            2L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());
	static {
		mExecutorService.allowCoreThreadTimeOut(true);
	}
	
	public static ThreadPoolExecutor getExecutorService() {
		return mExecutorService;
	}
	
	public static void delImageFromDownload(String url, final String imgFileDirectory) {
		String imgName = String.valueOf(System.currentTimeMillis());
		if (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".jpeg")) {
			imgName = url.substring(url.lastIndexOf("/") + 1);
			if (!imgName.contains(".")) {
				imgName += ".jpg";
			}
		} else if (url.contains("=")) {
			imgName = url.substring(url.lastIndexOf("=") + 1);
			if (!imgName.contains(".")) {
				imgName += ".jpg";
			}
		}
		
		String imgPathName = imgFileDirectory + imgName;
		File imgFile = new File(imgPathName);
		imgFile.deleteOnExit();
	}
	
	public static void downloadImage(String url, final String imgFileDirectory) {
		String imgName = String.valueOf(System.currentTimeMillis());
		if (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".jpeg")) {
			imgName = url.substring(url.lastIndexOf("/") + 1);
			if (!imgName.contains(".")) {
				imgName += ".jpg";
			}
		} else if (url.contains("=")) {
			imgName = url.substring(url.lastIndexOf("=") + 1);
			if (!imgName.contains(".")) {
				imgName += ".jpg";
			}
		}
		
		String imgPathName = imgFileDirectory + imgName;
		downloadImageTask(new DownloadTask(url), imgPathName);
	}
	
	public static void downloadImageBatch(List<String> imgList, final String imgFileDirectory) {
		for (final String imgUrl : imgList) {
			mExecutorService.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					downloadImage(imgUrl, imgFileDirectory);
				}
			});
		}
	}
	
	private static void downloadImageTask(DownloadTask downloadTask, final String imgPathName) {
		try {
			downloadImageInner(downloadTask.downloadUrl, imgPathName);
			if (downloadTask.retryCnt > 0) {
				System.out.println("======download image success for " + downloadTask.retryCnt + " times");
			}
		} catch (Exception e) {
			// TODO: handle exception
			++downloadTask.retryCnt;
			if (downloadTask.retryCnt < 3) {
				try {
					Thread.sleep(5000);
				} catch (Exception e2) {
					// TODO: handle exception
				}
				downloadImageTask(downloadTask, imgPathName);
			} else {
				System.out.println("======download image 3 times error for " + downloadTask.downloadUrl);
			}
		}
	}
	
	private static void downloadImageInner(final String url, final String imgPathName) throws Exception {
		OutputStream os = null;
		InputStream is = null;
		try {
			// 打开连接
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(30 * 1000);
			conn.setReadTimeout(60 * 1000);
			// 输入流
			is = conn.getInputStream();
			// 4K的数据缓冲
			byte[] bs = new byte[4096];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			os = new FileOutputStream(imgPathName);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			// 完毕，关闭所有链接
			if (os != null) {
				try {
					os.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
	}
	
	public static void main0(String[] args) {
		String imgString = "http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=170466070, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=160921077, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=159411344, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=150802278, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=148392253, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=155318921, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=162177895, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=154199985, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=163153269, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=152370822, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=156846718, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=159366707, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=155316420, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=144413550, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=158584834, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=150923988, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=144237352, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=159047961, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=155317810, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=128018192, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=153479989, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=147906799, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=162652527, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=161625688, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=160205719, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=163516063, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=146300143, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=157630177, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=155318123, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=164135157, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=155321203, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=157641059, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=150410701, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=155321395, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=148055191, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=154981160, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=149594676, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=158346046, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=160254682, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=144136173, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=137174744, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=128020680, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=142255702, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=163436417, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=152370702, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=160919492, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=150484713, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=166646539, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=166193857, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=157637312, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=155317410, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=143855262, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=159366860, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=143930075, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=157243798, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=160925352, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=155317208, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=166786403, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=159570177, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=170464762, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=161193899, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=155049553, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=154203192, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=154205951, http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=167071707";
		String[] imgPathArray = imgString.split(", ");
		List<String> imgList = new ArrayList<String>();
		for (String string : imgPathArray) {
			imgList.add(string);
		}
		downloadImageBatch(imgList, EnviormentUtils.BASE_THUMB_IMAGE_PATH);
	}
	
	public static void main(String[] args) { 
		testAppStoreDownload();
	}
	
	public static void testAppStoreDownload() {
		JSONObject json = new JSONObject();
		json.put("dcType", 0);
		json.put("keyword", "偶搭");
		json.put("clFlag", 1);
		json.put("perCount", 15);
		json.put("page", 1);
		
		post("http://jsondata.25pp.com/index.html?tunnel-command=4262469668", json);
	}
	
    public static JSONObject post(String url,JSONObject json){
        HttpClient client = new DefaultHttpClient();  
        HttpPost post = new HttpPost(url);  
        JSONObject response = null;  
        try {  
            StringEntity s = new StringEntity(json.toString());  
            s.setContentEncoding("UTF-8");  
            s.setContentType("application/json");  
            post.setEntity(s);  
              
            HttpResponse res = client.execute(post);  
            if(res.getStatusLine().getStatusCode() == 200){  
                HttpEntity entity = res.getEntity();  
                String charset = EntityUtils.getContentCharSet(entity);  
                InputStreamReader reader = new InputStreamReader(entity.getContent(), charset);
                BufferedReader streamReader = new BufferedReader(reader); 
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                
                System.out.println(responseStrBuilder.toString());
                JSONObject jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
                System.out.println("==========That's all");
//                new JSONObject(responseStrBuilder.toString());
//                response = new JSONObject(new JSONToken(new InputStreamReader(entity.getContent(),charset)));  
            }  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
        return response;  
    } 
	
	
	private static class DownloadTask {
		String downloadUrl;
		Integer retryCnt;
		
		public DownloadTask(String url) {
			this(url, 0);
		}
		
		public DownloadTask(String url, int retryCount) {
			this.downloadUrl = url;
			this.retryCnt = retryCount;
		}
	}

}
