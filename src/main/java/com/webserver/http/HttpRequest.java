package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象 该类的每个实例用于表示客户端浏览器发送过来的一个具体的请求信息. 一个请求包含三部分:请求行,消息头,消息正文
 */
public class HttpRequest {
	// 请求行相关信息
	private String method; // 请求方式
	private String url; // 资源路径
	private String protocol;// 协议版本

	private String requestURI;// url中的请求部分
	private String queryString;// url中的参数部分
	// 请求的所有参数,key:参数名 --- value:参数值
	private Map<String, String> parameters = new HashMap<String, String>();

	// 消息头相关信息定义
	private Map<String, String> headers = new HashMap<String, String>();

	// 消息正文相关信息定义

	/*
	 * 和客户端相连的相关属性
	 */
	private Socket socket;
	private InputStream in;

	/**
	 * 构造方法,用来初始化请求对象 初始化就是解析请求的过程.这里会根据Socket获取输入流,用来读取客户端发送过来的请求内容
	 * 将内容解析出来并设置到请求对象的对应属性上.
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException {
		try {
			this.socket = socket;
			// 通过socket获取输入流,用于读取客户端发送的请求内容
			this.in = socket.getInputStream();

			/*
			 * 解析请求内容需要做三件事: 1.解析请求行内容 2.解析消息头内容 3.解析消息正文内容
			 */
			parseRequestLine(); // 1
			parseHeaders(); // 2
			parseContent(); // 3
		} catch (IOException e) {
			e.printStackTrace();
		} catch (EmptyRequestException e) {
			// 空请求抛给ClientHandler
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析请求行
	 * 
	 * @throws EmptyRequestException
	 *             空请求异常
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("HttpRequest:解析请求行...");
		// 先通过输入流读取第一行字符串(CRLF)，因为一个请求中第一行内容就是请求行内容
		String line = readLine();
		if ("".equals(line)) {
			throw new EmptyRequestException();
		}

		System.out.println("请求行:" + line);

		/*
		 * 将请求行中的三部分信息: method url protocl 截取出来,并设置到对应的属性上
		 */
		String[] data = line.split("\\s");// 匹配空白字符进行分割
		method = data[0];
		url = data[1];
		protocol = data[2];

		// 进一步解析URL
		parseURL();

		System.out.println("method:" + method);
		System.out.println("url:" + url);
		System.out.println("protocol:" + protocol);
		System.out.println("HttpRequest:解析请求行完毕!");

	}

	/**
	 * 进一步解析URL
	 */
	private void parseURL() {
		System.out.println("HttpRequest:进一步解析url...");

		// url =
		// http://localhost:8088/myweb/reg?username=fucaijin&password=abc123&nickname=fcj&age=27

		//// 以下是我自己写的
		// String[] split = this.url.split("\\?");
		// // 判断是否有参数
		// if(split.length == 1){
		// requestURI = this.url;
		// }else{
		// // 如果有?则把左边,右边分别保存好
		// requestURI = split[0];//uri
		// queryString = split[1];//参数部分
		// System.out.println(queryString);
		//
		// // 将右边的参数进行分割
		// String[] info = queryString.split("&");//把参数部分,使用&分割出不同的参数
		// for (String detail : info) { // 遍历所有的参数
		// String[] data = detail.split("=");// 拿到每个参数的参数名和参数值
		// parameters.put(data[0], data[1]);
		// }
		// }

		// 以下是老师写的:
		// 首先判断当前请求中url是否含有参数
		if (url.indexOf("?") != -1) {
			String[] data = url.split("\\?");
			this.requestURI = data[0];
			// 判断"?"后面是否有实际的参数部分
			if (data.length > 1) {
				this.queryString = data[1];
				try {
					/*
					 * 将参数部分中的"%XX"还原为对应字符
					 */
					parseParameters(this.queryString);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		} else {
			this.requestURI = url;
		}

		System.out.println("requestURI:" + requestURI);
		System.out.println("queryString:" + queryString);
		System.out.println("parameters:" + parameters);

		System.out.println("HttpRequest:解析url完毕!");
	}
	
	/**
	 * 解析参数
	 * @throws UnsupportedEncodingException 
	 */
	private void parseParameters(String line) throws UnsupportedEncodingException{
		// 先对参数中含有"%XX"进行转码
		line = decode(line);
		// 进一步拆分参数
		String[] paraArr = line.split("&");
		// 遍历每一个参数进行拆分参数名和参数值
		for (String string : paraArr) {
			// 每个参数按"="拆分
			String[] arr = string.split("=");
			if (arr.length > 1) {
				parameters.put(arr[0], arr[1]);
			} else {
				parameters.put(arr[0], null);
			}
		}
		
	}
	

	/**
	 * 对给定字符串中"%XX"的内容解码
	 * 
	 * @param line
	 *            含有%XX的字符串
	 * @return 将line中的%XX内容替换为原意并返回
	 * @throws UnsupportedEncodingException
	 */
	private String decode(String line) throws UnsupportedEncodingException {
		return URLDecoder.decode(line, "utf-8");
	}

	/**
	 * 解析消息头
	 */
	private void parseHeaders() {
		System.out.println("HttpRequest:解析消息头...");
		/*
		 * 实现思路： 由于消息头是由多行构成的，对此我们应当循环的调用readLine方法读取每一行(每一个消息头),
		 * 若readLine方法返回的是一个空字符串时,说明应当单独读取到了CRLF,这就表示所有消息头都读取完毕了,那么就应当停止读取工作了.
		 * 并且我们在读取每行,即:每个消息头后,应当将该消息头按照冒号空格拆分为连线(消息头格式为name:value)
		 * 第一项应当是消息头名字,第二项为消息头的值.我们分别将它们以key,value存入到属性header这个map中,
		 * 这样我们最终就完成了解析消息头的工作.
		 */
		// 以下为我自己写的
		// String line = "";
		// while(!"".equals(line = readLine())){
		// String[] data = line.split(": ");
		// headers.put(data[0], data[1]);
		// }

		// 以下为老师代码:
		while (true) {
			String line = readLine();
			if ("".equals(line)) {
				break;
			}

			String[] data = line.split(": ");
			headers.put(data[0], data[1]);
		}

		System.out.println("headers:" + headers);
		System.out.println("HttpRequest:解析消息头完毕!");
	}

	/**
	 * 解析消息正文
	 */
	private void parseContent() {
		try {
			System.out.println("HttpRequest:解析消息正文...");
			/*
			 * 判断是否含有消息正文： 查看消息头中是否含有Content-length
			 */

			if (headers.containsKey("Content-Length")) {
				int len = Integer.parseInt(headers.get("Content-Length"));
				byte[] data = new byte[len];
				in.read(data);
				
				/*
				 * 判断消息正文的内容类型
				 * 查看消息头Content-Type
				 */
				String contentType = headers.get("Content-Type");
				if("application/x-www-form-urlencoded".equals(contentType)){
					
					String line = new String(data,"ISO8859-1");
					line = URLDecoder.decode(line, "utf-8");
					System.out.println("正文内容 : " + line);
					
					/*
					 * 将参数解析出来，存入到属性parameters中
					 */
					try {
						parseParameters(line);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					
					System.out.println(parameters);
				}
			}
			System.out.println("HttpRequest:解析消息正文完毕!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过输入流in读取一行字符串
	 * 连续读取若干字符串，当读取到CRLF时停止，并将之前读取的所有字符以一个字符串形式返回，返回的字符串中不含有最后的CRLF， CR:回车符 -
	 * 对应ASCII编码值13 LF:换行符 - 对应ASCII编码值10
	 */
	private String readLine() {
		StringBuilder sb = new StringBuilder();
		try {
			int pre = -1;// 记录上次while循环读取到的字符
			int cur = -1;// 记录本次while循环读取到的字符
			while ((cur = in.read()) != -1) {
				// 判断上次读取的是否为CR,本次是否为LF,如果是,就退出循环
				if (pre == 13 && cur == 10) {
					break;
				}
				sb.append((char) cur);
				pre = cur;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString().trim();// 将读取到的最后一个回车符trim掉
	}

	public String getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getHeader(String name) {
		return headers.get(name);
	}

	public String getRequestURI() {
		return requestURI;
	}

	public String getQueryString() {
		return queryString;
	}

	/**
	 * 根据给定的参数名,获取对应的参数值
	 */
	public String getParameter(String name) {
		return this.parameters.get(name);
	}
}
