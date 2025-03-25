package client;

public class HttpException extends RuntimeException {
    final int code;
    public HttpException(String message, int code) {
        super(message);
        this.code = code;
    }
}
