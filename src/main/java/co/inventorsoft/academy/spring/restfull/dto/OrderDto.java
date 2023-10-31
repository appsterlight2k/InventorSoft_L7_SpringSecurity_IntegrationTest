package co.inventorsoft.academy.spring.restfull.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long itemId;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String phone;
    private Boolean isPaid;
}
