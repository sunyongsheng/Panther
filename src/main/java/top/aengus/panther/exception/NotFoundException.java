package top.aengus.panther.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/9/9
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NotFoundException extends RuntimeException {

    private final Object key;

    public NotFoundException(String message, Object source) {
        super(message);
        this.key = source;
    }

}
