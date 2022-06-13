package iam.shoukou.redisexample.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RedissonTest {

    @Autowired
    RedissonClient redissonClient;

    @Test
    @DisplayName("simple redisson hmap usage")
    void redissonTest() {
        // given
        String name = "redissonKey";

        // when
        redissonClient.getMap(name).put("email", "shoukou.lee@gmail.com");

        // then
        Object email = redissonClient.getMap(name).get("email");
        assertThat(email.toString()).isEqualTo("shoukou.lee@gmail.com");
        System.out.println("email.toString() = " + email.toString());
    }

    @Test
    @DisplayName("synchronization using redisson lock")
    void redissonLock() {
        String name = "name";
        String key = "key";
        redissonClient.getMap(name).put(key, 0);
        
        List<Thread> pool = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            pool.add(new Thread(() -> {
                increaseWithLock(name, key);
            }));
        }

        for (Thread thread : pool) {
            thread.start();
        }

        for (Thread thread : pool) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Integer expected = (Integer) redissonClient.getMap(name).get(key);

        assertThat(expected).isEqualTo(Integer.valueOf(100));
    }

    void increase(String name, Object key) {
        Integer integer = (Integer) redissonClient.getMap(name).get(key);
        redissonClient.getMap(name).put(key, integer + 1);
        System.out.println("Thread " + Thread.currentThread().getName() + ": " + integer + " -> " + (integer + 1));
    }

    void increaseWithLock(String name, Object key) {
        RLock lock = redissonClient.getLock("lock");

        try {
            /**
             * @param waitTime wait time 이후 락 취득 포기
             * @param leaseTime lease time 이후 자동 락 해제
             */
            if (!lock.tryLock(1000, 3000, TimeUnit.MILLISECONDS)) {
                return;
            }

            Integer integer = (Integer) redissonClient.getMap(name).get(key);
            redissonClient.getMap(name).put(key, integer + 1);
            System.out.println("Thread " + Thread.currentThread().getName() + ": " + integer + " -> " + (integer + 1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock != null && lock.isLocked()) {
                lock.unlock();
            }
        }
    }
}
