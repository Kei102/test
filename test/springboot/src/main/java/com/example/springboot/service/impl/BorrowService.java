package com.example.springboot.service.impl;

import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.entity.Book;
import com.example.springboot.entity.Borrow;
import com.example.springboot.entity.Retur;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.BookMapper;
import com.example.springboot.mapper.BorrowMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IBorrowService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BorrowService implements IBorrowService {

    @Resource
    BorrowMapper borrowMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    BookMapper bookMapper;

    @Override
    public List<Borrow> list() {
        return borrowMapper.list();
    }

    @Override
    public PageInfo<Borrow> page(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(),baseRequest.getPageSize());
        List<Borrow> borrows = borrowMapper.listByCondition(baseRequest);
        for (Borrow borrow : borrows) {
            LocalDate returnDate = borrow.getReturnDate();
            LocalDate now = LocalDate.now();
            if (now.plusDays(1).isEqual(returnDate)) { //当前日期比归还日期小一天
                borrow.setNote("即将到期");
            } else if (now.isEqual(returnDate)) {
                borrow.setNote("已到期");
            } else if (now.isAfter(returnDate)) {
                borrow.setNote("已过期");
            }else {
                borrow.setNote("正常");
            }
        }
        return new PageInfo<>(borrows);
    }

    @Override
    public PageInfo<Retur> pageRetur(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(),baseRequest.getPageSize());
        List<Retur> returs = borrowMapper.listReturByCondition(baseRequest);
        return new PageInfo<>(returs);
    }

    @Override
    @Transactional
    public void save(Borrow obj) {
//        1.校验用户积分是否足够；
        String userNo = obj.getUserNo();
        User user = userMapper.getByUsername(userNo);
        if (Objects.isNull(user)) {
            throw new ServiceException("用户不存在");
        }
//        2.校验图书的数量是否足够；
        Book book = bookMapper.getByNo(obj.getBookNo());
        if (Objects.isNull(book)) {
            throw new ServiceException("所借图书不存在");
        }

//        3.校验图书的数量
        if (book.getNums() < 1) {
            throw new ServiceException("图书数量不足");
        }

        Integer account = user.getAccount();
        Integer score = book.getScore() * obj.getDays(); // score = 借一本 * 天数
//        4.校验用户账户余额
        if (score > account) {
            throw new ServiceException("用户积分不足，请充值！");
        }
//        5.更新账户的余额
        user.setAccount(user.getAccount() - score);
        userMapper.updateById(user);

//        6.更新图书的数量
        book.setNums(book.getNums() - 1);
        bookMapper.updateById(book);
        obj.setReturnDate(LocalDate.now().plus(obj.getDays(), ChronoUnit.DAYS)); //当前的日期加days 得到归还的日期
        obj.setScore(score);
//        7.新增借书记录
        borrowMapper.save(obj);
    }


//    还书
    @Transactional
    @Override
    public void saveRetur(Retur obj) {
        // 改状态
        obj.setStatus("已归还");
        borrowMapper.updateStatus("已归还", obj.getId()); //通过用户借书的id去归还图书，前端传来的借书id

        // 图书数量增加
//        obj.setId(null);  //新数据
        obj.setRealDate(LocalDate.now());
        borrowMapper.saveRetur(obj);
        bookMapper.updateNumByNo(obj.getBookNo());

        //返回用户积分
        Book book = bookMapper.getByNo(obj.getBookNo());
        if (book != null) {
            long until = 0;
            if (obj.getRealDate().isBefore(obj.getReturnDate())) {
                until = obj.getRealDate().until(obj.getReturnDate(), ChronoUnit.DAYS);
            } else if (obj.getRealDate().isAfter(obj.getReturnDate())) {  //逾期归还  需要扣除额外的积分
                until = -obj.getReturnDate().until(obj.getRealDate(), ChronoUnit.DAYS);
            }
            int score = (int) until * book.getScore();   //获取带归还的积分

            User user = userMapper.getByUsername(obj.getUserNo());
            int account = user.getAccount() + score;
            user.setAccount(account);
            if (account < 0) {
                    //锁定账号
                    user.setStatus(false);
            }
            userMapper.updateById(user);
        }
    }

    @Override
    public Borrow getById(Integer id) {
        return borrowMapper.getById(id);
    }

    @Override
    public void update(Borrow obj) {
        obj.setUpdatetime(LocalDate.now());
        borrowMapper.updateById(obj);
    }

    @Override
    public void deleteById(Integer id) {
        borrowMapper.deleteById(id);
    }

    @Override
    public void deleteReturById(Integer id) {
        borrowMapper.deleteReturById(id);
    }

}
