package iam.shoukou.redisexample.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RedissonTest {

    @Autowired
    RedissonClient redissonClient;

    @Test
    @DisplayName("description")
    void redissonTest() {
        // given
        String key = "redissonKey";

        // when
        redissonClient.getMap(key).put("email", "shoukou.lee@gmail.com");

        // then
        Object email = redissonClient.getMap(key).get("email");
        assertThat(email.toString()).isEqualTo("shoukou.lee@gmail.com");
        System.out.println("email.toString() = " + email.toString());
    }

}
