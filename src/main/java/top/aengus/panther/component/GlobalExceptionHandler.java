package top.aengus.panther.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.aengus.panther.core.Response;
import top.aengus.panther.exception.BadRequestException;
import top.aengus.panther.exception.InternalException;
import top.aengus.panther.exception.NoAuthException;
import top.aengus.panther.exception.NotFoundException;


/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/6/8
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public Response<String> badRequestException(BadRequestException exception) {
        log.error("[badRequestException]", exception);
        return new Response<String>().badRequest().msg(exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public Response<Object> notFoundException(NotFoundException exception) {
        log.error("[notFoundException] key=" + exception.getKey().toString(), exception);
        return new Response<>().notFound().msg(exception.getMessage()).data(exception.getKey());
    }

    @ExceptionHandler(InternalException.class)
    public Response<Void> internalException(InternalException exception) {
        log.error("[internalException] " + exception.getMessage(), exception);
        return new Response<Void>().unknownError().msg(exception.getMessage());
    }

    @ExceptionHandler(NoAuthException.class)
    public Response<Object> noAuthException(NoAuthException exception) {
        log.error("[noAuthException] " + exception.getMessage(), exception);
        return new Response<>().noAuth().msg(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("参数校验失败", e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMsg;
        if (fieldError != null) {
            errorMsg = fieldError.getDefaultMessage();
        } else {
            errorMsg = "参数不合法！";
        }
        return new Response<>().badRequest().msg(errorMsg);
    }
}
