package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@RequestMapping("/user/addressBook")
@Api(tags = "地址簿")
public class AddressController {

    @Autowired
    private AddressBookService addressBookService;
    @PostMapping
    @ApiOperation(value = "新增地址")
    public Result<String> addAddress(@RequestBody AddressBook addressBook){
        log.info("前端传回的地址"+addressBook);
        addressBookService.addAddress(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询所有地址信息")
    public Result<List<AddressBook>> list(){
        List<AddressBook> list=addressBookService.list();
        return Result.success(list);

    }

    @GetMapping("/default")
    @ApiOperation(value = "查询默认地址")
    public Result<AddressBook> getDefault(){
        AddressBook addressBook=addressBookService.getDefault();
        return Result.success(addressBook);
    }

    @PutMapping
    @ApiOperation(value = "根据指定id修改地址")
    public Result<String> update(@RequestBody AddressBook addressBook){
        log.info("需要修改地址的数据"+addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id){
        log.info("需要查询的id的值为："+id);
        AddressBook addressBook=addressBookService.getById(id);
        return Result.success(addressBook);
    }

    @PutMapping("/default")
    @ApiOperation(value = "设置默认地址")
    public Result<String> setDefault(@RequestBody Map<String,Long> Map){
        Long id=Map.get("id");
        log.info("需要设置默认地址的id："+id);
        addressBookService.setDefault(id);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation(value = "根据id删除地址")
    public Result<String> deleteById(Long id){
        log.info("需要删除的地址id为："+id);
        addressBookService.deleteById(id);
        return Result.success();

    }
}
