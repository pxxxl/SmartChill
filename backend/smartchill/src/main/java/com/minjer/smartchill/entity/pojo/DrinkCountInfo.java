package com.minjer.smartchill.entity.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrinkCountInfo implements Serializable {
    @JsonProperty("id")
    private Integer drinkId;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("fridge")
    private Integer fridge;

    @JsonProperty("position")
    private Integer position;
}
