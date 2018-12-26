package com.webserver.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 有关HTTP协议规定的内容
 */
public class HttpContext {
	/**
	 * 状态代码与描述对应关系 key:状态代码 value:对应的状态描述
	 */
	private static final Map<Integer, String> STATUS_MAPPING = new HashMap<Integer, String>();

	/**
	 * 资源后缀与Content-Type值的对应关系 key:后缀名,如:png value:Content-Type对应值,如:image/png
	 */
	private static final Map<String, String> MIME_MAPPING = new HashMap<String, String>();

	static {
		// 初始化

		// 初始化了状态代码与对应描述
		initStatusMapping();
		initMimeMapping();
	}

	/**
	 * 初始化状态代码与对应的描述
	 */
	private static void initStatusMapping() {
		STATUS_MAPPING.put(200, "OK");
		STATUS_MAPPING.put(201, "Created");
		STATUS_MAPPING.put(202, "Accepted");
		STATUS_MAPPING.put(204, "No Content");
		STATUS_MAPPING.put(301, "Moved Permanently");
		STATUS_MAPPING.put(302, "Moved Temporarily");
		STATUS_MAPPING.put(304, "Not Modified");
		STATUS_MAPPING.put(400, "Bad Request");
		STATUS_MAPPING.put(401, "Unauthorized");
		STATUS_MAPPING.put(403, "Forbidden");
		STATUS_MAPPING.put(404, "Not Found");
		STATUS_MAPPING.put(500, "Internal Server Error");
		STATUS_MAPPING.put(501, "Not Implemented");
		STATUS_MAPPING.put(502, "Bad Gateway");
		STATUS_MAPPING.put(503, "Service Unavailable");
	}

	/**
	 * 初始化资源后缀与Content-Type值的对应
	 */
	private static void initMimeMapping(){
//		MIME_MAPPING.put("png", "image/png");
//		MIME_MAPPING.put("gif", "image/gif");
//		MIME_MAPPING.put("jpg", "image/jpeg");
//		MIME_MAPPING.put("css", "text/css");
//		MIME_MAPPING.put("html", "text/html");
//		MIME_MAPPING.put("js", "application/javascript");
		/*
		 * 通过解析web.xml文件初始化MIME_MAPPING
		 * 1.创建SAXReader
		 * 2.使用SAXreader读取conf目录下的web.xml文件
		 * 3.获取根元素下所有名字<mime-mapping>的子元素
		 * 4.遍历每个<mime-mapping>元素,并将其子元素:
		 * <extension>中间的文本作为key
		 * <mime-type>中间的文本作为value
		 * 保存到MIME_MAPPING这个Map中即可
		 */
		try{
			SAXReader sax = new SAXReader();
			Document doc = sax.read(new File("./conf/web.xml"));
			Element root = doc.getRootElement();
			List<Element> mimetList = root.elements("mime-mapping");
			for(Element mimeElement : mimetList){
				String key = mimeElement.elementTextTrim("extension");
				String value = mimeElement.elementTextTrim("mime-type");
				MIME_MAPPING.put(key, value);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(MIME_MAPPING);
		System.out.println(MIME_MAPPING.size());
	}
	
	/**
	 * 根据状态码获取对应的状态描述
	 */
	public static String getStatusReason(int statusCode) {
		return STATUS_MAPPING.get(statusCode);
	}

	/**
	 * 根据资源后缀名获取对应的Content-Type的值
	 * @param ext
	 * @return
	 */
	public static String getMimeType(String ext){
		return MIME_MAPPING.get(ext);
	}
	
	public static void main(String[] args) {
//		System.out.println(getStatusReason(404));
		
//		截取文件后缀名的两种方式
		String fileName = "XXX.mp4";
		String[] split = fileName.split("\\.");
		System.out.println(getMimeType(split[split.length-1]));;//方法1
//		System.out.println(fileName.substring(fileName.lastIndexOf(".")+1));//方法2
//		initMimeMapping();
		
//		// 十六进制转unicode
//		try {
//			String decode = URLDecoder.decode("%5c%0f%9e%21%9e%21", "unicode");
//			System.out.println(decode);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		
		
		
	}
}
