package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    void addAddress(AddressBook addressBook);

    List<AddressBook> list();

    AddressBook getDefault();

    void update(AddressBook addressBook);

    AddressBook getById(Long id);

    void setDefault(Long id);

    void deleteById(Long id);
}
