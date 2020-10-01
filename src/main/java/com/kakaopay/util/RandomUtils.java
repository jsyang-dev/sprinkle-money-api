package com.kakaopay.util;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

public class RandomUtils {

  public static final int TOKEN_LENGTH = 3;
  public static final int DECISION_NUMBER = 0;
  public static final int DECISION_UPPER_ALPHABET = 1;
  public static final int DECISION_LOWER_ALPHABET = 2;

  private RandomUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static String generateToken() {
    StringBuilder tokenBuilder = new StringBuilder();
    SecureRandom secureRandom = new SecureRandom();

    List<Integer> decisionInts = getRandomDecisionInt(secureRandom);

    for (Integer decisionInt : decisionInts) {
      if (decisionInt == DECISION_NUMBER) {
        tokenBuilder.append(getSingleNumber());
      } else if (decisionInt == DECISION_UPPER_ALPHABET) {
        tokenBuilder.append(getSingleUpperAlphabet());
      } else if (decisionInt == DECISION_LOWER_ALPHABET) {
        tokenBuilder.append(getSingleLowerAlphabet());
      }
    }

    return tokenBuilder.toString();
  }

  public static long generateRandomMoney(long remainAmount, long remainPeople) {
    long min = 1;
    long max = remainAmount - remainPeople + 1; // 한명당 최소 1원은 받을 수 있도록 금액을 남겨야 한다

    SecureRandom secureRandom = new SecureRandom();
    return secureRandom.longs(1, min, max).sum();
  }

  private static List<Integer> getRandomDecisionInt(SecureRandom secureRandom) {
    return secureRandom
        .ints(TOKEN_LENGTH, DECISION_NUMBER, DECISION_LOWER_ALPHABET + 1)
        .boxed()
        .collect(Collectors.toList());
  }

  private static StringBuilder getSingleNumber() {
    return getSingleRandomChar('0', '9');
  }

  private static StringBuilder getSingleUpperAlphabet() {
    return getSingleRandomChar('A', 'Z');
  }

  private static StringBuilder getSingleLowerAlphabet() {
    return getSingleRandomChar('a', 'z');
  }

  private static StringBuilder getSingleRandomChar(char min, char max) {
    SecureRandom secureRandom = new SecureRandom();
    return secureRandom
        .ints(1, min, max + 1)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append);
  }
}
