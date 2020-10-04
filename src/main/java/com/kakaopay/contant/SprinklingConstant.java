package com.kakaopay.contant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SprinklingConstant {

  public static final int TOKEN_LENGTH = 3;
  public static final int DECISION_NUMBER_TYPE = 0;
  public static final int DECISION_UPPER_ALPHABET_TYPE = 1;
  public static final int DECISION_LOWER_ALPHABET_TYPE = 2;

  public static final long EXPIRE_RECEIVING_SECONDS = 60L * 10L; // 받기 유효 시간: 10분
  public static final long EXPIRE_READ_SECONDS = 60L * 60L * 24L * 7L; // 조회 유효 시간: 7일
}
