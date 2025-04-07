package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Override
    public void addAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.addAddress(addressBook);
    }

    @Override
    public List<AddressBook> list() {
        return addressBookMapper.list();
    }

    @Override
    public AddressBook getDefault() {
        return addressBookMapper.getDefault();
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    @Override
    public void setDefault(Long id) {
        //由于默认地址只能有一个，因此在设置一个默认地址之前要将之前的默认地址进行修改
        //先将所有地址修改为非默认地址
        addressBookMapper.setAllNot();

        //然后将指定id的default值修改为1
        addressBookMapper.setDefault(id);


    }

    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }
}
