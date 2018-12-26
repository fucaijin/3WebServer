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
 * �й�HTTPЭ��涨������
 */
public class HttpContext {
	/**
	 * ״̬������������Ӧ��ϵ key:״̬���� value:��Ӧ��״̬����
	 */
	private static final Map<Integer, String> STATUS_MAPPING = new HashMap<Integer, String>();

	/**
	 * ��Դ��׺��Content-Typeֵ�Ķ�Ӧ��ϵ key:��׺��,��:png value:Content-Type��Ӧֵ,��:image/png
	 */
	private static final Map<String, String> MIME_MAPPING = new HashMap<String, String>();

	static {
		// ��ʼ��

		// ��ʼ����״̬�������Ӧ����
		initStatusMapping();
		initMimeMapping();
	}

	/**
	 * ��ʼ��״̬�������Ӧ������
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
	 * ��ʼ����Դ��׺��Content-Typeֵ�Ķ�Ӧ
	 */
	private static void initMimeMapping(){
//		MIME_MAPPING.put("png", "image/png");
//		MIME_MAPPING.put("gif", "image/gif");
//		MIME_MAPPING.put("jpg", "image/jpeg");
//		MIME_MAPPING.put("css", "text/css");
//		MIME_MAPPING.put("html", "text/html");
//		MIME_MAPPING.put("js", "application/javascript");
		/*
		 * ͨ������web.xml�ļ���ʼ��MIME_MAPPING
		 * 1.����SAXReader
		 * 2.ʹ��SAXreader��ȡconfĿ¼�µ�web.xml�ļ�
		 * 3.��ȡ��Ԫ������������<mime-mapping>����Ԫ��
		 * 4.����ÿ��<mime-mapping>Ԫ��,��������Ԫ��:
		 * <extension>�м���ı���Ϊkey
		 * <mime-type>�м���ı���Ϊvalue
		 * ���浽MIME_MAPPING���Map�м���
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
	 * ����״̬���ȡ��Ӧ��״̬����
	 */
	public static String getStatusReason(int statusCode) {
		return STATUS_MAPPING.get(statusCode);
	}

	/**
	 * ������Դ��׺����ȡ��Ӧ��Content-Type��ֵ
	 * @param ext
	 * @return
	 */
	public static String getMimeType(String ext){
		return MIME_MAPPING.get(ext);
	}
	
	public static void main(String[] args) {
//		System.out.println(getStatusReason(404));
		
//		��ȡ�ļ���׺�������ַ�ʽ
		String fileName = "XXX.mp4";
		String[] split = fileName.split("\\.");
		System.out.println(getMimeType(split[split.length-1]));;//����1
//		System.out.println(fileName.substring(fileName.lastIndexOf(".")+1));//����2
//		initMimeMapping();
		
//		// ʮ������תunicode
//		try {
//			String decode = URLDecoder.decode("%5c%0f%9e%21%9e%21", "unicode");
//			System.out.println(decode);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		
		
		
	}
}
