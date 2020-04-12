package com.boot.service;

import com.boot.mapper.SysUserRoleMapper;
import com.boot.pojo.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserRoleService {

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    public List<SysUserRole> listByUserId(Long userId) {
        return userRoleMapper.findByUserId(userId);
    }
}
