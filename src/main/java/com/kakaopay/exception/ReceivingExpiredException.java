package com.kakaopay.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReceivingExpiredException extends RuntimeException {

  public ReceivingExpiredException(LocalDateTime createDate) {
    super(
        "뿌린지 10분이 지난 요청은 받을 수 없습니다.\ncreateDate: "
            + createDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }
}
