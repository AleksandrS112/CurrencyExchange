package exception;

public class RespException extends RuntimeException {

    private final int code;
    private final String message;

    public RespException(Throwable throwable, int code, String message) {
        super(throwable);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
