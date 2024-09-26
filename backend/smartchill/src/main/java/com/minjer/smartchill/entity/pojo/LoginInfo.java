package com.minjer.smartchill.entity.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginInfo {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;
}
