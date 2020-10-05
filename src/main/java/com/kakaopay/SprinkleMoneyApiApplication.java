package com.kakaopay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SprinkleMoneyApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SprinkleMoneyApiApplication.class, args);
  }
}
