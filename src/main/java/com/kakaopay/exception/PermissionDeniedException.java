package com.kakaopay.exception;

public class PermissionDeniedException extends SprinkleException {

  public PermissionDeniedException(String token) {
    super("뿌린 사용자 이외의 사용자가 조회할 수 없습니다. token: " + token);
  }
}
