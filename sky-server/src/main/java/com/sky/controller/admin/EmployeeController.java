package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.entity.PageBean;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation(value = "员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation(value = "员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

//不知道为什么，前端并没有将empID传到后端来
    @ApiOperation(value = "修改密码")
    @PutMapping("/editPassword")
    public Result<String> editPassword(@RequestBody Map<Object,Object> map){
        Long empId= BaseContext.getCurrentId();
        String newPassword= (String) map.get("newPassword");
        String oldPassword=(String) map.get("oldPassword");
        System.out.println(empId+newPassword+oldPassword);
        employeeService.editPassword(empId,newPassword,oldPassword);
        return Result.success();
    }

    @ApiOperation(value = "新增员工")
    @PostMapping
    public Result<String> addEmpl(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增的员工：{}",employeeDTO);
//        System.out.println("当前线程的ID："+Thread.currentThread().getId());
        employeeService.addEmpl(employeeDTO);
        return Result.success();
    }

    @ApiOperation(value = "更改状态")
    @PostMapping("/status/{status}")
    public Result<String> editStatus(@PathVariable Integer status,Long id){
        employeeService.editStatus(status,id);
        return Result.success();
    }


    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public Result<PageBean> pageSearch(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询，参数："+employeePageQueryDTO);
        PageBean pageBean =employeeService.querySearch(employeePageQueryDTO);
        return Result.success(pageBean);
    }

    @ApiOperation(value = "修改员工信息")
    @PutMapping
    public Result<String> UpdateEml(@RequestBody EmployeeDTO employeeDTO){
        log.info("传回的员工修改数据："+employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("获取的id值为："+id);
        Employee employee=employeeService.getById(id);
        return Result.success(employee);
    }
}
