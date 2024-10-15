package com.minjer.smartchill.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrinkStatisticsVO {
    @JsonProperty("name")
    private String name;
    @JsonProperty("count")
    private Integer count;

}
