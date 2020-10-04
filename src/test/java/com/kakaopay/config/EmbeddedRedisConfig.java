package com.kakaopay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class EmbeddedRedisConfig {

  @Value("${spring.redis.port}")
  private int port;

  private RedisServer redisServer;

  @PostConstruct
  public void redisServer() {
    redisServer = new RedisServer(port);
    try {
      redisServer.start();
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
  }

  @PreDestroy
  public void stopRedis() {
    if (redisServer != null) {
      redisServer.stop();
    }
  }
}
