package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderQueryDTO implements Serializable {

    private Integer page;
    private Integer pageSize;
    private Integer status;
}
