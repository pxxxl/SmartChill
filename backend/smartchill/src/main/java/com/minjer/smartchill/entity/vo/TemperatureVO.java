package com.minjer.smartchill.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemperatureVO {
    @JsonProperty("temperature")
    private String temperature;
    @JsonProperty("id")
    private String id;
}
