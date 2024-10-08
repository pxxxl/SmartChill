package com.minjer.smartchill.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction implements Serializable {
    private Long id;
    private Integer drinkId;
    private Byte type;
    private Integer count;
    private Integer position;
    private LocalDateTime time;
}
