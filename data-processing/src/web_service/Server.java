package web_service;
import java.net.InetSocketAddress;

import processing.Preprocessing;

import com.sun.net.httpserver.HttpServer;

public class Server {
	public static void main(String[] args) throws Exception {
		int port = 9000;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		System.out.println("server started at " + port);
		server.createContext("/", new RootHandler());
		server.createContext("/compensate", new CompensateHandler());
		Preprocessing.init();
		server.start();
	}
}
