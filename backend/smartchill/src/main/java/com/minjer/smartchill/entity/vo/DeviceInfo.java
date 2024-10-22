package com.minjer.smartchill.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceInfo implements Serializable {
    @JsonProperty("name")
    private String name;

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("position")
    private Integer position;

    @JsonProperty("belong")
    private String belong;
}
