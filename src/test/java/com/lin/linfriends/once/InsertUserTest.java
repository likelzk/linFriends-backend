package com.lin.linfriends.once;

import com.lin.linfriends.mapper.UserMapper;
import com.lin.linfriends.model.domain.User;
import com.lin.linfriends.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;


//todo 打包要跳过这个测试
@SpringBootTest
class InsertUserTest {

    @Resource
    private UserService userService;
    //1.逐条插入userMapper.insert 1000条 3238ms
    //2.批量插入userService.savebach 1000条 928ms
    //  十万条 批量区间五万 11300ms
    //  十万条 批量区间一万 11690ms
    //  十万条 批量区间一千 12272ms
    //3.异步并发
    //  十万条 线程批量区间一万 4058ms

    @Test
    void doInsertUser() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
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
            userList.add(user);
        }
        userService.saveBatch(userList, 50000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    void doConcurrencyInsertUser() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        final int ONCE_INSERT = 10000;
        //异步任务数组
        ArrayList<CompletableFuture<Void>> futureList = new ArrayList<>();
        int j = 0;
        //以一万为区间创建异步任务并添加到任务数组中
        for (int i = 0; i < 10; i++) {
            List<User> userList = new ArrayList<>();
            while (j < ONCE_INSERT) {
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
                userList.add(user);
                j++;
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("ThreadName：" + Thread.currentThread().getName());
                userService.saveBatch(userList, ONCE_INSERT);
            });
            futureList.add(future);
            j = 0;
        }
        //todo 这里的语法我不是很懂
        //这里就是要执行异步任务了
        //join阻塞，知道所有任务执行完毕再执行下面的停止秒表的程序
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}