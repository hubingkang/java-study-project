package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Users;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<Users> {

}
