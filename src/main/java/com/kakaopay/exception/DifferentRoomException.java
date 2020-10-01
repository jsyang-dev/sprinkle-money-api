package com.kakaopay.exception;

public class DifferentRoomException extends SprinkleException {

  public DifferentRoomException(String roomId) {
    super("다른 대화방의 사용자가 받을 수 없습니다. roomId: " + roomId);
  }
}
