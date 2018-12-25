package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * ������� �����ÿ��ʵ�����ڱ�ʾ�ͻ�����������͹�����һ�������������Ϣ. һ���������������:������,��Ϣͷ,��Ϣ����
 */
public class HttpRequest {
	// �����������Ϣ
	private String method; // ����ʽ
	private String url; // ��Դ·��
	private String protocol;// Э��汾

	private String requestURI;// url�е����󲿷�
	private String queryString;// url�еĲ�������
	// ��������в���,key:������ --- value:����ֵ
	private Map<String, String> parameters = new HashMap<String, String>();

	// ��Ϣͷ�����Ϣ����
	private Map<String, String> headers = new HashMap<String, String>();

	// ��Ϣ���������Ϣ����

	/*
	 * �Ϳͻ����������������
	 */
	private Socket socket;
	private InputStream in;

	/**
	 * ���췽��,������ʼ��������� ��ʼ�����ǽ�������Ĺ���.��������Socket��ȡ������,������ȡ�ͻ��˷��͹�������������
	 * �����ݽ������������õ��������Ķ�Ӧ������.
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException {
		try {
			this.socket = socket;
			// ͨ��socket��ȡ������,���ڶ�ȡ�ͻ��˷��͵���������
			this.in = socket.getInputStream();

			/*
			 * ��������������Ҫ��������: 1.�������������� 2.������Ϣͷ���� 3.������Ϣ��������
			 */
			parseRequestLine(); // 1
			parseHeaders(); // 2
			parseContent(); // 3
		} catch (IOException e) {
			e.printStackTrace();
		} catch (EmptyRequestException e) {
			// �������׸�ClientHandler
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����������
	 * 
	 * @throws EmptyRequestException
	 *             �������쳣
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("HttpRequest:����������...");
		// ��ͨ����������ȡ��һ���ַ���(CRLF)����Ϊһ�������е�һ�����ݾ�������������
		String line = readLine();
		if ("".equals(line)) {
			throw new EmptyRequestException();
		}

		System.out.println("������:" + line);

		/*
		 * ���������е���������Ϣ: method url protocl ��ȡ����,�����õ���Ӧ��������
		 */
		String[] data = line.split("\\s");// ƥ��հ��ַ����зָ�
		method = data[0];
		url = data[1];
		protocol = data[2];

		// ��һ������URL
		parseURL();

		System.out.println("method:" + method);
		System.out.println("url:" + url);
		System.out.println("protocol:" + protocol);
		System.out.println("HttpRequest:�������������!");

	}

	/**
	 * ��һ������URL
	 */
	private void parseURL() {
		System.out.println("HttpRequest:��һ������url...");

		// url =
		// http://localhost:8088/myweb/reg?username=fucaijin&password=abc123&nickname=fcj&age=27

		//// ���������Լ�д��
		// String[] split = this.url.split("\\?");
		// // �ж��Ƿ��в���
		// if(split.length == 1){
		// requestURI = this.url;
		// }else{
		// // �����?������,�ұ߷ֱ𱣴��
		// requestURI = split[0];//uri
		// queryString = split[1];//��������
		// System.out.println(queryString);
		//
		// // ���ұߵĲ������зָ�
		// String[] info = queryString.split("&");//�Ѳ�������,ʹ��&�ָ����ͬ�Ĳ���
		// for (String detail : info) { // �������еĲ���
		// String[] data = detail.split("=");// �õ�ÿ�������Ĳ������Ͳ���ֵ
		// parameters.put(data[0], data[1]);
		// }
		// }

		// ��������ʦд��:
		// �����жϵ�ǰ������url�Ƿ��в���
		if (url.indexOf("?") != -1) {
			String[] data = url.split("\\?");
			this.requestURI = data[0];
			// �ж�"?"�����Ƿ���ʵ�ʵĲ�������
			if (data.length > 1) {
				this.queryString = data[1];
				try {
					/*
					 * �����������е�"%XX"��ԭΪ��Ӧ�ַ�
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

		System.out.println("HttpRequest:����url���!");
	}
	
	/**
	 * ��������
	 * @throws UnsupportedEncodingException 
	 */
	private void parseParameters(String line) throws UnsupportedEncodingException{
		// �ȶԲ����к���"%XX"����ת��
		line = decode(line);
		// ��һ����ֲ���
		String[] paraArr = line.split("&");
		// ����ÿһ���������в�ֲ������Ͳ���ֵ
		for (String string : paraArr) {
			// ÿ��������"="���
			String[] arr = string.split("=");
			if (arr.length > 1) {
				parameters.put(arr[0], arr[1]);
			} else {
				parameters.put(arr[0], null);
			}
		}
		
	}
	

	/**
	 * �Ը����ַ�����"%XX"�����ݽ���
	 * 
	 * @param line
	 *            ����%XX���ַ���
	 * @return ��line�е�%XX�����滻Ϊԭ�Ⲣ����
	 * @throws UnsupportedEncodingException
	 */
	private String decode(String line) throws UnsupportedEncodingException {
		return URLDecoder.decode(line, "utf-8");
	}

	/**
	 * ������Ϣͷ
	 */
	private void parseHeaders() {
		System.out.println("HttpRequest:������Ϣͷ...");
		/*
		 * ʵ��˼·�� ������Ϣͷ���ɶ��й��ɵģ��Դ�����Ӧ��ѭ���ĵ���readLine������ȡÿһ��(ÿһ����Ϣͷ),
		 * ��readLine�������ص���һ�����ַ���ʱ,˵��Ӧ��������ȡ����CRLF,��ͱ�ʾ������Ϣͷ����ȡ�����,��ô��Ӧ��ֹͣ��ȡ������.
		 * ���������ڶ�ȡÿ��,��:ÿ����Ϣͷ��,Ӧ��������Ϣͷ����ð�ſո���Ϊ����(��Ϣͷ��ʽΪname:value)
		 * ��һ��Ӧ������Ϣͷ����,�ڶ���Ϊ��Ϣͷ��ֵ.���Ƿֱ�������key,value���뵽����header���map��,
		 * �����������վ�����˽�����Ϣͷ�Ĺ���.
		 */
		// ����Ϊ���Լ�д��
		// String line = "";
		// while(!"".equals(line = readLine())){
		// String[] data = line.split(": ");
		// headers.put(data[0], data[1]);
		// }

		// ����Ϊ��ʦ����:
		while (true) {
			String line = readLine();
			if ("".equals(line)) {
				break;
			}

			String[] data = line.split(": ");
			headers.put(data[0], data[1]);
		}

		System.out.println("headers:" + headers);
		System.out.println("HttpRequest:������Ϣͷ���!");
	}

	/**
	 * ������Ϣ����
	 */
	private void parseContent() {
		try {
			System.out.println("HttpRequest:������Ϣ����...");
			/*
			 * �ж��Ƿ�����Ϣ���ģ� �鿴��Ϣͷ���Ƿ���Content-length
			 */

			if (headers.containsKey("Content-Length")) {
				int len = Integer.parseInt(headers.get("Content-Length"));
				byte[] data = new byte[len];
				in.read(data);
				
				/*
				 * �ж���Ϣ���ĵ���������
				 * �鿴��ϢͷContent-Type
				 */
				String contentType = headers.get("Content-Type");
				if("application/x-www-form-urlencoded".equals(contentType)){
					
					String line = new String(data,"ISO8859-1");
					line = URLDecoder.decode(line, "utf-8");
					System.out.println("�������� : " + line);
					
					/*
					 * �������������������뵽����parameters��
					 */
					try {
						parseParameters(line);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					
					System.out.println(parameters);
				}
			}
			System.out.println("HttpRequest:������Ϣ�������!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ͨ��������in��ȡһ���ַ���
	 * ������ȡ�����ַ���������ȡ��CRLFʱֹͣ������֮ǰ��ȡ�������ַ���һ���ַ�����ʽ���أ����ص��ַ����в���������CRLF�� CR:�س��� -
	 * ��ӦASCII����ֵ13 LF:���з� - ��ӦASCII����ֵ10
	 */
	private String readLine() {
		StringBuilder sb = new StringBuilder();
		try {
			int pre = -1;// ��¼�ϴ�whileѭ����ȡ�����ַ�
			int cur = -1;// ��¼����whileѭ����ȡ�����ַ�
			while ((cur = in.read()) != -1) {
				// �ж��ϴζ�ȡ���Ƿ�ΪCR,�����Ƿ�ΪLF,�����,���˳�ѭ��
				if (pre == 13 && cur == 10) {
					break;
				}
				sb.append((char) cur);
				pre = cur;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString().trim();// ����ȡ�������һ���س���trim��
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
	 * ���ݸ����Ĳ�����,��ȡ��Ӧ�Ĳ���ֵ
	 */
	public String getParameter(String name) {
		return this.parameters.get(name);
	}
}
