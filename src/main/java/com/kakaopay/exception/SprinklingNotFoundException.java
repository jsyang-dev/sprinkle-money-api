package com.kakaopay.exception;

public class SprinklingNotFoundException extends SprinkleException {

  public SprinklingNotFoundException(String token) {
    super("유효한 뿌리기 건이 존재하지 않습니다. token: " + token);
  }
}
