package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO 员工数据传输对象
     * @return Employee
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO 员工数据传输对象
     */
    void save(EmployeeDTO employeeDTO);

    /**
     *  分页查询员工信息
      * @param employeePageQueryDTO 员工数据分页传输对象
     *  @return  PageResult<EmployeeDTO>
     */
    PageResult<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 更新用户信息
      * @param employee 用户
     */
    void update(EmployeeDTO employee);

    /**
     * 修改用户状态
     * @param status 状态
     * @param id id
     */
    void startOrStop(Integer status, Long id);


    /**
     * 根据id查询员工数据
      * @param id id
     * @return Employee
     */
    Employee getById(Long id);
}
