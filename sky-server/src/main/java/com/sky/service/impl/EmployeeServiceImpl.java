package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.entity.PageBean;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.properties.JwtProperties;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

//    @Autowired
//    private HttpServletRequest request;
//
//    @Autowired
//    private JwtProperties jwtProperties;


    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
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
        // 通过DigestUtils工具类，将前端传回的密码进行md5加密
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        //密码比对
        // 后期需要进行md5加密，然后再进行比对
        //需要对密码进行MD5加密
        //首先要将数据库中的密码改为密文密码
        //然后将前端传回的密码进行MD5加密之后再进行比对
        //注意在数据库进行修改的时候，不要敲回车键，不然会把回车键也添加到密码中去，并且当成\n来看待
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void editPassword(Long empId, String newPassword, String oldPassword) {
        //要将前端传回的旧密码和新密码都进行md5加密
        newPassword=DigestUtils.md5DigestAsHex(newPassword.getBytes());
        oldPassword=DigestUtils.md5DigestAsHex(oldPassword.getBytes());

        Employee employee=employeeMapper.getExsit(empId,oldPassword);
        if(null==employee){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if(employee!=null){
            employeeMapper.editPassword(newPassword,empId);
        }
    }

    @Override
    public void addEmpl(EmployeeDTO employeedto) {
        Employee employee=new Employee();
        BeanUtils.copyProperties(employeedto,employee);
        //设置账号的状态
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        employee.setStatus(StatusConstant.ENABLE);
        //将密码进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置当前记录创建人的id和修改id
        // TODO 后期需要修改为当前登录用户的id
//        String token = request.getHeader(jwtProperties.getAdminTokenName());
//        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
//        Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
//        Long empId=BaseContext.getCurrentId();
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

//        System.out.println("当前线程的ID："+Thread.currentThread().getId());
        employeeMapper.addEmpl(employee);

    }

    @Override
    public void editStatus(Integer status,Long empId) {
        Employee employee= Employee.builder()
                .status(status)
                .id(empId)
                .build();
        employeeMapper.editStatus(employee);
    }

    @Override
    public PageBean querySearch(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        List<Employee> empList=employeeMapper.list(employeePageQueryDTO);
        Page<Employee> p= (Page<Employee>) empList;
        PageBean pageBean=new PageBean(p.getTotal(), p.getResult());
        return pageBean;
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
//        employee.setUpdateUser(BaseContext.getCurrentId());
//        employee.setUpdateTime(LocalDateTime.now());
        employeeMapper.editStatus(employee);
    }

    @Override
    public Employee getById(Long id) {
        return employeeMapper.getById(id);
    }

}
