package com.lin.linfriends.service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        //todo 尝试使用断言测试(Assertions)
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("lin","小帅哥");
        Object lin = valueOperations.get("lin");
        Assertions.assertEquals("小帅哥",lin);
        //报错,这里应该是依赖问题，导致一些命令使用不了
        //valueOperations.getAndExpire("lin",0, TimeUnit.HOURS);
    }
}
