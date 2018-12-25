package com.webserver.servlet;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * ���ڴ���ʵ��ҵ��
 */
public class RegServlet extends HttpServlet{

	public void service(HttpRequest request, HttpResponse response) {
		/*
		 * ע������:
		 * 1.ͨ��request��ȡ�û��ύ��ע����Ϣ
		 * 2.���û���Ϣд���ļ�user.dat��
		 * 3.����response��Ӧ��ע����ҳ��
		 */
		System.out.println("Regservlet:��ʼע��...");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		int age = Integer.parseInt(request.getParameter("age"));
		System.out.println(username + "," + password + "," + nickname + "," + age);
		
		
		/*
		 * �����û���,����,�ǳ�Ϊ�ַ���,����Ϊ����
		 * �������ÿ����¼ռ100�ֽ�
		 * �û���,����,�ǳƸ�ռ32�ֽ�,�����ռ4�ֽ�
		 */
		try (RandomAccessFile raf = new RandomAccessFile("user.dat", "rw")){
			raf.seek(raf.length());
			
			// д�û���
			byte[] data = username.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			// д����
			data = password.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//д�ǳ�
			data = nickname.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//д����
			raf.writeInt(age);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 3��Ӧ�û�ע��ɹ�
		forward("myweb/reg_success.html", request, response);
		
		System.out.println("Regservlet:ע�����");
	}

}
