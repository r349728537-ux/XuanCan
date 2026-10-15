package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);
    //插入员工数据
    @Insert("insert into employee (username, password, name, sex,  phone, status, create_time, update_time, create_user, update_user, id_number) " +
            "values (#{username}, #{password}, #{name}, #{sex},  #{phone}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{idNumber})")
    void insert(Employee employee);
}
