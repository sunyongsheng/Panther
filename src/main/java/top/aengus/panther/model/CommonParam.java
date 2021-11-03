package top.aengus.panther.model;

import lombok.Data;

import java.util.List;

@Data
public class CommonParam<T> {

    private T key;

    private T value;

    private List<T> values;
}
