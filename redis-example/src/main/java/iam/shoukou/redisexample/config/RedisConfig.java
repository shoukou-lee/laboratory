package iam.shoukou.redisexample.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    private final String REDIS_ADDR_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {

        String address = REDIS_ADDR_PREFIX + this.host + ":" + this.port;

        Config config = new Config();
        config.useSingleServer()
                .setAddress(address);

        return Redisson.create(config);
    }

    /** 라이브러리를 redisConnectionFactory에서 교체할 수 있음 */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedissonClient client) {
        // return new LettuceConnectionFactory(host, port);
        return new RedissonConnectionFactory(client);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedissonClient client) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        redisTemplate.setConnectionFactory(redisConnectionFactory(client));
        return redisTemplate;
    }

}
