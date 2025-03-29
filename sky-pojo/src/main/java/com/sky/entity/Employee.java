package com.sky.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("员工信息的实体类")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "员工的id")
    private Long id;

    @ApiModelProperty(value = "员工的用户名")
    private String username;
    @ApiModelProperty(value = "员工的姓名")
    private String name;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value ="性别" )
    private String sex;
    @ApiModelProperty(value = "身份证号")
    private String idNumber;
    @ApiModelProperty(value = "状态 ")
    private Integer status;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;

}
