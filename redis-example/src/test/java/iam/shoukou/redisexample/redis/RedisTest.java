package iam.shoukou.redisexample.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;

    @AfterEach
    void tearDown() {
        Set keys = redisTemplate.keys("*");

        for (Object key : keys) {
            System.out.println("delete key = " + key.toString());
            redisTemplate.delete(key);
        }
    }

    @Test
    @DisplayName("list push and pop")
    void redisListOperationsTest() {
        // given
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPush("key", "one");
        listOperations.rightPush("key", "two");
        listOperations.rightPush("key", "three");

        // when
        String one = listOperations.leftPop("key");
        String two = listOperations.leftPop("key");
        String three = listOperations.leftPop("key");

        // then
        assertThat(one).isEqualTo("one");
        assertThat(two).isEqualTo("two");
        assertThat(three).isEqualTo("three");
    }

    @Test
    @DisplayName("hash put and get")
    void redisHashOperationsTest() {
        // given
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put("key", "hashkey1", "one");
        hashOperations.put("key", "hashkey2", "two");
        hashOperations.put("key", "hashkey3", "three");

        String one = hashOperations.get("key", "hashkey1");
        String two = hashOperations.get("key", "hashkey2");
        String three = hashOperations.get("key", "hashkey3");

        assertThat(one).isEqualTo("one");
        assertThat(two).isEqualTo("two");
        assertThat(three).isEqualTo("three");

    }

}
