package com.demo.allframework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.allframework.entity.SysRole;

/**
 * @author YUDI
 * @date 2020/5/2 17:39
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    int insert(SysRole sysRole);


}
