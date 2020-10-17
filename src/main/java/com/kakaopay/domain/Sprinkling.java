package com.kakaopay.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kakaopay.constant.SprinklingConstant.EXPIRE_READ_SECONDS;
import static com.kakaopay.constant.SprinklingConstant.EXPIRE_RECEIVING_SECONDS;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sprinkling extends BaseEntity {

  // token
  @Column(unique = true, nullable = false, length = 3)
  private String token;

  // 뿌린 금액
  @Column(nullable = false)
  private Long amount;

  // 뿌린 인원
  @Column(nullable = false)
  private Integer people;

  // 뿌린 사용자
  @Column(nullable = false)
  private Integer userId;

  // 대화방
  @Column(nullable = false)
  private String roomId;

  @OneToMany(mappedBy = "sprinkling", cascade = CascadeType.PERSIST)
  @Builder.Default
  List<Receiving> receivings = new ArrayList<>();

  public void addReceiving(Receiving receiving) {
    receivings.add(receiving);
    receiving.setSprinkling(this);
  }

  public boolean isReceivingUserDuplicated(int userId) {
    return receivings.stream()
        .map(receiving -> Optional.ofNullable(receiving.getUserId()).orElse(0))
        .anyMatch(receiving -> receiving == userId);
  }

  public boolean isSprinklingUserDuplicated(int userId) {
    return this.userId == userId;
  }

  public boolean isDifferentRoom(String roomId) {
    return !roomId.equals(this.roomId);
  }

  public boolean isReceivingExpired() {
    long secondsGap = Duration.between(super.getCreateDate(), LocalDateTime.now()).getSeconds();
    return secondsGap > EXPIRE_RECEIVING_SECONDS;
  }

  public boolean isPermissionDenied(int userId) {
    return this.userId != userId;
  }

  public boolean isReadExpired() {
    long secondsGap = Duration.between(super.getCreateDate(), LocalDateTime.now()).getSeconds();
    return secondsGap > EXPIRE_READ_SECONDS;
  }
}
