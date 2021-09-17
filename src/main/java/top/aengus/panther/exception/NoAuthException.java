package top.aengus.panther.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoAuthException extends RuntimeException {

    public NoAuthException(String message) {
        super(message);
    }
}
