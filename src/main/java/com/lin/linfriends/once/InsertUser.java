package com.lin.linfriends.once;

import com.lin.linfriends.mapper.UserMapper;
import com.lin.linfriends.model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUser {

    @Resource
    private UserMapper userMapper;

    //@Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE )
    public void doInsertUser() {
        //todo 添加多一个profile属性，用于用户的个人描述
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++){
            User user = new User();
            user.setUsername("假面");
            user.setUserAccount("Faker");
            user.setAvatarUrl("https://images.wallpaperscraft.com/image/single/girl_neon_glasses_1314591_1280x720.jpg");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("12312312312");
            user.setEmail("lin-sir@qq.com");
            user.setUserStatus(0);
            user.setTags("[]");
            user.setUserRole(0);
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }
}
