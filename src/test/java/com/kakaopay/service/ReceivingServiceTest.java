package com.kakaopay.service;

import com.kakaopay.exception.DifferentRoomException;
import com.kakaopay.exception.DuplicateReceivingUserException;
import com.kakaopay.exception.DuplicateSprinklingUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class ReceivingServiceTest {

  @Autowired private SprinklingService sprinklingService;
  @Autowired private ReceivingService receivingService;

  private long amount;
  private int people;
  private int userId;
  private String roomId;
  private String token;

  @BeforeEach
  void setUp() {
    amount = 20000;
    people = 3;
    userId = 900001;
    roomId = "TEST-ROOM";
    token = sprinklingService.sprinkle(amount, people, userId, roomId);
  }

  @Test
  @DisplayName("받기를 요청하고 받은 금액을 반환 받음")
  void receivingTest01() {

    // Given
    int receivingUserId = 900002;

    // When
    long receivedAmount = receivingService.receive(token, receivingUserId, roomId);

    // Then
    assertThat(receivedAmount).isLessThanOrEqualTo(amount);
  }

  @Test
  @DisplayName("동일 사용자가 한 뿌리기에서 두번 이상 받으면 예외 발생")
  void receivingTest02() {

    // Given
    int receivingUserId = 900002;
    receivingService.receive(token, receivingUserId, roomId);

    // When & Then
    assertThatThrownBy(() -> receivingService.receive(token, receivingUserId, roomId))
        .isInstanceOf(DuplicateReceivingUserException.class)
        .hasMessageContaining("동일 사용자가 한 뿌리기에서 두번 이상 받을 수 없습니다")
        .hasMessageContaining(String.valueOf(receivingUserId));
  }

  @Test
  @DisplayName("자신이 뿌린 건을 자신이 받으면 예외 발생")
  void receivingTest03() {

    // When & Then
    assertThatThrownBy(() -> receivingService.receive(token, userId, roomId))
        .isInstanceOf(DuplicateSprinklingUserException.class)
        .hasMessageContaining("자신이 뿌린 건은 자신이 받을 수 없습니다")
        .hasMessageContaining(String.valueOf(userId));
  }

  @Test
  @DisplayName("다른 대화방의 사용자가 받으면 예외 발생")
  void receivingTest04() {

    // Given
    int receivingUserId = 900002;
    String receivingRoomId = "OTHER-ROOM";

    // When & Then
    assertThatThrownBy(() -> receivingService.receive(token, receivingUserId, receivingRoomId))
        .isInstanceOf(DifferentRoomException.class)
        .hasMessageContaining("다른 대화방의 사용자가 받을 수 없습니다")
        .hasMessageContaining(receivingRoomId);
  }

  @Test
  @DisplayName("뿌린지 10분이 지난 요청은 예외 발생")
  void receivingTest05() {}

  @Test
  @DisplayName("미할당 건이 없으면 예외 발생")
  void receivingTest06() {}
}
