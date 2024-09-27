package com.minjer.smartchill.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Drink implements Serializable {
    private Integer id;
    private String name;
    private BigDecimal price;
    private String image;
}
