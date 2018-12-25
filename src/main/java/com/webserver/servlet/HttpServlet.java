package com.webserver.servlet;

import java.io.File;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 所有Servlet的超类
 */
public abstract class HttpServlet {
	
	/**
	 * 业务处理方法
	 * @param request
	 * @param response
	 */
	public abstract void service(HttpRequest request, HttpResponse response);
	
	
	/**
	 * 转跳指定路径对应的资源
	 * 实际该方法将来(Tomcat)在转发器中,可以通过request获取
	 * @param path 
	 * @param request
	 * @param response
	 */
	public void forward(String path, HttpRequest request, HttpResponse response){
		File file = new File("webapps/" + path);
		response.setEntity(file);
	}
	
}
