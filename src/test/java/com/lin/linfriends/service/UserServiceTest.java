package com.lin.linfriends.service;

import com.lin.linfriends.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
 * 用户服务处测试
 *
 * @author lin
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private  UserService userService;

    @Test
    public void testAddUser(){
        User user=new User();
        user.setUsername("manLin");
        user.setUserAccount("123");
        user.setAvatarUrl("G:/Picture/壁纸/0.jpg");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("111");
        user.setEmail("222");


        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);
    }

    @Test
    void userRegister() {
        //输入不能为空的测试
        String userAccount = "linzekai";
        String userPassword = "";
        String checkPassword = "123456";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);
        //账户长度不小于4位的测试
        userAccount= "lin";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);
        //密码长度不短于8位的测试
        userAccount="linzekai";
        userPassword="123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);
        //不能有特殊字符的测试
        userAccount="lin zekai";
        userPassword="12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);
        //账户不能重复的测试
        userAccount="123";
        checkPassword="12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);
        //都正确的情况下应该返回id>0
        userAccount="linzekai";

        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);

    }
    @Test
    void searchUsersByTags() {
        List<String> tagNameList= Arrays.asList("java","男");
        List<User> users = userService.searchUsersByTags(tagNameList);
        Assertions.assertNotNull(users);
    }
}