package com.boot.service;

import com.boot.mapper.SysUserMapper;
import com.boot.pojo.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    public SysUser selectByName(String username) {
        return sysUserMapper.findByName(username);
    }
}
