package com.minjer.smartchill.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrinkOnSale implements Serializable {
    @JsonProperty("name")
    private String name;
    @JsonProperty("price")
    private BigDecimal price;
    @JsonProperty("fridge")
    private Integer fridge;
    @JsonProperty("position")
    private Integer position;
    @JsonProperty("count")
    private Integer count;
    @JsonProperty("temperature")
    private BigDecimal temperature;
    @JsonProperty("image")
    private String image;

    @JsonIgnore
    private LocalDateTime createTime;
    @JsonIgnore
    private BigDecimal createTemperature;
    @JsonIgnore
    private Integer drinkId;
}
