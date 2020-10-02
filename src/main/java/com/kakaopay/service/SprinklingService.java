package com.kakaopay.service;

import com.kakaopay.dto.ReadDto.SprinklingDto;

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

  /**
   * 머니 뿌리기 조회
   *
   * @param token 뿌리기 token
   * @param userId 사용자 ID
   * @return 뿌리기 현재 상태
   */
  SprinklingDto read(String token, int userId);
}
