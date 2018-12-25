package com.webserver.servlet;

import java.io.RandomAccessFile;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

public class LoginServlet extends HttpServlet{
	
	public void service(HttpRequest request, HttpResponse response) {
		// 获取请求的用户名和密码
		System.out.println("开始登录...");
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		try (RandomAccessFile raf = new RandomAccessFile("user.dat", "r")) {
			// 比对用户名和密码
			byte[] b = new byte[32];
			boolean check = false;
			for (int i = 0; i < raf.length() / 100; i++) {
				raf.seek(i * 100);
				// 读用户名
				raf.read(b);
				String dbUserName = new String(b, "utf-8").trim();

				// 判断用户名
				if (dbUserName.equals(username)) {
					// 读取密码
					raf.read(b);
					String dbPassword = new String(b, "utf-8").trim();
					// 判断密码
					if (dbPassword.equals(password)) {
						forward("myweb/login_success.html", request, response);
						check = true;
						System.out.println("登录成功");
						break;
					}
				}
			}

			if (!check) {
				System.out.println("登陆失败");
				forward("myweb/login_fail.html", request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("登录结束!");
		}
	}
}
