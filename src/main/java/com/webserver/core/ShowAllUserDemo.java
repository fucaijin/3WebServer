package com.webserver.core;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * ��user.dat�ļ��е�ÿ���û���Ϣ��ȡ���������������̨
 */
public class ShowAllUserDemo {

	public static void main(String[] args) throws IOException {
		RandomAccessFile raf = new RandomAccessFile("user.dat","r");
		
		String str;
		for (int i = 0; i < raf.length()/100; i++) {
			byte[] b = new byte[32];
			raf.read(b);
			str = new String(b,"utf-8");
			System.out.println("�û�����" + str);
			
			raf.read(b);
			str = new String(b,"utf-8");
			System.out.println("���룺" + str);
			
			raf.read(b);
			str = new String(b,"utf-8");
			System.out.println("�ǳƣ�" + str);
			
			System.out.println("���䣺" + raf.readInt());
		}
		
		raf.close();
		
	}

}
