package com.webserver.servlet;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

public class UpdateServlet extends HttpServlet{

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		System.out.println("UpdateServlet:开始修改密码");
		String username = request.getParameter("username");
		String oldpwd = request.getParameter("oldpwd");
		String newpwd = request.getParameter("newpwd");
		
		// 判断用户名密码是否一致
		try (RandomAccessFile raf = new RandomAccessFile("user.dat", "rw")){
			boolean isUpdate = false;
			for(int i = 0; i < raf.length()/100; i++){
				raf.seek(i*100);
				byte[] data = new byte[32];
				raf.read(data);
				String user = new String(data, "utf-8").trim();
				// 判断用户名是否正确
				if(user.equals(username)){
					data = new byte[32];
					raf.read(data);
					String pwd = new String(data, "utf-8").trim();
					// 判断密码是否正确
					if(pwd.equals(oldpwd)){
						// 修改密码
						raf.seek(i*100+32);
						byte[] bytes = newpwd.getBytes("utf-8");
						bytes = Arrays.copyOf(bytes, 32);
						raf.write(bytes);
						isUpdate = true;
					}
					break;
				}
			}
			
			if(isUpdate){
				forward("myweb/update_success.html", request, response);
			}else{
				forward("myweb/update_fail.html", request, response);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
