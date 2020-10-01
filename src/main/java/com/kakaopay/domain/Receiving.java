package com.kakaopay.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Receiving extends BaseEntity {

  // 받은 금액
  @Column(nullable = false)
  private Long amount;

  // 받은 사용자
  @Column private Integer userId;

  // Lock 버전
  @Version private Integer version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sprinkling_id")
  private Sprinkling sprinkling;

  public boolean isNotReceived() {
    return userId == null;
  }
}
