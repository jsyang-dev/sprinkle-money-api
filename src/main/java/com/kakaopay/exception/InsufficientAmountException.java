package com.kakaopay.exception;

public class InsufficientAmountException extends SprinkleException {
  public InsufficientAmountException(long amount, int people) {
    super("뿌린 금액이 요청한 인원수보다 적을 수 없습니다.\namount: " + amount + ", people: " + people);
  }
}
