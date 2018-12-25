package com.webserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer����
 */
public class WebServer {
	private ServerSocket server;
	// �̳߳�
	private ExecutorService threadPool;
	/**
	 * ���췽��,��ʼ��ʹ��
	 */
	public WebServer() {
		try {
			server = new ServerSocket(8088); // ����8088�˿�
			threadPool = Executors.newFixedThreadPool(100);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����˿�ʼ�����ķ���
	 */
	public void start() {
		try {
			/*
			 * ��ʱ�����ý��ܶ�οͻ������ӵĲ���
			 */
			while (true) {
				System.out.println("�ȴ��ͻ�������...");
				Socket socket = server.accept(); // �����ȴ��ͻ�������,ÿ�пͻ�������,������һ��socket
				System.out.println("һ���ͻ���������!");// ʹ�����������url(ͳһ��Դ��λ) ��  http://localhost:8088/ ����  127.0.0.1:8088
				// ����һ���̴߳���ÿͻ��˽���
				ClientHandler handler = new ClientHandler(socket);
				threadPool.execute(handler);
//				Thread t = new Thread(handler);
//				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}

}
