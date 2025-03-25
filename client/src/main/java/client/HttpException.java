package client;

public class HttpException extends RuntimeException {
    final int code;
    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }
}
