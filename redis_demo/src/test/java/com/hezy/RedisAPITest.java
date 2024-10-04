package com.hezy;

import cn.hutool.json.JSONUtil;
import com.hezy.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 10765
 * @create 2024/10/4 16:30
 */
@SpringBootTest
public class RedisAPITest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void stringAPI() {
        // 1.添加一个数据
        redisTemplate.opsForValue().set("username", "zhangsan");

        // 2.添加一个有时效性的数据
        redisTemplate.opsForValue().set("password", "123456", 1, TimeUnit.MINUTES);

        // 3.对一个数据进行加减操作
        redisTemplate.opsForValue().set("age", String.valueOf(20));
        redisTemplate.opsForValue().increment("age", 10);
        redisTemplate.opsForValue().decrement("age", 5);

        // 4.设置一对值，如果存在就返回true并设置成功，如果不存在就返回false并设置失败
        Boolean flag = redisTemplate.opsForValue().setIfPresent("username", "zhangsan_fix");
        System.out.println("flag = " + flag);

        // 5.如果key存在就存储失败，不存在则存储成功
        Boolean flagAge = redisTemplate.opsForValue().setIfAbsent("age", "100");
        System.out.println("flagAge = " + flagAge);
        Boolean flagNewAge = redisTemplate.opsForValue().setIfAbsent("NewAge", "200");
        System.out.println("flagNewAge = " + flagNewAge);

        // 6.存入一个自定义对象
        User user = User.builder().id(1).username("zhangsan").password("123456").build();
        redisTemplate.opsForValue().set("user", JSONUtil.toJsonStr(user));
    }

    @Test
    public void hashAPI() {
        // 1.添加一个数据
        redisTemplate.opsForHash().put("user1", "username", "zhangsan");

        // 2.添加多个数据
        HashMap<String, String> map = new HashMap<>();
        map.put("username", "zhangsan");
        map.put("password", "123456");
        map.put("age", "20");
        redisTemplate.opsForHash().putAll("user2", map);

        // 3.获取指定Map中的指定key的值
        Object username = redisTemplate.opsForHash().get("user2", "username");
        System.out.println(username);

        // 4.获取所有Key列表
        Set<Object> keys = redisTemplate.opsForHash().keys("user2");
        System.out.println("keys = " + keys);

        // 5.获取所有Value列表
        List<Object> values = redisTemplate.opsForHash().values("user2");
        System.out.println("values = " + values);

        // 6.获取所有键值对
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("user2");
        System.out.println("entries = " + entries);

        // 7.给Hash中的数值型字符进行自增/自减
        Long increment = redisTemplate.opsForHash().increment("user2", "age", 10);
        System.out.println("increment = " + increment);

        // 8.删除数据
        redisTemplate.opsForHash().delete("user2", "age");

        // 9.判断某个键是否存在
        Boolean flag = redisTemplate.opsForHash().hasKey("user2", "age");
        System.out.println("flag = " + flag);

        // 10.获取长度
        Long size = redisTemplate.opsForHash().size("user2");
        System.out.println("size = " + size);
    }

    @Test
    public void listAPI() {
        // 1.添加数据
        // 第一种方式：直接添加元素
        redisTemplate.opsForList().leftPush("list", "b");

        // 第二种方式：添加多个元素
        redisTemplate.opsForList().leftPushAll("list", "b", "c");

        // 第三种方式：添加一个List
        List<String> list = new ArrayList<>();
        list.add("d");
        list.add("e");
        redisTemplate.opsForList().leftPushAll("list", list);

        // 2.指定位置插入元素
        redisTemplate.opsForList().set("list", 2, "a");

        // 3.查询元素个数
        Long size = redisTemplate.opsForList().size("list");
        System.out.println("size = " + size);

        // 4.范围查询元素列表
        List<String> range = redisTemplate.opsForList().range("list", 0, 3);
        System.out.println("range = " + range);

        // 5.弹出左边一个元素
        String leftPop = redisTemplate.opsForList().leftPop("list");
        System.out.println("leftPop = " + leftPop);

        // 6.删除元素
        // count: 0表示删除全部，正数表示从左边开始删除，负数表示从右边开始删除
        redisTemplate.opsForList().remove("list", 1, "b");
    }

    @Test
    public void setAPI() {
        // 1.添加元素
        Long add = redisTemplate.opsForSet().add("set", "a", "b", "c");
        System.out.println("add = " + add);

        // 2.判断元素是否在
        Boolean member = redisTemplate.opsForSet().isMember("set", "a");
        System.out.println("member = " + member);

        // 3.获取set集合
        Set<String> set = redisTemplate.opsForSet().members("set");
        System.out.println("set = " + set);

        // 4.计算两个set的交集
        redisTemplate.opsForSet().add("set2", "c", "d", "e");
        Set<String> intersection = redisTemplate.opsForSet().intersect("set", "set2");
        System.out.println("intersection = " + intersection);

        // 5.计算两个set的并集
        Set<String> union = redisTemplate.opsForSet().union("set", "set2");
        System.out.println("union = " + union);

        // 6.删除元素
        redisTemplate.opsForSet().remove("set", "c");
    }

    @Test
    public void zsetAPI() {
        // 1.添加元素
        redisTemplate.opsForZSet().add("zset", "a", 1);
        redisTemplate.opsForZSet().add("zset", "b", 2);
        redisTemplate.opsForZSet().add("zset", "c", 3);
        redisTemplate.opsForZSet().add("zset", "d", 4);
        redisTemplate.opsForZSet().add("zset", "e", 5);
        redisTemplate.opsForZSet().add("zset", "f", 6);

        // 2.查询元素
        Set<String> range = redisTemplate.opsForZSet().range("zset", 0, -1);
        System.out.println("range = " + range);

        // 3.计算元素个数
        Long size = redisTemplate.opsForZSet().zCard("zset");
        System.out.println("size = " + size);

        // 4.删除元素
        Long remove = redisTemplate.opsForZSet().remove("zset", "b");
        System.out.println("remove = " + remove);

        // 5.查询元素的score值
        Double score = redisTemplate.opsForZSet().score("zset", "c");
        System.out.println("score = " + score);

        // 6.增加/减少某元素的score值
        redisTemplate.opsForZSet().incrementScore("zset", "a", 10);
        redisTemplate.opsForZSet().incrementScore("zset", "c", -10);
    }

    @Test
    public void otherAPI() {
        // 1.对任意的RedisKey设置过期时间
        redisTemplate.expire("username", 10, TimeUnit.SECONDS);

        // 2.获取某元素的过期时间
        Long expire = redisTemplate.getExpire("zset");
        System.out.println("expire = " + expire);

        // 3.根据RedisKey删除某元素
        redisTemplate.delete("zset");
    }
}
