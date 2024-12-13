package com.lin.linfriends;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.lin.linfriends.mapper")
@EnableScheduling
public class LinFriendsApplication {

    public static void main(String[] args) {

        SpringApplication.run(LinFriendsApplication.class, args);
    }

}
