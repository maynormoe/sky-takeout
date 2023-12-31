package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Maynormoe
 */
@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username
     * @return
     */
    @Select("select " +
            "id, name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user" +
            " from employee " +
            "where username = #{username}")
    Employee getByUsername(String username);

    /**
     * @param employee 员工数据
     */
    @Insert("insert into employee (name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "VALUES " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{status},#{createTime},#{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(Employee employee);


    /**
     * 分页查询
     *
     * @param employeePageQueryDTO 员工分页数据传输对象
     * @return Page<Employee>
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据主键修改员工数据
     *
     * @param employee 员工
     */
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);

    Employee selectById(Long id);
}
