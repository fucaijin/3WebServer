package com.webserver.core;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 将user.dat文件中的每个用户信息读取出来并输出到控制台
 */
public class ShowAllUserDemo {

	public static void main(String[] args) throws IOException {
		RandomAccessFile raf = new RandomAccessFile("user.dat","r");
		
		String str;
		for (int i = 0; i < raf.length()/100; i++) {
			byte[] b = new byte[32];
			raf.read(b);
			str = new String(b,"utf-8");
			System.out.println("用户名：" + str);
			
			raf.read(b);
			str = new String(b,"utf-8");
			System.out.println("密码：" + str);
			
			raf.read(b);
			str = new String(b,"utf-8");
			System.out.println("昵称：" + str);
			
			System.out.println("年龄：" + raf.readInt());
		}
		
		raf.close();
		
	}

}
