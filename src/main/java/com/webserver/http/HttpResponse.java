package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 响应对象 该类的每个实例表示服务端发送给客户端的一个具体HTTP响应内容 一个HTTP响应包含三部分: 状态行\响应头\响应正文
 *
 */
public class HttpResponse {
	// 状态行相关信息定义
	private int statusCode = 200; // 状态代码默认为:200
	private String statusReason = "OK"; // 状态描述默认为:OK

	// 响应头相关信息定义
	private Map<String, String> headers = new HashMap<String, String>();

	// 响应正文相关信息定义
	// 响应的实体文件
	private File entity;

	// 和连接相关信息
	private Socket socket;
	private OutputStream out;

	public HttpResponse(Socket socket) {
		try {
			this.socket = socket;
			out = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将当前响应对象内容按照标准的HTTP响应格式发送给客户端
	 */
	public void flush() {
		/*
		 * 1.发送状态行 2.发送响应头 3.发送响应正文
		 */
		sendStatusLine();
		sendHeaders();
		sendContent();
	}

	/**
	 * 1.发送状态行
	 */
	public void sendStatusLine() {
		System.out.println("HttpResponse:开始发送状态行...");
		try {
			String line = "HTTP/1.1" + " " + statusCode + " " + statusReason;
			out.write(line.getBytes("ISO8859-1"));
			out.write(13); // written CR
			out.write(10); // written LF
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("HttpResponse:发送状态行完毕!");
	}

	/**
	 * 2.发送响应头
	 */
	public void sendHeaders() {
		System.out.println("HttpResponse:开始发送响应头...");
		try {
			// 遍历headers,将每个响应头发送
			Set<Entry<String, String>> entrySet = headers.entrySet();
			for (Entry<String, String> header : entrySet) {
				String line = header.getKey() + ": " + header.getValue();
				out.write(line.getBytes("ISO8859-1"));
				out.write(13); // written CR
				out.write(10); // written LF
			}

			// 单独发送一个CRLF表示响应头发送完毕
			out.write(13); // written CR
			out.write(10); // written LF
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("HttpResponse:发送响应头完毕!");
	}

	/**
	 * 3.发送响应正文
	 */
	public void sendContent() {
		System.out.println("HttpResponse:开始发送响应正文...");
		if (entity == null) {
			return;
		}
		// try块退出时，会自动调用fis.close()方法，关闭资源。
		try (FileInputStream fis = new FileInputStream(entity);) {
			byte[] b = new byte[1024 * 10];
			int len = -1;
			while ((len = fis.read(b)) != -1) {
				out.write(b, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("HttpResponse:发送响应正文完毕!");
	}

	public File getEntity() {
		return entity;
	}

	/**
	 * 设置响应正文中的实体文件 在设置该文件的同时,会自动根据文件添加两个响应头:Content-Type和Content-Length
	 * @param entity
	 */
	public void setEntity(File entity) {
		/*
		 * 添加Content-Type
		 * 根据给定的文件的名字的后缀，从HttpContext中获取对应的Context-Type
		 */
		String fileName = entity.getName();
		String ext = fileName.substring(fileName.lastIndexOf(".")+1);//获取文件后缀名
		String contentType = HttpContext.getMimeType(ext);
		
		// 添加Content-Type
		this.headers.put("Content-Type", contentType);
		// 添加Content-Length
		this.headers.put("Content-Length", String.valueOf(entity.length()));
		this.entity = entity;
	}

	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * 设置状态代码 在设置的同时,内部会根据该状态代码去HttpContext中获取该代码对应的状态描述值,并自动进行设置.
	 * 这样做就省去了外界每次设置状态代码后还要单独进行状态描述的设置.
	 * 除非需要给该代码额外设置不同的状态描述值,否则就不需要再调用setStatusReason()方法了
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		// 自动设置对应的状态描述
		this.statusReason = HttpContext.getStatusReason(statusCode);
	}

	public String getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	/**
	 * 添加指定的响应头
	 * 
	 * @param name
	 * @param value
	 */
	public void putHeader(String name, String value) {
		this.headers.put(name, value);
	}

	public String getHeader(String name) {
		return this.headers.get(name);
	}

}
