package com.webserver.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * ���ඨ���йط����������Ϣ
 * key:����·��
 * value:��Ӧ��Servlet�����ȫ�޶���(����.����)
 */
public class ServerContext {
	private static final Map<String, String> SERVLET_MAPPING = new HashMap<String, String>();
	static{
		// ��ʼ��
		initServletMapping();
	}
	
	/**
	 * ��ʼ���������Ӧ��Servlet����
	 */
	private static void initServletMapping(){
//		SERVLET_MAPPING.put("/myweb/reg", "com.webserver.servlet.RegServlet");
//		SERVLET_MAPPING.put("/myweb/login", "com.webserver.servlet.LoginServlet");
		
		/*
		 * ����conf/servlets.xml
		 * ����Ԫ��<servlets>�µ�����<servlet>Ԫ��ȡ��
		 * ����ÿ��<servlet>Ԫ���е�����:
		 * url��ֵ��Ϊkey,className��ֵ��Ϊvalue
		 * ���浽SERVLET_MAPPING���Map����ɳ�ʼ��
		 */
		
		try {
			SAXReader sax = new SAXReader();
			Document doc = sax.read("conf/servlets.xml");
			Element root = doc.getRootElement();
			List<Element> eleList = root.elements("servlet");
			for(Element e : eleList){
				String url = e.attributeValue("url");
				String className = e.attributeValue("className");
				SERVLET_MAPPING.put(url, className);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������ȡ��Ӧ��ҵ�����������
	 * @param url
	 * @return
	 */
	public static String getServletName(String url){
		return SERVLET_MAPPING.get(url);
	}
	
	
	public static void main(String[] args) {
		System.out.println(getServletName("/myweb/login"));
	}
}
