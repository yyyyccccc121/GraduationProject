package com.boot.service;

import com.boot.mapper.SysRoleMapper;
import com.boot.pojo.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    public SysRole selectById(Long id){
        return roleMapper.findById(id);
    }
}
