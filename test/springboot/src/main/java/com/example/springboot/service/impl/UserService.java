package com.example.springboot.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.IdUtil;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.controller.request.UserPageRequest;
import com.example.springboot.entity.User;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.IUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

@Service
public class UserService implements IUserService {

    @Autowired
    UserMapper userMapper;
    @Override
    public List<User> list() {
        return userMapper.list();
    }

    @Override
    public PageInfo<User> page(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(),baseRequest.getPageSize());
        List<User> users = userMapper.listByCondition(baseRequest);
        return new PageInfo<>(users);
    }

    @Override
    public void save(User user) {
//        当作卡号来处理,开卡时间加上唯一的ID号
        Date date = new Date();
        user.setUsername(DateUtil.format(date,"yyyyMMdd") + Math.abs(IdUtil.fastSimpleUUID().hashCode()));
        userMapper.save(user);
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }

    @Override
    public void update(User user) {
        user.setUpdatetime(new Date());
        userMapper.updateById(user);
    }

    @Override
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }
}
