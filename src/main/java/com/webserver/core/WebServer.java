package com.webserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer主类
 */
public class WebServer {
	private ServerSocket server;
	// 线程池
	private ExecutorService threadPool;
	/**
	 * 构造方法,初始化使用
	 */
	public WebServer() {
		try {
			server = new ServerSocket(8088); // 监听8088端口
			threadPool = Executors.newFixedThreadPool(100);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 服务端开始工作的方法
	 */
	public void start() {
		try {
			/*
			 * 暂时不启用接受多次客户端连接的操作
			 */
			while (true) {
				System.out.println("等待客户端连接...");
				Socket socket = server.accept(); // 阻塞等待客户端连接,每有客户端连接,就生成一个socket
				System.out.println("一个客户端连接了!");// 使用浏览器访问url(统一资源定位) →  http://localhost:8088/ 或者  127.0.0.1:8088
				// 启动一个线程处理该客户端交互
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
