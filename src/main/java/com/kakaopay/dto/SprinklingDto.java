package com.kakaopay.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Positive;

public class SprinklingDto {

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public static class Request {

    @Positive(message = "뿌릴 금액을 양수로 입력해주세요.")
    private long amount;

    @Positive(message = "뿌릴 인원을 양수로 입력해주세요.")
    private int people;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public static class Response extends RepresentationModel<Response> {

    private String token;
  }
}
