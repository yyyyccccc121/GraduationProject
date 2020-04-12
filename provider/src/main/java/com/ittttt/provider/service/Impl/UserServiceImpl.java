package com.ittttt.provider.service.Impl;

import com.ittttt.provider.mapper.SysUserMapper;
import com.ittttt.provider.pojo.SysUser;
import com.ittttt.provider.pojo.recommendHead;
import com.ittttt.provider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    public boolean findByaccountnumber(String accountnumber) {
        SysUser sysUser = sysUserMapper.findByaccountnumber(accountnumber);
        if(sysUser==null){
            return true;
        }
        return false;
    }

    @Override
    public void adduser(String accountnumber, String password,String nickName,String headPortrait) {
        sysUserMapper.adduser(accountnumber,password,nickName,headPortrait);
    }

    @Override
    public String findUrlByname(String accountnumber) {
        return sysUserMapper.findUrlByname(accountnumber);
    }

    @Override
    public SysUser findAllByName(String accountnumber) {
        return sysUserMapper.findByaccountnumber(accountnumber);
    }

    @Override
    public void updateUser(String nickName, String sex, String region, String signature,String accountNumber) {
        sysUserMapper.updateUser(nickName,sex,region,signature,accountNumber);
    }

    @Override
    public List<recommendHead> findAllHead() {
        return sysUserMapper.findAllHead();
    }

    @Override
    public void updateHead(String finalUrl, String accountNumber) {
        sysUserMapper.updateHead(finalUrl,accountNumber);
    }

    @Override
    public List<SysUser> findAllUser() {
        return sysUserMapper.findAllUser();
    }
}
