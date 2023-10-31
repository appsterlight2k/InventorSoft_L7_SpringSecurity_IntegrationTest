package co.inventorsoft.academy.spring.restfull.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ItemDto {
    private Long id;

    @NotBlank
    @Size(min = 5, max = 2000)
    private String name;

    @NotNull
    @Min(0)
    private Integer count;

    @NotNull
    @Min(0)
    private Double price;

    @NotNull
    private String articleNum;

    private String description;

}
