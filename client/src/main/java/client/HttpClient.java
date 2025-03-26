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

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";

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
            //return if status is 200. Else throw error
            if (statusCode==200) {
                var message = "{}";
                try (InputStream respBody = http.getInputStream()) {
                    message = readStream(respBody);
                }
                return message;
            } else {
                var messageJson = "";
                try (InputStream respBody = http.getErrorStream()) {
                    messageJson = readStream(respBody);
                }
                String message = messageJson.split("\"")[3];
                throw new HttpException(statusCode, message);
            }

        } catch (Exception e) {
            if(e instanceof HttpException httpException){
                throw httpException;
            } else {
                throw new HttpException(-1, e.getMessage());
            }
        }
    }

    private static String readStream(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
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
