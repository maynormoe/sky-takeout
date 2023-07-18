package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对密码进行md5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus().equals(StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO 员工数据传输对象
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        // 设置账号的状态， 默认正常状态 1 正常 0 表示锁定
        employee.setStatus(StatusConstant.ENABLE);

        // 设置默认密码
        String md5Password = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());
        employee.setPassword(md5Password);

        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

    /**
     * 分页查询员工信息
     *
     * @param employeePageQueryDTO 员工数据分页传输对象
     * @return PageResult<EmployeeDTO>
     */
    @Override
    public PageResult<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // select * from employee limit 0,10
        // 开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> employeePage = employeeMapper.pageQuery(employeePageQueryDTO);

        long pageTotal = employeePage.getTotal();
        List<Employee> records = employeePage.getResult();

        // 员工信息脱敏
        records = records.stream().map(item -> {
            Employee employee = new Employee();
            BeanUtils.copyProperties(item, employee, "password");
            return employee;
        }).collect(Collectors.toList());


        return new PageResult<Employee>(pageTotal, records);
    }

    /**
     * 更新用户信息
     *
     * @param employee 用户
     */
    @Override
    public void update(EmployeeDTO employee) {
        Employee employeeDto = new Employee();
        BeanUtils.copyProperties(employee, employeeDto);
        employeeMapper.update(employeeDto);
    }

    /**
     * 修改用户状态
     *
     * @param status 状态
     * @param id     id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 获取员工实体构造器
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工数据
     *
     * @param id id
     * @return Employee
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        Employee employeeVo = new Employee();
        // 用户信息脱敏
        BeanUtils.copyProperties(employee, employeeVo,"password");
        return employeeVo;
    }
}
