package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    void addAddress(AddressBook addressBook);

    @Select("select * from address_book")
    List<AddressBook> list();

    @Select("select * from address_book where is_default=1")
    AddressBook getDefault();

    void update(AddressBook addressBook);

    @Select("select * from address_book where id=#{id}")
    AddressBook getById(Long id);

    @Update("update address_book set is_default=0")
    void setAllNot();

    @Update("update address_book set is_default=1 where id=#{id}")
    void setDefault(Long id);

    @Delete("delete from address_book where id=#{id}")
    void deleteById(Long id);
}
