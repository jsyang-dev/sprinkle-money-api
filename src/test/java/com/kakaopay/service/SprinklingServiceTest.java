package com.kakaopay.service;

import com.kakaopay.domain.Receiving;
import com.kakaopay.domain.Sprinkling;
import com.kakaopay.dto.ReadDto.SprinklingDto;
import com.kakaopay.exception.InsufficientAmountException;
import com.kakaopay.exception.PermissionDeniedException;
import com.kakaopay.exception.ReadExpiredException;
import com.kakaopay.repository.SprinklingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.kakaopay.constant.SprinklingConstant.EXPIRE_READ_SECONDS;
import static com.kakaopay.constant.SprinklingConstant.TOKEN_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class SprinklingServiceTest {

  @Autowired private SprinklingService sprinklingService;
  @Autowired private ReceivingService receivingService;
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

    assertThat(token).isNotNull().hasSize(TOKEN_LENGTH);
    assertThat(sprinkling.getAmount()).isEqualTo(amount);
    assertThat(sprinkling.getPeople()).isEqualTo(people);
    assertThat(sprinkling.getUserId()).isEqualTo(userId);
    assertThat(sprinkling.getRoomId()).isEqualTo(roomId);
  }

  @Test
  @DisplayName("뿌린 금액은 요청한 인원수에게 모든 금액이 분배됨")
  void sprinkleTest02() {

    // When
    String token = sprinklingService.sprinkle(amount, people, userId, roomId);

    // Then
    Sprinkling sprinkling =
        sprinklingRepository
            .findByToken(token)
            .orElseThrow(() -> new AssertionError("Test failed"));

    assertThat(sprinkling.getReceivings().size()).isEqualTo(people);
    assertThat(sprinkling.getReceivings().stream().map(Receiving::getAmount).reduce(0L, Long::sum))
        .isEqualTo(amount);
  }

  @Test
  @DisplayName("뿌린 금액이 요청한 인원수보다 적으면 예외 발생")
  void sprinkleTest03() {

    // Given
    long amount = 2;
    int people = 3;

    // When & Then
    assertThatThrownBy(() -> sprinklingService.sprinkle(amount, people, userId, roomId))
        .isInstanceOf(InsufficientAmountException.class)
        .hasMessageContaining("뿌린 금액이 요청한 인원수보다 적을 수 없습니다");
  }

  @Test
  @DisplayName("조회를 요청하고 뿌리기 상태를 반환 받음")
  void readTest01() {

    // Given
    int receivingUserId = 900002;
    String token = sprinklingService.sprinkle(amount, people, userId, roomId);
    long receivedAmount = receivingService.receive(token, receivingUserId, roomId);

    // When
    SprinklingDto sprinklingDto = sprinklingService.read(token, userId);

    // Then
    assertThat(sprinklingDto.getCreateDate()).isNotNull();
    assertThat(sprinklingDto.getTotalAmount()).isEqualTo(amount);
    assertThat(sprinklingDto.getReceivedAmount()).isEqualTo(receivedAmount);
    assertThat(sprinklingDto.getReceivingDtos()).hasSize(people);
  }

  @Test
  @DisplayName("뿌린 사용자 이외의 사용자가 조회하면 예외 발생")
  void readTest02() {

    // Given
    String token = sprinklingService.sprinkle(amount, people, userId, roomId);
    int otherUserId = 900002;

    // When & Then
    assertThatThrownBy(() -> sprinklingService.read(token, otherUserId))
        .isInstanceOf(PermissionDeniedException.class)
        .hasMessageContaining("뿌린 사용자 이외의 사용자가 조회할 수 없습니다")
        .hasMessageContaining(token);
  }

  @Test
  @DisplayName("뿌린지 7일이 지난 조회 요청은 예외 발생")
  @Transactional
  void readTest03() {

    // Given
    String token = sprinklingService.sprinkle(amount, people, userId, roomId);

    Sprinkling sprinkling =
        sprinklingRepository
            .findByToken(token)
            .orElseThrow(() -> new AssertionError("Test failed"));
    sprinkling.setCreateDate(LocalDateTime.now().minusSeconds(EXPIRE_READ_SECONDS + 1));

    // When & Then
    assertThatThrownBy(() -> sprinklingService.read(token, userId))
        .isInstanceOf(ReadExpiredException.class)
        .hasMessageContaining("뿌린지 7일이 지난 조회 요청은 처리할 수 없습니다");
  }
}
