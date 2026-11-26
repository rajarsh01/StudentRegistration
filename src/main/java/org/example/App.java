
package org.example;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class App {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress("0.0.0.0", 8082), 0);

        server.createContext("/", exchange -> {

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                String body = new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8);

                String decoded = URLDecoder.decode(
                        body, StandardCharsets.UTF_8);

                String response = "<h2>Registration Submitted!</h2>"
                        + "<p>" + decoded.replace("&", " | ") + "</p>"
                        + "<a href='/'>Go Back</a>";

                exchange.getResponseHeaders().set(
                        "Content-Type", "text/html; charset=UTF-8");

                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);
                exchange.getResponseBody().write(responseBytes);
                exchange.close();
                return;
            }

            InputStream in = App.class.getResourceAsStream("/index.html");

            if (in == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            byte[] data = in.readAllBytes();

            exchange.getResponseHeaders().set(
                    "Content-Type", "text/html; charset=UTF-8");

            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            exchange.close();
        });

        server.start();
        System.out.println("Server running at http://localhost:8082");
    }
}
