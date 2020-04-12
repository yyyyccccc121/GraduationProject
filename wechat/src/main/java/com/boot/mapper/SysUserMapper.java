package com.boot.mapper;

import com.boot.pojo.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper {

    /**
     * 根据用户名查找用户
     *
     * @param accountNumber 账号
     * @return 用户
     */
    @Select("select * from sys_user where accountNumber = #{accountNumber}")
    SysUser findByName(String accountNumber);
}

