package com.kakaopay.exception;

public class DuplicateReceivingUserException extends SprinkleException {

  public DuplicateReceivingUserException(int userId) {
    super("동일 사용자가 한 뿌리기에서 두번 이상 받을 수 없습니다.\nuserId: " + userId);
  }
}
