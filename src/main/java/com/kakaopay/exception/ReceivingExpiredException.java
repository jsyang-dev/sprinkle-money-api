package com.kakaopay.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReceivingExpiredException extends SprinkleException {

  public ReceivingExpiredException(LocalDateTime createDate) {
    super(
        "뿌린지 10분이 지난 받기 요청은 처리할 수 없습니다. createDate: "
            + createDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }
}
