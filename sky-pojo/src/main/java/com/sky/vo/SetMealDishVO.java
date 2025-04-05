package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetMealDishVO implements Serializable {
    //菜品的名称
    private String name;
    //菜品的图片路径
    private String image;

    //菜品的描述
    private String description;

    //菜品的数量
    private Integer copies;
}
