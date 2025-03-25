package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpClient {

    private final String rootUri;

    public static String GET = "GET";
    public static String POST = "POST";
    public static String DELETE = "DELETE";
    public static String PUT = "PUT";

    public HttpClient(String rootUri) {
        this.rootUri = rootUri;
    }

    public String sendHttpRequest(String path, String method, String authToken, String body) throws HttpException {
        try {
            //init http connection
            URI uri = new URI(rootUri + path);
            HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
            http.setDoInput(true);
            http.setDoOutput(true);

            http.setRequestMethod(method);

            //add authorization
            if(authToken!=null && !authToken.isEmpty()) {
                http.setRequestProperty("authorization", authToken);
            }

            //write request body
            if (body!=null && !body.isEmpty()) {
                http.setDoOutput(true);
                try (var outputStream = http.getOutputStream()) {
                    outputStream.write(body.getBytes());
                }
            }

            //send request
            http.connect();

            //unpack response
            var statusCode = http.getResponseCode();
            var message = "{}";
            try (InputStream respBody = http.getInputStream()) {
                message = readStream(respBody);
            }

            //return if status is 200. Else throw error
            if (statusCode==200) {
                return message;
            } else {
                throw new HttpException(statusCode, message);
            }

        } catch (Exception e) {
            throw new HttpException(-1, e.getMessage());
        }
    }

    private static String readStream(InputStream stream) throws IOException {
        if (stream == null) return "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();
        }
    }
}
