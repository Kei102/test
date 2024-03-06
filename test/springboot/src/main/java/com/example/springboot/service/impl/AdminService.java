package com.example.springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.example.springboot.controller.dto.LoginDTO;
import com.example.springboot.controller.request.BaseRequest;
import com.example.springboot.controller.request.LoginRequest;
import com.example.springboot.controller.request.PasswordRequest;
import com.example.springboot.entity.Admin;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.AdminMapper;
import com.example.springboot.service.IAdminService;
import com.example.springboot.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class AdminService implements IAdminService {

    @Autowired
    AdminMapper adminMapper;
    private static final String DEFULT_PASS = "123456";
    private static final String PASS_SALT = "NJ";

    @Override
    public List<Admin> list() {
        return adminMapper.list();
    }

    @Override
    public PageInfo<Admin> page(BaseRequest baseRequest) {
        PageHelper.startPage(baseRequest.getPageNum(),baseRequest.getPageSize());
        List<Admin> admins = adminMapper.listByCondition(baseRequest);
        return new PageInfo<>(admins);
    }

    @Override
    public void save(Admin obj) {
//        默认密码123456
        if (StrUtil.isBlank(obj.getPassword())) {
            obj.setPassword(DEFULT_PASS);
        }
//        加密
        obj.setPassword(securePass(obj.getPassword()));
        try {
            adminMapper.save(obj);
        } catch (DuplicateKeyException e) {
            log.error("数据插入失败 , username:{}",obj.getUsername(), e);
            throw new ServiceException("用户名重复");
        }
    }

    @Override
    public Admin getById(Integer id) {
        return adminMapper.getById(id);
    }

    @Override
    public void update(Admin admin) {
        admin.setUpdatetime(new Date());
        adminMapper.updateById(admin);
    }

    @Override
    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    @Override
    public LoginDTO login(LoginRequest request) {
        Admin admin = null;
        try{
            admin = adminMapper.getByUsername(request.getUsername());
        } catch (Exception e) {
            log.error("根据用户名{} 查询出错", request.getUsername());
            throw new ServiceException("用户名错误");
        }

        if (admin == null){
            throw new ServiceException("用户名或密码错误");
        }
        String securePass = securePass(request.getPassword());
        if (!securePass.equals(admin.getPassword())) {
            throw new ServiceException("用户名或者密码错误");
        }
        if (!admin.isStatus()) {
            throw new ServiceException("当前用户处于禁用状态，请联系管理员");
        }
        LoginDTO loginDTO = new LoginDTO();
//        将admin属性复制到loginDTO中，只会复制名字相同的属性，不相同的不复制
        BeanUtils.copyProperties(admin,loginDTO);

//        生成Token
        String token = TokenUtils.genToken(String.valueOf(admin.getId()),admin.getPassword());

        loginDTO.setToken(token);
        return loginDTO;
    }

    @Override
    public void changePass(PasswordRequest request) {
        //        注意，要对新的密码进行加密
        request.setNewPass(securePass(request.getNewPass()));
        int count = adminMapper.updatePassword(request);
        if (count <= 0) {
            throw new ServiceException("修改失败");
        }
    }
    private String securePass(String password) {
        return SecureUtil.md5(password + PASS_SALT);
    }
}
