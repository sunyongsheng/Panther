package top.aengus.panther.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/6/8
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
