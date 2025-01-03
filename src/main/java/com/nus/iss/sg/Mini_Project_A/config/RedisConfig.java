package com.nus.iss.sg.Mini_Project_A.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.nus.iss.sg.Mini_Project_A.constants.Constants;
import com.nus.iss.sg.Mini_Project_A.model.CryptoData;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    @Value("${spring.data.redis.username:}")
    private String redisUsername; // Optional username

    @Value("${spring.data.redis.password:}")
    private String redisPassword; // Optional password

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration rsc = new RedisStandaloneConfiguration();
        rsc.setHostName(redisHost);
        rsc.setPort(redisPort);

        if (redisPassword != null && redisPassword.trim().length() > 0) {
            rsc.setPassword(redisPassword);
            if (redisUsername != null && redisUsername.trim().length() > 0) {
                rsc.setUsername(redisUsername);
            }
        }

        JedisClientConfiguration jcc = JedisClientConfiguration.builder().build();
        JedisConnectionFactory jcf = new JedisConnectionFactory(rsc, jcc);

        // Log Redis connection details
        System.out.println("Connecting to Redis at " + redisHost + ":" + redisPort);
        jcf.afterPropertiesSet();
        return jcf;
    }

    @Bean(Constants.template01)
    public RedisTemplate<String, Object> redisObjectTemplate2(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setEnableTransactionSupport(true); // Enable transaction support
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean(Constants.template02)
    public RedisTemplate<String, List<CryptoData>> cryptoDataRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, List<CryptoData>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
