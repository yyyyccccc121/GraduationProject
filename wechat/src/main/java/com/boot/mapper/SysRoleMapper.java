package com.boot.mapper;

import com.boot.pojo.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysRoleMapper {

    /**
     * 根据角色ID查找角色
     *
     * @param id 角色ID
     * @return 角色
     */
    @Select("select * from sys_role where id = #{id}")
    SysRole findById(Long id);
}


