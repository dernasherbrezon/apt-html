package ru.r2cloud.apt.html.model;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import ru.r2cloud.apt.html.Main;

public class MainTest {

	private HttpServer server;
	private String host;
	private int port;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void testSuccess() throws Exception {
		String args = "--url http://" + host + ":" + port + " --include-arch armhf,amd64 --include-component main --include-codename stretch,bionic --include-package rtl-sdr,plutosdr,r2cloud-jdk,r2cloud --output-dir " + tempFolder.getRoot().getAbsolutePath();
		Main.main(args.split(" "));
		try (FileInputStream actual = new FileInputStream(new File(tempFolder.getRoot(), "index.html")); FileInputStream expected = new FileInputStream(new File("src/test/resources/index.html"))) {
			assertEquals(convert(expected), convert(actual));
		}
	}

	@Before
	public void start() throws Exception {
		host = "localhost";
		port = 8000;
		server = HttpServer.create(new InetSocketAddress(host, port), 0);
		server.createContext("/", new HttpHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				System.out.println("Requested: " + exchange.getRequestURI());
				File source = new File("./src/test/resources" + exchange.getRequestURI());
				if (!source.exists()) {
					exchange.sendResponseHeaders(404, 0);
					exchange.close();
					return;
				}
				exchange.sendResponseHeaders(200, source.length());
				try (FileInputStream fis = new FileInputStream(source); OutputStream os = exchange.getResponseBody()) {
					copy(fis, os);
				}
				exchange.close();
			}
		});
		server.start();
	}

	public static void copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024 * 4];
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
	}

	public static String convert(InputStream is) {
		try (StringWriter sw = new StringWriter()) {
			copy(new InputStreamReader(is, StandardCharsets.UTF_8), sw);
			sw.flush();
			return sw.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void copy(Reader input, Writer output) throws IOException {
		char[] buffer = new char[1024 * 4];
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
	}

	@After
	public void stop() {
		if (server != null) {
			server.stop(0);
		}
	}

}
