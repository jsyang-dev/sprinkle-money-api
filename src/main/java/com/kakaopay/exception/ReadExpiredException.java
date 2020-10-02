package com.kakaopay.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReadExpiredException extends SprinkleException {

  public ReadExpiredException(LocalDateTime createDate) {
    super(
        "뿌린지 7일이 지난 조회 요청은 처리할 수 없습니다. createDate: "
            + createDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }
}
