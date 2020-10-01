package com.kakaopay.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SprinklingReqDto {

  @Positive(message = "뿌릴 금액을 양수로 입력해주세요.")
  private long amount;

  @Positive(message = "뿌릴 인원을 양수로 입력해주세요.")
  private int people;
}
