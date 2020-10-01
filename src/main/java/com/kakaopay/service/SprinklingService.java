package com.kakaopay.service;

public interface SprinklingService {

  /**
   * 머니 뿌리기
   *
   * @param amount 뿌린 금액
   * @param people 뿌린 인원
   * @param userId 뿌린 사용자 ID
   * @param roomId 뿌린 대화방 ID
   * @return 뿌리기 token
   */
  String sprinkle(long amount, int people, int userId, String roomId);
}
