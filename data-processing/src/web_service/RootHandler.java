package web_service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler implements HttpHandler {
	@Override

	public void handle(HttpExchange he) throws IOException {
		Headers responseHeaders = he.getResponseHeaders();
		responseHeaders.set("Content-Type", "text/html");

		File file = new File("front-end/index.html");
		he.sendResponseHeaders(200, 0);
		OutputStream os = he.getResponseBody();
		FileInputStream fs = new FileInputStream(file);
		final byte[] buffer = new byte[0x10000];
		int count = 0;
		while ((count = fs.read(buffer)) >= 0) {
			os.write(buffer,0,count);
		}
		fs.close();
		os.close();
	}

}
