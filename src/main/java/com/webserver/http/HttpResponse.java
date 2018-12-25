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
 * ��Ӧ���� �����ÿ��ʵ����ʾ����˷��͸��ͻ��˵�һ������HTTP��Ӧ���� һ��HTTP��Ӧ����������: ״̬��\��Ӧͷ\��Ӧ����
 *
 */
public class HttpResponse {
	// ״̬�������Ϣ����
	private int statusCode = 200; // ״̬����Ĭ��Ϊ:200
	private String statusReason = "OK"; // ״̬����Ĭ��Ϊ:OK

	// ��Ӧͷ�����Ϣ����
	private Map<String, String> headers = new HashMap<String, String>();

	// ��Ӧ���������Ϣ����
	// ��Ӧ��ʵ���ļ�
	private File entity;

	// �����������Ϣ
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
	 * ����ǰ��Ӧ�������ݰ��ձ�׼��HTTP��Ӧ��ʽ���͸��ͻ���
	 */
	public void flush() {
		/*
		 * 1.����״̬�� 2.������Ӧͷ 3.������Ӧ����
		 */
		sendStatusLine();
		sendHeaders();
		sendContent();
	}

	/**
	 * 1.����״̬��
	 */
	public void sendStatusLine() {
		System.out.println("HttpResponse:��ʼ����״̬��...");
		try {
			String line = "HTTP/1.1" + " " + statusCode + " " + statusReason;
			out.write(line.getBytes("ISO8859-1"));
			out.write(13); // written CR
			out.write(10); // written LF
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("HttpResponse:����״̬�����!");
	}

	/**
	 * 2.������Ӧͷ
	 */
	public void sendHeaders() {
		System.out.println("HttpResponse:��ʼ������Ӧͷ...");
		try {
			// ����headers,��ÿ����Ӧͷ����
			Set<Entry<String, String>> entrySet = headers.entrySet();
			for (Entry<String, String> header : entrySet) {
				String line = header.getKey() + ": " + header.getValue();
				out.write(line.getBytes("ISO8859-1"));
				out.write(13); // written CR
				out.write(10); // written LF
			}

			// ��������һ��CRLF��ʾ��Ӧͷ�������
			out.write(13); // written CR
			out.write(10); // written LF
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("HttpResponse:������Ӧͷ���!");
	}

	/**
	 * 3.������Ӧ����
	 */
	public void sendContent() {
		System.out.println("HttpResponse:��ʼ������Ӧ����...");
		if (entity == null) {
			return;
		}
		// try���˳�ʱ�����Զ�����fis.close()�������ر���Դ��
		try (FileInputStream fis = new FileInputStream(entity);) {
			byte[] b = new byte[1024 * 10];
			int len = -1;
			while ((len = fis.read(b)) != -1) {
				out.write(b, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("HttpResponse:������Ӧ�������!");
	}

	public File getEntity() {
		return entity;
	}

	/**
	 * ������Ӧ�����е�ʵ���ļ� �����ø��ļ���ͬʱ,���Զ������ļ����������Ӧͷ:Content-Type��Content-Length
	 * @param entity
	 */
	public void setEntity(File entity) {
		/*
		 * ���Content-Type
		 * ���ݸ������ļ������ֵĺ�׺����HttpContext�л�ȡ��Ӧ��Context-Type
		 */
		String fileName = entity.getName();
		String ext = fileName.substring(fileName.lastIndexOf(".")+1);//��ȡ�ļ���׺��
		String contentType = HttpContext.getMimeType(ext);
		
		// ���Content-Type
		this.headers.put("Content-Type", contentType);
		// ���Content-Length
		this.headers.put("Content-Length", String.valueOf(entity.length()));
		this.entity = entity;
	}

	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * ����״̬���� �����õ�ͬʱ,�ڲ�����ݸ�״̬����ȥHttpContext�л�ȡ�ô����Ӧ��״̬����ֵ,���Զ���������.
	 * ��������ʡȥ�����ÿ������״̬�����Ҫ��������״̬����������.
	 * ������Ҫ���ô���������ò�ͬ��״̬����ֵ,����Ͳ���Ҫ�ٵ���setStatusReason()������
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		// �Զ����ö�Ӧ��״̬����
		this.statusReason = HttpContext.getStatusReason(statusCode);
	}

	public String getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	/**
	 * ���ָ������Ӧͷ
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
