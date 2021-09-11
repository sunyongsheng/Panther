package top.aengus.panther.core;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Response<T> {

    public static final int CODE_SUCCESS = HttpStatus.OK.value();
    public static final int CODE_BAD_REQUEST = HttpStatus.BAD_REQUEST.value();
    public static final int CODE_NO_AUTH = HttpStatus.UNAUTHORIZED.value();
    public static final int CODE_NOT_FOUND = HttpStatus.NOT_FOUND.value();
    public static final int CODE_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();

    private Integer code;
    private String msg;
    private T data;

    public Response<T> success() {
        this.code = CODE_SUCCESS;
        return this;
    }

    public Response<T> badRequest() {
        this.code = CODE_BAD_REQUEST;
        return this;
    }

    public Response<T> noAuth() {
        this.code = CODE_NO_AUTH;
        return this;
    }

    public Response<T> notFound() {
        this.code = CODE_NOT_FOUND;
        return this;
    }

    public Response<T> unknownError() {
        this.code = CODE_ERROR;
        return this;
    }

    public Response<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Response<T> data(T data) {
        this.data = data;
        return this;
    }

}