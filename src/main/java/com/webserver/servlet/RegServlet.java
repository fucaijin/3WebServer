package com.webserver.servlet;

import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 用于处理实际业务
 */
public class RegServlet extends HttpServlet{

	public void service(HttpRequest request, HttpResponse response) {
		/*
		 * 注册流程:
		 * 1.通过request获取用户提交的注册信息
		 * 2.将用户信息写入文件user.dat中
		 * 3.设置response对应的注册结果页面
		 */
		System.out.println("Regservlet:开始注册...");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		int age = Integer.parseInt(request.getParameter("age"));
		System.out.println(username + "," + password + "," + nickname + "," + age);
		
		
		/*
		 * 其中用户名,密码,昵称为字符串,年龄为整数
		 * 因此我们每条记录占100字节
		 * 用户名,密码,昵称各占32字节,年龄各占4字节
		 */
		try (RandomAccessFile raf = new RandomAccessFile("user.dat", "rw")){
			raf.seek(raf.length());
			
			// 写用户名
			byte[] data = username.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			// 写密码
			data = password.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//写昵称
			data = nickname.getBytes("utf-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//写年龄
			raf.writeInt(age);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 3响应用户注册成功
		forward("myweb/reg_success.html", request, response);
		
		System.out.println("Regservlet:注册完毕");
	}

}
