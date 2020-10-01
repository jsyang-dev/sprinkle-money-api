package com.kakaopay.exception;

public class DuplicateSprinklingUserException extends SprinkleException {

  public DuplicateSprinklingUserException(int userId) {
    super("자신이 뿌린 건은 자신이 받을 수 없습니다. userId: " + userId);
  }
}
