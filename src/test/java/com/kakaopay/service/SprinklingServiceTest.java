package com.kakaopay.service;

import com.kakaopay.domain.Sprinkling;
import com.kakaopay.repository.SprinklingRepository;
import com.kakaopay.util.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SprinklingServiceTest {

  @Autowired private SprinklingService sprinklingService;
  @Autowired private SprinklingRepository sprinklingRepository;

  private long amount;
  private int people;
  private int userId;
  private String roomId;

  @BeforeEach
  void setUp() {
    amount = 20000;
    people = 3;
    userId = 900001;
    roomId = "TEST-ROOM";
  }

  @Test
  @DisplayName("뿌리기를 요청하고 token을 반환 받음")
  void sprinkleTest01() {

    // When
    String token = sprinklingService.sprinkle(amount, people, userId, roomId);

    // Then
    Sprinkling sprinkling =
        sprinklingRepository
            .findByToken(token)
            .orElseThrow(() -> new AssertionError("Test failed"));

    assertThat(token).isNotNull().hasSize(RandomUtils.TOKEN_LENGTH);
    assertThat(sprinkling.getAmount()).isEqualTo(amount);
    assertThat(sprinkling.getPeople()).isEqualTo(people);
    assertThat(sprinkling.getUserId()).isEqualTo(userId);
    assertThat(sprinkling.getRoomId()).isEqualTo(roomId);
  }

  @Test
  @DisplayName("뿌린 금액은 요청한 인원수에게 모든 금액이 분배됨")
  void sprinkleTest02() {}

  @Test
  @DisplayName("뿌린 금액이 요청한 인원수보다 작으면 예외 발생")
  void sprinkleTest03() {}
}
