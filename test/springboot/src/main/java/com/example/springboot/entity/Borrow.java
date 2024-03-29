package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class Borrow {
    /**
     * id
     */
    private Integer id;

    /**
     * 图书名称
     */
    private String bookName;

    /**
     * 图书标准码
     */
    private String bookNo;

    /**
     * 用户id
     */
    private String userNo;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户的联系方式
     */
    private String userPhone;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate createtime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate updatetime;

    /**
     * 借书积分
     */
    private Integer score;
    /**
     *图书归还状态
     */
    private String status;
    /**
     * 借书天数
     */
    private Integer days;

    /**
     * 归还日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate returnDate;

//    提醒状态 即将到期（-1）  已到期（当天）  已过期（超过归还日期之后）
    private String note;
}
