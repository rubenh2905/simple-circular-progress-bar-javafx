package com.mrsky;

import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WebRequest {
    public static synchronized String request() {
        try {
            // https://hypixel-api.inventivetalent.org/api/skyblock/darkauction/estimate
            final Socket socket = SSLSocketFactory.getDefault().createSocket();
            socket.setSoTimeout(2000);
            socket.connect(new InetSocketAddress("hypixel-api.inventivetalent.org", 443), 2000);
            final InputStream inputStream = socket.getInputStream();
            final OutputStream outputStream = socket.getOutputStream();
            outputStream.write(("GET /api/skyblock/darkauction/estimate HTTP/1.1\nHOST: hypixel-api.inventivetalent.org\n\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            final byte[] singleBuffer = new byte[1];
            int length;
            String lastChar = "";
            String current;
            final StringBuilder builder = new StringBuilder();
            while ((length = inputStream.read(singleBuffer)) > 0) {
                current = new String(singleBuffer, 0, length);
                if (current.equals("\r")) {
                    continue;
                }
                builder.append(current);
                if (lastChar.equals("\n") && current.equals("\n")) {
                    break;
                }
                lastChar = current;
            }
            int content_length = 0;
            for (String s : builder.toString().split("\n")) {
                s = s.trim().toLowerCase();
                if (s.startsWith("content-length:")) {
                    content_length = Integer.parseInt(s.split("content-length:")[1].trim());
                    break;
                }
            }
            if (content_length == 0) {
                System.out.println("Can't get content_length");
                return null;
            }
            final byte[] data = new byte[content_length];
            final byte[] buffer = new byte[10];
            int all = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                System.arraycopy(buffer, 0, data, all, length);
                all += length;
                if (content_length <= all) {
                    break;
                }
            }
            try {
                inputStream.close();
            } catch (Exception ignored) {
            }
            try {
                outputStream.close();
            } catch (Exception ignored) {
            }
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            return new String(data, 0, data.length, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return null;
    }
}
