package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

//@Data可以在我们编译后自动生成get、set这些模板代码，源代码更加简洁
@Data
public class User {
    private Integer id;
    private String name;
    private String username;
    private Integer age;
    private Integer account;
    private Integer score;
    private String sex;
    private String phone;
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date createtime;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date updatetime;
    private boolean status;

}
