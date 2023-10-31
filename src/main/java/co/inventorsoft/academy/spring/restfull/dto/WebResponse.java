package co.inventorsoft.academy.spring.restfull.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class WebResponse<T> {
    private T data;
    private String message;
    private boolean success;
    private Integer count;
}
