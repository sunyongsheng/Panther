package top.aengus.panther.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InternalException extends RuntimeException {
    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
