package com.webserver.servlet;

import java.io.RandomAccessFile;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

public class LoginServlet extends HttpServlet{
	
	public void service(HttpRequest request, HttpResponse response) {
		// ��ȡ������û���������
		System.out.println("��ʼ��¼...");
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		try (RandomAccessFile raf = new RandomAccessFile("user.dat", "r")) {
			// �ȶ��û���������
			byte[] b = new byte[32];
			boolean check = false;
			for (int i = 0; i < raf.length() / 100; i++) {
				raf.seek(i * 100);
				// ���û���
				raf.read(b);
				String dbUserName = new String(b, "utf-8").trim();

				// �ж��û���
				if (dbUserName.equals(username)) {
					// ��ȡ����
					raf.read(b);
					String dbPassword = new String(b, "utf-8").trim();
					// �ж�����
					if (dbPassword.equals(password)) {
						forward("myweb/login_success.html", request, response);
						check = true;
						System.out.println("��¼�ɹ�");
						break;
					}
				}
			}

			if (!check) {
				System.out.println("��½ʧ��");
				forward("myweb/login_fail.html", request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("��¼����!");
		}
	}
}
