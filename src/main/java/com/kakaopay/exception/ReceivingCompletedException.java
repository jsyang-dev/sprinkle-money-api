package com.kakaopay.exception;

public class ReceivingCompletedException extends SprinkleException {

  public ReceivingCompletedException(String token) {
    super("받기가 마감되어 받을 수 없습니다. token: " + token);
  }
}
